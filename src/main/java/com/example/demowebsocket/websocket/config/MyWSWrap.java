package com.example.demowebsocket.websocket.config;

import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class MyWSWrap implements WebSocketHandler {

        private final WebSocketHandler delegate;


        public MyWSWrap(WebSocketHandler delegate) {
            Assert.notNull(delegate, "Delegate must not be null");
            this.delegate = delegate;
        }


        public WebSocketHandler getDelegate() {
            return this.delegate;
        }

        public WebSocketHandler getLastHandler() {
            WebSocketHandler result = this.delegate;
            while (result instanceof org.springframework.web.socket.handler.WebSocketHandlerDecorator) {
                result = ((org.springframework.web.socket.handler.WebSocketHandlerDecorator) result).getDelegate();
            }
            return result;
        }

        public static WebSocketHandler unwrap(WebSocketHandler handler) {
            if (handler instanceof org.springframework.web.socket.handler.WebSocketHandlerDecorator) {
                return ((org.springframework.web.socket.handler.WebSocketHandlerDecorator) handler).getLastHandler();
            }
            else {
                return handler;
            }
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            this.delegate.afterConnectionEstablished(session);
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            this.delegate.handleMessage(session, message);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            this.delegate.handleTransportError(session, exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            this.delegate.afterConnectionClosed(session, closeStatus);
        }

        @Override
        public boolean supportsPartialMessages() {
            return this.delegate.supportsPartialMessages();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
        }


}
