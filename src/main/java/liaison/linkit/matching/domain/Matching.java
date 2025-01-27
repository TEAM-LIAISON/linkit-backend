package liaison.linkit.matching.domain;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("status = 'USABLE'")
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 발신자 타입: PROFILE, TEAM, (추가 가능)
    @Enumerated(value = STRING)
    @Column(name = "sender_type", length = 50)
    private SenderType senderType;

    // 발신자가 프로필이면 sender_profile_id 사용, 팀이면 sender_team_id 사용
    @Column(name = "sender_email_id", nullable = true)
    private String senderEmailId;

    @Column(name = "sender_team_code", nullable = true)
    private String senderTeamCode;

    // 수신자 타입: PROFILE, TEAM, POSTING 등
    @Enumerated(value = STRING)
    @Column(name = "receiver_type", length = 50)
    private ReceiverType receiverType;

    // 수신자가 프로필이면 receiver_email_id 사용, 팀이면 receiver_team_id 사용, 공고이면 receiver_posting_id 사용
    @Column(name = "receiver_email_id", nullable = true)
    private String receiverEmailId;

    @Column(name = "receiver_team_code", nullable = true)
    private String receiverTeamCode;

    @Column(name = "receiver_announcement_id", nullable = true)
    private Long receiverAnnouncementId;

    // 매칭 요청 메시지
    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;

    // 매칭 상태(PENDING, COMPLETED, DENIED 등)
    @Enumerated(value = STRING)
    @Column(name = "matching_status", length = 50)
    private MatchingStatusType matchingStatusType;

    // 발신자 삭제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_delete_status", length = 50)
    private SenderDeleteStatus senderDeleteStatus;

    // 수신자 삭제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_delete_status", length = 50)
    private ReceiverDeleteStatus receiverDeleteStatus;

    // 수신자 읽음 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_read_status", length = 50)
    private ReceiverReadStatus receiverReadStatus;

    private boolean isChatRoomCreated;

    public void setReceiverReadStatus(final ReceiverReadStatus receiverReadStatus) {
        this.receiverReadStatus = receiverReadStatus;
    }

    public void setReceiverDeleteStatus(final ReceiverDeleteStatus receiverDeleteStatus) {
        this.receiverDeleteStatus = receiverDeleteStatus;
    }

    public void setSenderDeleteStatus(final SenderDeleteStatus senderDeleteStatus) {
        this.senderDeleteStatus = senderDeleteStatus;
    }

    public void setMatchingStatusType(final MatchingStatusType matchingStatusType) {
        this.matchingStatusType = matchingStatusType;
    }

    public void setIsChatRoomCreated(final boolean isChatRoomCreated) {
        this.isChatRoomCreated = isChatRoomCreated;
    }
}
