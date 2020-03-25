package com.example.demowebsocket.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // ⭐ -----  创建Stomp节点，并指定到url上
    // ⭐ -----  可以添加多个节点
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/test").setAllowedOrigins("*").withSockJS();

    }


//     ⭐ ----
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry){
//         1.配置消息大小限制 2.配置发送信息时限 3.配置发送数据缓冲大小 4.首次连接成功，第一次发送消息的时限（超时可能断开连接）
//         5.配置 Handler的装饰器
//         1~4 都比较好理解，这里做一个 Handler 的例子
//                 WebSocketHandlerDecoratorFactory 接口里面写明了： 必须使用WebSocketHandlerDecorator的子类进行装饰
//        registry.

        registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        // 客户端与服务器端建立连接后，此处记录谁上线了
//                        String username = session.getPrincipal().getName();
                        //log.info("online: " + username);
//                        System.out.println("online: " + username);
                        System.out.println("建立连接");
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        // 客户端与服务器端断开连接后，此处记录谁下线了
//                        String username = session.getPrincipal().getName();
                        // log.info("offline: " + username);
//                        System.out.println("offline: " + username);
                        System.out.println("断开丽娜姐");
                        super.afterConnectionClosed(session, closeStatus);
                    }
                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        System.out.println("**************************8");
                        super.handleMessage(session,message);
                    }

                };
            }
        });
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        // 配置消息代理的一些参数

        // 1.前端前缀
        config.enableSimpleBroker("/back","/auth");

        // 2.用户端向服务器发送信息的时候，要带上 /front 这个前缀
        config.setApplicationDestinationPrefixes("/front");

        // 一对一前缀 auth
        config.setUserDestinationPrefix("/auth");


    }
}
