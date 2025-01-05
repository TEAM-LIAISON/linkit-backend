//package liaison.linkit.chat.domain;
//
//import static lombok.AccessLevel.PROTECTED;
//
//import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import lombok.Builder;
//
//@Document(collection = "chat_messages")
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor(access = PROTECTED)
//public class ChatMessage {
//
//    @Id
//    private String id;
//
//    // 메시지가 속한 채팅방 ID
//    private Long chatRoomId;
//
//    // 실제 메시지를 보낸 회원의 ID
//    private Long senderId;
//
//    // 메시지 발신자 유형 (PROFILE 또는 TEAM)
//    private ParticipantType senderType;
//
//    // 발신자 엔티티 ID (Profile인 경우 memberId, Team인 경우 teamId)
//    private Long senderEntityId;
//
//    // 메시지 내용
//    private String content;
//
//    // 메시지 전송 시간
//    private LocalDateTime timestamp;
//
//    // 메시지 읽음 여부
//    private boolean isRead;
//
//    /**
//     * 채팅 메시지 생성을 위한 빌더 패턴 생성자
//     */
//    @Builder
//    public ChatMessage(Long chatRoomId, Long senderId, ParticipantType senderType,
//                      Long senderEntityId, String content) {
//        this.chatRoomId = chatRoomId;
//        this.senderId = senderId;
//        this.senderType = senderType;
//        this.senderEntityId = senderEntityId;
//        this.content = content;
//        this.timestamp = LocalDateTime.now();
//        this.isRead = false;
//    }
//}
