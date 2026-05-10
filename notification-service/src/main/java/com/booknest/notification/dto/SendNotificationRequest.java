package com.booknest.notification.dto;

import lombok.Data;

@Data
public class SendNotificationRequest {
    private Long userId;
    private String type;
    private String message;
}
