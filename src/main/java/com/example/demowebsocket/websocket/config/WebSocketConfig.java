package com.example.demowebsocket.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

import java.util.List;

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
                        System.out.println("断开");
                        super.afterConnectionClosed(session, closeStatus);
                    }
                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        System.out.println("**************************8");
                        System.out.println(message);
                        /**
                         * ⭐ 我最初想在 handleMessage 里面对数据进行预处理，但是handleMessage中的参数 WebSocketMessage 实际上
                         * ⭐ 是未经过 STOMP 协议解析的消息，在这里处理，需要自行 解码/编码 STOMP协议数据，所以放弃在这里进行处理
                         * */
                        super.handleMessage(session,message);
                    }

                };
            }
        });
    }
    /**
     * Inbound 和 OutBound 配置是一样的
     *
     * In/Out Channel的理解要和WebSocket 收发区分一下，这里的 In/Out 主要还是说明 Channel
     *
     * 对应理解是：向同一个后端地址发送数据，就是像同一个 inChannel
     *            订阅了同一个后端地址的客户端，服务端用同一个 outChannel 发送数据
     *
     * 有一个简单的证明：开启三个前端，订阅同一个后端，后端给前端反馈一个数据的时候，下面 outBound Channel 会打印三次
     * */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        /**
         * 猜测：基于 TaskExecutor 异步体系，配置Channel,这套我在写 爬虫下载代码的时候用过
         * */
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setAllowCoreThreadTimeOut(false); // 是否允许触发超时,
        executor.setCorePoolSize(2); // 线程池大小 core
        executor.setKeepAliveSeconds(60);// 空闲线程多久被关闭：如果线程池中现有现成数量超过 core number，当某个现成空闲超过设定时间，就会被关闭
        executor.setMaxPoolSize(3);// 线程池最大大小
        executor.setQueueCapacity(Integer.MAX_VALUE);// 任务队列大小
//        executor.setTaskDecorator(); task装饰器，非必要
        registration.taskExecutor(executor);


        /**
         * ChannelInterceptor 也是在发送/接收信息前后 可以做一些操作，因为可以获取到 message 和 channel 两个参数，所以可以对channel级别做一些统一操作
         * */
        ChannelInterceptor interceptor = new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("inbound  Channel Interceptor preSend");
                return message;
            }

        };
        registration.interceptors(interceptor);

    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        ChannelInterceptor interceptor = new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("outbound Channel Interceptor preSend");
                return message;
            }
        };
        registration.interceptors(interceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        HandlerMethodArgumentResolver resolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                Class<?> paramType = parameter.getParameterType();
                return parameter.hasParameterAnnotation(MessageMap.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {

                return null;
            }
        };
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
