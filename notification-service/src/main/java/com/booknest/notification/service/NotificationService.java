package com.booknest.notification.service;

import com.booknest.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Long userId, String type, String message);

    List<Notification> getByUser(Long userId);

    List<Notification> getUnreadByUser(Long userId);

    long getUnreadCount(Long userId);

    Notification markAsRead(Long notificationId);

    void markAllRead(Long userId);

    void deleteNotification(Long notificationId);

    List<Notification> getAllNotifications();

    List<Notification> getByType(String type);
}
