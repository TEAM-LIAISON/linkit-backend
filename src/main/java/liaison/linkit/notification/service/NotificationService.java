package liaison.linkit.notification.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import liaison.linkit.member.domain.Member;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.repository.emitter.EmitterRepository;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    // 연결 유지 시간 (30분)
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 30;

    /**
     * SSE 구독(연결) 로직
     */
    public SseEmitter subscribe(Long memberId, String lastEventId) throws BadRequestException {
        // Emitter 고유 ID 생성
        String emitterId = memberId + "_" + System.currentTimeMillis();
        // Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // EmitterRepository에 등록 (메모리나 Redis 등에서 관리)
        emitterRepository.save(emitterId, emitter);

        // 연결 종료 및 타임아웃 시점에 Repository에서 제거
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // SSE 연결 직후 에러 방지를 위한 더미 이벤트 전송
        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        // lastEventId가 있는 경우, 유실된 이벤트(캐시) 재전송
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> {
                        try {
                            sendToClient(emitter, entry.getKey(), entry.getValue());
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        return emitter;
    }

    /**
     * 알림 전송 로직
     */
    public void send(Member member,
                     NotificationType notificationType,
                     String content,
                     String url,
                     String toName) {

        // 1) Notification 생성 및 MongoDB 저장
        Notification notification = createNotification(member, notificationType, content, url, toName);
        Notification savedNotification = notificationRepository.save(notification);

        // 2) 해당 memberId로 시작하는 Emitter들을 가져와 알림 전송
        String memberIdKey = String.valueOf(member.getId());
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberIdKey);

        // 3) 각 Emitter에 데이터 전송
        sseEmitters.forEach((key, emitter) -> {
            // 캐시에 이벤트 데이터 저장 (재연결 시 재전송 가능)
            emitterRepository.saveEventCache(key, savedNotification);

            try {
                // 실제 SSE 전송
                sendToClient(emitter, key, savedNotification);
            } catch (BadRequestException e) {
                log.error("[SSE SEND ERROR] EmitterId={}, cause={}", key, e.getMessage());
                // 필요 시 예외처리 로직
            }
        });
    }


    /**
     * Notification 생성 (MongoDB Document)
     */
    private Notification createNotification(
            Member member,
            NotificationType notificationType,
            String content,
            String url,
            String toName
    ) {
        // 여기서는 간단히 공통 필드만 저장 예시
        return Notification.builder()
                .notificationType(notificationType)
                .notificationStatus(NotificationStatus.PENDING) // 임의로 설정
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    /**
     * SSE Emitter로 데이터 전송
     */
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) throws BadRequestException {
        try {
            emitter.send(
                    SseEmitter.event()
                            .id(emitterId)
                            .data(data)
            );
        } catch (IOException e) {
            // 전송 실패 시 EmitterRepository에서 삭제
            emitterRepository.deleteById(emitterId);
            throw new BadRequestException("SSE 전송 실패: " + e.getMessage());
        }
    }
}
