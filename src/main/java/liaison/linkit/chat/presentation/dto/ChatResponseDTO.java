package liaison.linkit.chat.presentation.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatLeftMenu {
        private List<ChatRoomSummary> chatRoomSummaries = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomSummary {
        private Long chatRoomId;

        @Builder.Default
        private ChatPartnerInformation chatPartnerInformation = new ChatPartnerInformation();

        private Long unreadCount;                   // 읽지 않은 메시지 수
        private boolean isOnline;                   // 상대방 온라인 상태
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatPartnerInformation {
        private String chatPartnerName;             // 상대방 이름
        private String chatPartnerImageUrl;         // 상대방 프로필 이미지

        @Builder.Default
        private PartnerProfileDetailInformation partnerProfileDetailInformation = new PartnerProfileDetailInformation();

        @Builder.Default
        private PartnerTeamDetailInformation partnerTeamDetailInformation = new PartnerTeamDetailInformation();

        private String lastMessage;                 // 마지막 메시지
        private LocalDateTime lastMessageTime;      // 마지막 메시지 시간
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerProfileDetailInformation {
        @Builder.Default
        private ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerTeamDetailInformation {
        // 팀 규모 정보
        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();

        // 지역 정보
        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChatRoomResponse {
        private Long chatRoomId;

        private Long matchingId;

        private String participantAId;
        private ParticipantType participantAType;
        private String participantAName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private String participantBId;
        private ParticipantType participantBType;
        private String participantBName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private String lastMessage;                // 마지막 메시지 정보
        private String lastMessageTime;
        private Long unreadCount;                       // 읽지 않은 메시지 수
    }

    // 채팅 내역에 대한 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageHistoryResponse {
        private Long totalElements;      // 전체 메시지 수
        private Integer totalPages;      // 전체 페이지 수
        private Boolean hasNext;         // 다음 페이지 존재 여부

        @Builder.Default
        private List<ChatMessageResponse> messages = new ArrayList<>();
    }

    // 개별 메시지에 대한 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageResponse {
        private String messageId;                   // 메시지 ID
        private Long chatRoomId;                    // 채팅방 ID

        private ParticipantType messageSenderType;
        private String messageSenderId;

        private String content;                     // 메시지 내용
        private LocalDateTime timestamp;            // 전송 시간
        private boolean isRead;                     // 읽음 여부
    }
}
