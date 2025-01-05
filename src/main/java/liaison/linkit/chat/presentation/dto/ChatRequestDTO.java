package liaison.linkit.chat.presentation.dto;

import liaison.linkit.chat.domain.type.CreateChatLocation;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChatRoomRequest {
        private Long matchingId;                                    // 채팅을 발생시킬 수 있는 매칭 ID

        private CreateChatLocation createChatLocation;              // 채팅방 생성 위치 (발신함 / 수신함)

        private SenderType senderType;                              // 매칭 요청 발신자의 타입
        private String senderEmailId;                               // 매칭 요청 발신자의 유저 아이디
        private String senderTeamCode;                              // 매칭 요청 발신자의 팀 아이디

        private ReceiverType receiverType;                          // 매칭 요청 수신자의 타입
        private String receiverEmailId;                             // 매칭 요청 수신자 유저 아이디
        private String receiverTeamCode;                            // 매칭 요청 수신자 팀 아이디
        private Long receiverAnnouncementId;                        // 매칭 요청 수신자 공고 아이디
    }
}
