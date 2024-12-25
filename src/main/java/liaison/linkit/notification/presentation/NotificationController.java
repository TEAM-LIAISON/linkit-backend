package liaison.linkit.notification.presentation;

import java.util.concurrent.ConcurrentHashMap;
import liaison.linkit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 연결 생성
     *
     * @param memberId    SSE를 구독하는 사용자 ID
     * @param lastEventId 재연결 시 마지막 이벤트 ID(없으면 "")
     */

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SseEmitter> connect(
            @RequestParam("memberId") Long memberId,
            @RequestParam(name = "lastEventId", defaultValue = "") String lastEventId
    ) throws BadRequestException {
        log.info("[SSE CONNECT] memberId={}, lastEventId={}", memberId, lastEventId);

        // SSE Emitter 생성 후 반환
        SseEmitter emitter = notificationService.subscribe(memberId, lastEventId);

        return ResponseEntity.ok(emitter);
    }
}
