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
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.global.type.StatusType;
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

    // USABLE, DELETED
    private StatusType participantAStatus;

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

    private StatusType participantBStatus;

    @Column
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageTime;

    public void updateLastMessage(String message, LocalDateTime timestamp) {
        this.lastMessage = message;
        this.lastMessageTime = timestamp;
    }
}
