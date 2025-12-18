package com.pay.message_push.config;

import com.pay.message_push.handler.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 映射路径 /chat/{userId}
        // setAllowedOrigins("*") 允许跨域，方便本地测试
        registry.addHandler(chatHandler, "/chat/{userId}")
                .addInterceptors(new UserIdHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

    /**
     * 握手拦截器，用于从 URL 中获取 userId 并放入 Session 属性
     */
    private static class UserIdHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            // 获取 URI 路径，例如 /chat/1001
            String path = request.getURI().getPath();
            
            // 简单解析 userId，假设路径最后一段是 userId
            String[] parts = path.split("/");
            if (parts.length > 0) {
                String userId = parts[parts.length - 1];
                if (userId != null && !userId.isEmpty()) {
                    attributes.put("userId", userId);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
            // 握手后的处理，此处无需操作
        }
    }
}
