package org.alexburchak.trice.ws;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.alexburchak.trice.config.TriceConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;

/**
 * @author alexburchak
 */
@Slf4j
public class TriceWebSocketHandler extends TextWebSocketHandler {
    private static final String SESSION_ATTRIBUTE_CHANNEL = "TRICE_CHANNEL";
    private static final String SESSION_ATTRIBUTE_CONSUMER_TAG = "TRICE_CONSUMER_TAG";

    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private TriceConfiguration triceConfiguration;
    @Autowired
    private ConnectionFactory connectionFactory;

    private Connection connection;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        log.debug("Established WebSocket connection for session {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        log.debug("Closing WebSocket session {} on connection close", session.getId());

        Channel channel = (Channel) session.getAttributes().get(SESSION_ATTRIBUTE_CHANNEL);
        if (channel != null) {
            String consumerTag = (String) session.getAttributes().get(SESSION_ATTRIBUTE_CONSUMER_TAG);

            if (consumerTag != null) {
                log.debug("Closing AMQP consumer {}", consumerTag);

                RabbitUtils.closeMessageConsumer(channel, Collections.singleton(consumerTag), false);
            }

            log.debug("Closing AMQP channel {}", channel.getChannelNumber());

            RabbitUtils.closeChannel(channel);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();

        IncomingMessageType.handle(payload, new IncomingMessageTypeVisitor() {
            @Override
            public void visitTrice(String sid) throws IOException {
                onTrice(session, sid);
            }

            @Override
            public void visitEcirt(String unused) {
                onEcirt(session);
            }

            @Override
            public void visitUnknown(String payload) {
                onUnknown(payload);
            }
        });
    }

    private void onTrice(WebSocketSession session, String sid) throws IOException {
        log.debug("Initializing WebSocket session {} for sid {}", session.getId(), sid);

        String queueName = triceConfiguration.getQueueNamePrefix() + sid;

        if (amqpAdmin.getQueueProperties(queueName) == null) {
            try {
                session.sendMessage(OutcomingMessageType.EXPIRED.format(null));
            } catch (IOException e) {
                log.error("Failed to send {} message", OutcomingMessageType.EXPIRED, e);
                throw e;
            }
            return;
        }

        session.sendMessage(OutcomingMessageType.TRICE.format(sid));

        Channel channel = getConnection().createChannel(false);
        log.debug("Created new AMQP channel {}", channel.getChannelNumber());

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.debug("Received AMQP message {}", message);

                session.sendMessage(OutcomingMessageType.REQUEST.format(message));
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                log.debug("Received shutdown signal for AMQP consumer tag {}", consumerTag);

                super.handleShutdownSignal(consumerTag, sig);

                try {
                    session.sendMessage(OutcomingMessageType.ECIRT.format(null));
                } catch (IOException e) {
                    log.error("Failed to send {} message to WebSocket session {}", IncomingMessageType.ECIRT.name(), session.getId());
                } finally {
                    IOUtils.closeQuietly(session);

                    RabbitUtils.closeChannel(channel);
                }
            }
        };

        session.getAttributes().put(SESSION_ATTRIBUTE_CHANNEL, channel);

        try {
            String consumerTag = channel.basicConsume(queueName, true, consumer);

            log.debug("Registered AMQP consumer {}", consumerTag);

            session.getAttributes().put(SESSION_ATTRIBUTE_CONSUMER_TAG, consumerTag);
        } catch (IOException e) {
            log.error("Failed to register AMQP consumer for queue {}", queueName);
            throw e;
        }
    }

    private void onEcirt(WebSocketSession session) {
        log.debug("Closing WebSocket session {}", session.getId());

        IOUtils.closeQuietly(session);
    }

    private void onUnknown(String payload) {
        log.error("Unknown message {}", payload);
    }

    public synchronized Connection getConnection() {
        if (connection != null && connection.isOpen()) {
            return connection;
        }

        connection = connectionFactory.createConnection();
        return connection;
    }
}
