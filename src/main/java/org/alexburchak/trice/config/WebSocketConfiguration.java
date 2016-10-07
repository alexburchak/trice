package org.alexburchak.trice.config;

import org.alexburchak.trice.ws.TriceWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author alexburchak
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(triceWebSocketHandler(), "/sockjs")
                .withSockJS();
    }

    @Bean
    public WebSocketHandler triceWebSocketHandler() {
        return new TriceWebSocketHandler();
    }
}
