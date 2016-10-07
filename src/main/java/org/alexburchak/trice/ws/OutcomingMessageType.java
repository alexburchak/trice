package org.alexburchak.trice.ws;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

/**
 * @author alexburchak
 */
public enum OutcomingMessageType {
    /**
     * Success, as reply to {@link IncomingMessageType#TRICE}
     */
    TRICE,
    /**
     * SID is expired or does not exist, as reply to {@link IncomingMessageType#TRICE}
     */
    EXPIRED,
    /**
     * HTTP Request dump message
     */
    REQUEST,
    /**
     * Say bye
     */
    ECIRT;

    public WebSocketMessage<String> format(String message) {
        return new TextMessage(name() + (message != null ? " " + message : ""));
    }
}
