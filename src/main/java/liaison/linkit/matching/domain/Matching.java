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
import java.time.LocalDateTime;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.RequestSenderDeleteStatusType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.domain.type.SuccessReceiverDeleteStatusType;
import liaison.linkit.matching.domain.type.SuccessSenderDeleteStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 발신자 타입: PROFILE, TEAM, (추가 가능)
    @Enumerated(value = STRING)
    @Column(name = "sender_type", length = 50)
    private SenderType senderType;

    // 수신자 타입: PROFILE, TEAM, POSTING 등
    @Enumerated(value = STRING)
    @Column(name = "receiver_type", length = 50)
    private ReceiverType receiverType;

    // 발신자가 프로필이면 sender_profile_id 사용, 팀이면 sender_team_id 사용
    @Column(name = "sender_email_id", nullable = true)
    private Long senderEmailId;

    @Column(name = "sender_team_id", nullable = true)
    private Long senderTeamId;

    // 수신자가 프로필이면 receiver_email_id 사용, 팀이면 receiver_team_id 사용, 공고이면 receiver_posting_id 사용
    @Column(name = "receiver_email_id", nullable = true)
    private Long receiverEmailId;

    @Column(name = "receiver_team_id", nullable = true)
    private Long receiverTeamId;

    @Column(name = "receiver_posting_id", nullable = true)
    private Long receiverAnnouncementId;

    // 매칭 요청 메시지
    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;

    // 매칭 상태(PENDING, SUCCESSFUL, DENIED 등)
    @Enumerated(value = STRING)
    @Column(name = "matching_status", length = 50)
    private MatchingStatusType matchingStatusType;

    // 발신자 삭제 상태 (매칭 요청 단계)
    @Enumerated(EnumType.STRING)
    @Column(name = "request_sender_delete", length = 50)
    private RequestSenderDeleteStatusType requestSenderDeleteStatusType;

    // 성사된 매칭에서 발신자 삭제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "success_sender_delete", length = 50)
    private SuccessSenderDeleteStatusType successSenderDeleteStatusType;

    // 성사된 매칭에서 수신자 삭제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "success_receiver_delete", length = 50)
    private SuccessReceiverDeleteStatusType successReceiverDeleteStatusType;

    // 수신자가 열람했는지 여부
    @Column(name = "is_receiver_check", columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isReceiverCheck;

    // 생성, 수정 시간
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
