package com.pay.message_push.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay.message_push.model.MessageDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    /**
     * 存储在线用户的 Session，Key 为 userId
     */
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            // 检查用户是否已经在线
            if (SESSIONS.containsKey(userId)) {
                // 用户已在线，拒绝新连接并发送提示信息
                System.out.println("Connection rejected: User " + userId + " is already connected");
                session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"User " + userId + " is already connected\"}"));
                session.close(CloseStatus.GOING_AWAY);
                return;
            }
            
            SESSIONS.put(userId, session);
            System.out.println("User connected: " + userId + ", Session ID: " + session.getId());
        } else {
            System.err.println("Connection rejected: No userId found");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 简单的心跳或空消息处理
        if (payload.isEmpty()) {
            return;
        }

        try {
            MessageDTO msg = objectMapper.readValue(payload, MessageDTO.class);
            
            // 补全发送者信息
            String fromId = (String) session.getAttributes().get("userId");
            msg.setFromId(fromId);
            msg.setTimestamp(System.currentTimeMillis());

            String toId = msg.getToId();
            
            // 路由逻辑
            if (toId != null && SESSIONS.containsKey(toId)) {
                WebSocketSession targetSession = SESSIONS.get(toId);
                if (targetSession.isOpen()) {
                    String forwardJson = objectMapper.writeValueAsString(msg);
                    targetSession.sendMessage(new TextMessage(forwardJson));
                    System.out.println("Message routed from " + fromId + " to " + toId);
                } else {
                    SESSIONS.remove(toId);
                    System.out.println("Target user " + toId + " session is closed.");
                    // 通知发送者目标离线
                    notifySenderOffline(session, toId);
                }
            } else {
                System.out.println("User " + toId + " is not online. Message dropped.");
                // 通知发送者目标离线
                notifySenderOffline(session, toId);
            }

        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            SESSIONS.remove(userId);
            System.out.println("User disconnected: " + userId);
        }
    }

    /**
     * 通知发送者目标用户离线
     * @param senderSession 发送者会话
     * @param targetId 目标用户ID
     */
    private void notifySenderOffline(WebSocketSession senderSession, String targetId) {
        try {
            MessageDTO offlineNotice = MessageDTO.builder()
                    .type("ERROR")
                    .fromId(targetId)
                    .toId((String) senderSession.getAttributes().get("userId"))
                    .content("User " + targetId + " is offline")
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            String noticeJson = objectMapper.writeValueAsString(offlineNotice);
            senderSession.sendMessage(new TextMessage(noticeJson));
            System.out.println("Notified sender that target user " + targetId + " is offline");
        } catch (Exception e) {
            System.err.println("Failed to notify sender about offline target: " + e.getMessage());
            e.printStackTrace();
        }
    }
}