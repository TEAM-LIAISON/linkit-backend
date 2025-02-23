package liaison.linkit.chat.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.matching.domain.type.SenderType;
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
    private SenderType participantAType;

    // USABLE, DELETED
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_a_status", nullable = false)
    private StatusType participantAStatus;

    // 채팅방의 두 번째 참여자 ID (Profile의 경우 emailId, Team의 경우 teamCode, TeamMemberAnnouncement의 경우
    // AnnouncementId)
    @Column(name = "participant_b_id", nullable = false)
    private String participantBId;

    @Column(name = "participant_b_member_id", nullable = false)
    private Long participantBMemberId;

    @Column(name = "participant_b_name", nullable = false)
    private String participantBName;

    // 두 번째 참여자의 유형 (PROFILE 또는 TEAM)
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_b_type", nullable = false)
    private SenderType participantBType;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_b_status", nullable = false)
    private StatusType participantBStatus;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    public void updateLastMessage(String message, LocalDateTime timestamp) {
        this.lastMessage = message;
        this.lastMessageTime = timestamp;
    }

    public void setParticipantAStatus(final StatusType participantAStatus) {
        this.participantAStatus = participantAStatus;
    }

    public void setParticipantBStatus(final StatusType participantBStatus) {
        this.participantBStatus = participantBStatus;
    }
}
