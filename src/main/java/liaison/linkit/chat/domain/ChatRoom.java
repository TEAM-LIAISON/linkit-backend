package liaison.linkit.chat.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ChatRoom extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long matchingId;

    // 채팅방의 첫 번째 참여자 ID (Profile의 경우 emailId, Team의 경우 teamCode)
    @Column(nullable = false)
    private String participantAId;

    @Column(nullable = false)
    private Long participantAMemberId;

    @Column(nullable = false)
    private String participantAName;

    // 첫 번째 참여자의 유형 (PROFILE 또는 TEAM)
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_a_type", nullable = false)
    private ParticipantType participantAType;

    // 채팅방의 두 번째 참여자 ID (Profile의 경우 emailId, Team의 경우 teamCode, TeamMemberAnnouncement의 경우 AnnouncementId)
    @Column(nullable = false)
    private String participantBId;

    @Column(nullable = false)
    private Long participantBMemberId;

    @Column(nullable = false)
    private String participantBName;

    // 두 번째 참여자의 유형 (PROFILE 또는 TEAM)
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_b_type", nullable = false)
    private ParticipantType participantBType;

    // 채팅 참여자 유형을 정의하는 열거형
    public enum ParticipantType {
        PROFILE,    // 개인 프로필로 참여
        TEAM        // 팀의 대표로 참여
    }

    /**
     * 주어진 ID와 타입이 채팅방의 참여자인지 확인
     *
     * @param id   확인할 참여자 ID
     * @param type 확인할 참여자 타입
     * @return 채팅방 참여자이면 true, 아니면 false
     */
    public boolean isParticipant(Long id, ParticipantType type) {
        return (participantAId.equals(id) && participantAType == type) ||
                (participantBId.equals(id) && participantBType == type);
    }

    /**
     * 채팅방 접근 권한 확인
     *
     * @param memberId 접근을 시도하는 회원 ID
     * @param teamId   접근을 시도하는 팀 ID (팀으로 접근하는 경우)
     * @return 접근 권한이 있으면 true, 없으면 false
     */
    public boolean canAccessChat(Long memberId, Long teamId) {
        // Profile로 참여한 경우 해당 memberId를 가진 사용자는 직접 접근 가능
        if (isParticipant(memberId, ParticipantType.PROFILE)) {
            return true;
        }
        // Team으로 참여한 경우 해당 teamId를 가진 팀의 채팅방만 접근 가능
        return isParticipant(teamId, ParticipantType.TEAM);
    }

    @Column
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageTime;

    public void updateLastMessage(String message, LocalDateTime timestamp) {
        this.lastMessage = message;
        this.lastMessageTime = timestamp;
    }
}
