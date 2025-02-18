package liaison.linkit.matching.business.validator;

import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.exception.MatchingSenderBadRequestException;
import liaison.linkit.matching.exception.MatchingStatusTypeBadRequestException;
import liaison.linkit.matching.exception.UpdateMatchingStatusTypeBadRequestException;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateMatchingStatusTypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingValidator {

    /**
     * 요청 객체 검증: 요청 객체가 null이거나 REQUESTED 상태인 경우 예외 발생.
     */
    public void validateUpdateStatusRequest(final UpdateMatchingStatusTypeRequest request) {
        if (request == null || MatchingStatusType.REQUESTED.equals(request.getMatchingStatusType())) {
            throw MatchingStatusTypeBadRequestException.EXCEPTION;
        }
    }

    /**
     * 매칭 수신자가 요청을 보낸 회원과 일치하는지 확인.
     */
    public void ensureReceiverAuthorized(final Long memberId, final Long receiverMemberId) {
        if (!receiverMemberId.equals(memberId)) {
            throw UpdateMatchingStatusTypeBadRequestException.EXCEPTION;
        }
    }

    public void validateAddMatching(final MatchingRequestDTO.AddMatchingRequest req) {
        if (req.getSenderTeamCode() != null && req.getReceiverAnnouncementId() != null) {
            throw MatchingRelationBadRequestException.EXCEPTION;
        }

        if (req.getSenderType().equals(SenderType.PROFILE)) {
            if (req.getSenderEmailId() == null) {
                throw MatchingSenderBadRequestException.EXCEPTION;
            }
        }

        if (req.getSenderType().equals(SenderType.TEAM)) {
            if (req.getSenderTeamCode() == null) {
                throw MatchingSenderBadRequestException.EXCEPTION;
            }
        }


    }
}
