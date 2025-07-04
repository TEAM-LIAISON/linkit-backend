package liaison.linkit.notification.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @MemberOnly
    @Logging(item = "Notification", action = "GET_NOTIFICATION_ITEMS")
    public CommonResponse<NotificationResponseDTO.NotificationItems> getNotificationItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                notificationService.getNotificationItems(accessor.getMemberId()));
    }

    @GetMapping("/notification/header")
    @MemberOnly
    @Logging(item = "Notification", action = "GET_NOTIFICATION_COUNT")
    public CommonResponse<NotificationResponseDTO.NotificationCountResponse> getNotificationCount(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                notificationService.getNotificationCount(accessor.getMemberId()));
    }

    @PostMapping("/notification/read/{notificationId}")
    @MemberOnly
    @Logging(item = "Notification", action = "POST_READ_NOTIFICATION")
    public CommonResponse<NotificationResponseDTO.ReadNotificationResponse> readNotification(
            @Auth final Accessor accessor, @PathVariable final String notificationId) {
        return CommonResponse.onSuccess(
                notificationService.readNotification(accessor.getMemberId(), notificationId));
    }
}
