package com.pay.message_push.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    /**
     * 消息类型: CONNECT, CHAT, ERROR
     */
    private String type;
    private String fromId;
    private String toId;
    private String content;
    private Long timestamp;
}
