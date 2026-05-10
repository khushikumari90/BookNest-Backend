package com.booknest.notification.service;

import com.booknest.notification.entity.Notification;
import com.booknest.notification.exception.NotificationNotFoundException;
import com.booknest.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification sendNotification(Long userId, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Override
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found with id: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndIsRead(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotificationNotFoundException(
                    "Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getByType(String type) {
        return notificationRepository.findByType(type);
    }
}
