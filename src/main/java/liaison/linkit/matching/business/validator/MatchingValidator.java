package liaison.linkit.matching.business.validator;

import java.util.Objects;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.exception.CannotRequestMyAnnouncementException;
import liaison.linkit.matching.exception.CannotRequestMyProfileException;
import liaison.linkit.matching.exception.MatchingReceiverBadRequestException;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.exception.MatchingSenderBadRequestException;
import liaison.linkit.matching.exception.MatchingStatusTypeBadRequestException;
import liaison.linkit.matching.exception.UpdateMatchingStatusTypeBadRequestException;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateMatchingStatusTypeRequest;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingValidator {

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

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

    /**
     * 매칭 요청 발신에 대한 검증.
     */
    public void validateAddMatching(final Long memberId, final MatchingRequestDTO.AddMatchingRequest req) {
        validateSenderReceiverExclusivity(req);
        validateSender(req);
        validateReceiver(memberId, req);
    }

    // 팀으로 팀원 공고에 매칭 요청을 보낼 수 없음
    private void validateSenderReceiverExclusivity(final MatchingRequestDTO.AddMatchingRequest req) {
        if (Objects.nonNull(req.getSenderTeamCode()) && Objects.nonNull(req.getReceiverAnnouncementId())) {
            throw MatchingRelationBadRequestException.EXCEPTION;
        }
    }

    // 발신자 검증
    private void validateSender(final MatchingRequestDTO.AddMatchingRequest req) {
        if (req.getSenderType() == SenderType.PROFILE && Objects.isNull(req.getSenderEmailId())) {
            throw MatchingSenderBadRequestException.EXCEPTION;
        }

        if (req.getSenderType() == SenderType.TEAM && Objects.isNull(req.getSenderTeamCode())) {
            throw MatchingSenderBadRequestException.EXCEPTION;
        }
    }

    // 수신자 검증
    private void validateReceiver(final Long memberId, final MatchingRequestDTO.AddMatchingRequest req) {
        switch (req.getReceiverType()) {
            case PROFILE:
                validateProfileReceiver(memberId, req.getReceiverEmailId());
                break;
            case TEAM:
                validateTeamReceiver(memberId, req.getReceiverTeamCode());
                break;
            case ANNOUNCEMENT:
                validateAnnouncementReceiver(memberId, req.getReceiverAnnouncementId());
                break;
            default:
                throw MatchingReceiverBadRequestException.EXCEPTION;
        }
    }

    private void validateProfileReceiver(final Long memberId, final String receiverEmailId) {
        if (Objects.isNull(receiverEmailId)) {
            throw MatchingReceiverBadRequestException.EXCEPTION;
        }

        if (receiverEmailId.equals(memberQueryAdapter.findById(memberId).getEmail())) {
            throw CannotRequestMyProfileException.EXCEPTION;
        }
    }

    // 팀으로 수신된 경우 검증
    private void validateTeamReceiver(final Long memberId, final String receiverTeamCode) {
        if (Objects.isNull(receiverTeamCode)) {
            throw MatchingReceiverBadRequestException.EXCEPTION;
        }

        if (teamMemberQueryAdapter.findMembersByTeamCode(receiverTeamCode)
            .contains(memberQueryAdapter.findById(memberId))) {
            throw CannotRequestMyProfileException.EXCEPTION;
        }
    }

    // 공고로 수신된 경우 검증
    private void validateAnnouncementReceiver(final Long memberId, final Long receiverAnnouncementId) {
        if (Objects.isNull(receiverAnnouncementId)) {
            throw MatchingReceiverBadRequestException.EXCEPTION;
        }

        final Team team = teamMemberAnnouncementQueryAdapter
            .getTeamMemberAnnouncement(receiverAnnouncementId)
            .getTeam();

        if (teamMemberQueryAdapter.findMembersByTeamCode(team.getTeamCode())
            .contains(memberQueryAdapter.findById(memberId))) {
            throw CannotRequestMyAnnouncementException.EXCEPTION;
        }
    }
}
