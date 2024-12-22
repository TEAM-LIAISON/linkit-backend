//package liaison.linkit.chat.presentation;
//
//import liaison.linkit.chat.domain.MessageKafka;
//import liaison.linkit.chat.service.ChatMessageService;
//import liaison.linkit.global.util.KafkaMessageProducer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
/// **
// * 채팅 관련 REST API 및 WebSocket 메시지 처리를 담당하는 컨트롤러 클래스
// */
//@RestController
//@RequiredArgsConstructor
//public class ChattingController {
//
//    private final KafkaMessageProducer messageProducer; // Kafka 메시지를 전송하는 프로듀서 클래스
//    private final ChatMessageService chatMessageService; // 채팅 메시지 데이터를 처리하는 서비스 클래스
//
//    /**
//     * REST API를 통해 메시지를 전송하는 엔드포인트
//     *
//     * @param message 전송할 메시지 정보 (JSON 포맷)
//     * @return 메시지 전송 성공 응답
//     */
//    @PostMapping("/send")
//    public ResponseEntity<String> sendMessage(@RequestBody MessageKafka message) {
//        // Kafka 프로듀서를 통해 메시지를 지정된 토픽으로 전송
//        messageProducer.sendMessage(message, "chat-room-" + message.getChatNo());
//        return ResponseEntity.ok("Message sent"); // 전송 성공 응답
//    }
//
//    /**
//     * WebSocket을 통해 들어오는 메시지를 처리하고, 구독 중인 클라이언트에게 브로드캐스트
//     *
//     * @param message WebSocket을 통해 받은 메시지
//     * @return 브로드캐스트될 메시지
//     */
//    @MessageMapping("/message") // 클라이언트에서 /app/message 경로로 전송된 메시지를 처리
//    @SendTo("/topic/chat-room/{chatRoomId}") // 지정된 경로로 메시지를 브로드캐스트
//    public MessageKafka broadcastMessage(@Payload MessageKafka message) {
//        // 메시지를 데이터베이스에 저장
////        chatMessageService.saveMessage(message);
//        // 저장된 메시지를 구독 중인 클라이언트들에게 전송
//        return message;
//    }
//}
