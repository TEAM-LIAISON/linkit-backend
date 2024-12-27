package liaison.linkit.matching.domain;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
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
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile_matching")
@TypeAlias("ProfileMatching")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProfileMatching {

    @Id
    private String id;

    private String memberId;
    private String profileId;
    
    // 매칭 요청 발신자 타입
    @Column(name = "sender_type")
    @Enumerated(value = STRING)
    private SenderType senderType;

    // 어떤 소개서에 요청 보낸 것인지 type 필요
    @Column(name = "receiver_type")
    @Enumerated(value = STRING)
    private ReceiverType receiverType;

    // 요청할 때 보내는 메시지
    @Column(name = "request_message")
    private String requestMessage;

    // 해당 매칭의 상태 관리 필요
    @Column(name = "matching_status_type")
    @Enumerated(value = STRING)
    private MatchingStatusType matchingStatusType;

    // 내가 보낸 매칭 삭제 관리 컬럼 (발신자)
    @Column(name = "request_sender_delete_status_type")
    @Enumerated(value = STRING)
    private RequestSenderDeleteStatusType requestSenderDeleteStatusType;

    // 성사된 매칭 삭제 관리 컬럼 (발신자)
    @Column(name = "success_sender_delete_status_type")
    @Enumerated(value = STRING)
    private SuccessSenderDeleteStatusType successSenderDeleteStatusType;

    // 성사된 매칭 삭제 관리 컬럼 (수신자)
    @Column(name = "success_receiver_delete_status_type")
    @Enumerated(value = STRING)
    private SuccessReceiverDeleteStatusType successReceiverDeleteStatusType;

    // 이 매칭 요청을 받은 사람이 열람을 했나요?
    @Column(name = "is_receiver_check", columnDefinition = "boolean default false")
    private Boolean isReceiverCheck;

    public void updateMatchingStatus(final boolean isAllow) {
        if (isAllow) {
            this.matchingStatusType = MatchingStatusType.SUCCESSFUL;
        } else {
            this.matchingStatusType = MatchingStatusType.DENIED;
        }
    }

    // 내가 보낸 매칭에서 발신자가 삭제한 경우
    public void updateRequestSenderDeleteStatusType(final boolean isDeleted) {
        if (isDeleted) {
            this.requestSenderDeleteStatusType = RequestSenderDeleteStatusType.DELETED;
        } else {
            this.requestSenderDeleteStatusType = RequestSenderDeleteStatusType.REMAINED;
        }
    }

    // 성사된 매칭에서 발신자가 삭제한 경우
    public void updateSuccessSenderDeleteStatusType(final boolean isDeleted) {
        if (isDeleted) {
            this.successSenderDeleteStatusType = SuccessSenderDeleteStatusType.DELETED;
        } else {
            this.successSenderDeleteStatusType = SuccessSenderDeleteStatusType.REMAINED;
        }
    }

    // 성사된 매칭에서 수신자가 삭제한 경우
    public void updateSuccessReceiverDeleteStatusType(final boolean isDeleted) {
        if (isDeleted) {
            this.successReceiverDeleteStatusType = SuccessReceiverDeleteStatusType.DELETED;
        } else {
            this.successReceiverDeleteStatusType = SuccessReceiverDeleteStatusType.REMAINED;
        }
    }
}
