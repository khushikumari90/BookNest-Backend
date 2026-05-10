package com.booknest.notification.controller;

import com.booknest.notification.dto.SendNotificationRequest;
import com.booknest.notification.entity.Notification;
import com.booknest.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "In-app notification dispatch, read/unread state, badge count, and admin broadcast")
@SecurityRequirement(name = "BearerAuth")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Send a notification to a user",
               description = "Dispatches an in-app notification. Triggered by order events, payment confirmations, etc.\n\n" +
                             "Body: `{ \"userId\": 1, \"type\": \"ORDER_PLACED\", \"message\": \"Your order has been placed!\" }`\n\n" +
                             "Notification types: `ORDER_PLACED`, `ORDER_CONFIRMED`, `ORDER_DISPATCHED`, `ORDER_DELIVERED`, `PAYMENT_SUCCESS`, `LOW_STOCK`")
    @ApiResponse(responseCode = "201", description = "Notification sent", content = @Content(schema = @Schema(implementation = Notification.class)))
    @PostMapping("/send")
    public ResponseEntity<Notification> send(@RequestBody SendNotificationRequest req) {
        return new ResponseEntity<>(
                notificationService.sendNotification(req.getUserId(), req.getType(), req.getMessage()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Get all notifications for a user",
               description = "Returns all notifications (read and unread) for the user, ordered by most recent.")
    @ApiResponse(responseCode = "200", description = "All notifications for the user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @Operation(summary = "Get unread notifications for a user",
               description = "Returns only unread notifications. Shown in the notification dropdown.")
    @ApiResponse(responseCode = "200", description = "Unread notifications list")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadByUser(userId));
    }

    @Operation(summary = "Get unread notification count (badge)",
               description = "Returns the count of unread notifications. Used to render the bell icon badge number.")
    @ApiResponse(responseCode = "200", description = "Unread count as long")
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Mark a notification as read",
               description = "Updates isRead = true for a single notification.")
    @ApiResponse(responseCode = "200", description = "Notification marked read", content = @Content(schema = @Schema(implementation = Notification.class)))
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(
            @Parameter(description = "Notification ID", example = "1") @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    @Operation(summary = "Mark all notifications as read",
               description = "Bulk marks all notifications for a user as read. Called when user opens the notification centre.")
    @ApiResponse(responseCode = "200", description = "All notifications marked read")
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllRead(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok("All notifications marked as read for userId: " + userId);
    }

    @Operation(summary = "Delete a notification",
               description = "Permanently removes a notification from the user's notification centre.")
    @ApiResponse(responseCode = "200", description = "Notification deleted")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> delete(
            @Parameter(description = "Notification ID", example = "1") @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted.");
    }

    @Operation(summary = "Get all notifications (Admin)", description = "Returns every notification in the system. Admin only.")
    @ApiResponse(responseCode = "200", description = "All notifications")
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @Operation(summary = "Get notifications by type",
               description = "Filters notifications by event type. E.g., `ORDER_PLACED`, `PAYMENT_SUCCESS`")
    @ApiResponse(responseCode = "200", description = "Filtered notifications")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getByType(
            @Parameter(description = "Notification type", example = "ORDER_PLACED") @PathVariable String type) {
        return ResponseEntity.ok(notificationService.getByType(type));
    }
}
