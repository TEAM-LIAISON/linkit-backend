package liaison.linkit.matching.service;

import java.util.List;
import liaison.linkit.matching.business.MatchingMapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingReceivedMenu;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingService {

    private final MatchingCommandAdapter matchingCommandAdapter;
    private final MatchingQueryAdapter matchingQueryAdapter;

    private final MatchingMapper matchingMapper;

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MemberQueryAdapter memberQueryAdapter;

    @Transactional(readOnly = true)
    public MatchingMenu getMatchingMenu(
            final Long memberId
    ) {
        int receivedMatchingNotificationCount = 0;
        int requestedMatchingNotificationCount = 0;

        // 내 프로필에 대한 수신함 우선 판단
        /**
         *  내가 가지고 있는 memberId에 대하여 Matching 엔티티에서 emailId와 동일한 값을 찾는다
         *
         */

        // 해당 회원이 오너인 팀이 존재한다면
        if (teamMemberQueryAdapter.existsTeamOwnerByMemberId(memberId)) {
            // 해당 회원이 오너로 등록된 팀들의 teamCode를 가져온다.
            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);

            // 2. 팀 코드 목록 추출
            List<String> myTeamCodes = myTeams.stream()
                    .map(Team::getTeamCode)
                    .toList();

            // "TEAM" 매칭(= 팀으로 수신) 건수
            receivedMatchingNotificationCount += matchingQueryAdapter.countByReceiverTeamCodes(myTeamCodes);

            // 2-2) 팀 ID 목록
            List<Long> myTeamIds = myTeams.stream()
                    .map(Team::getId)
                    .toList();

            // 3) 팀이 만든 공고 목록 -> 공고 ID 목록
            List<TeamMemberAnnouncement> announcements =
                    teamMemberAnnouncementQueryAdapter.getAllByTeamIds(myTeamIds);

            List<Long> announcementIds = announcements.stream()
                    .map(TeamMemberAnnouncement::getId)
                    .toList();

            // "ANNOUNCEMENT" 매칭(= 공고로 수신) 건수
            receivedMatchingNotificationCount += matchingQueryAdapter.countByReceiverAnnouncementIds(announcementIds);
        }

        return matchingMapper.toMatchingMenuResponse(receivedMatchingNotificationCount, requestedMatchingNotificationCount);
    }

    public Page<MatchingReceivedMenu> getMatchingReceivedMenuResponse(
            final Long memberId,
            final ReceiverType receiverType,
            Pageable pageable
    ) {
        if (receiverType.equals(ReceiverType.PROFILE)) {
            // 삭제되지 않은 상태 + 수신자가 Profile + 발신자는 상관 X + 읽음 여부 상관 X
            final String emailId = memberQueryAdapter.findEmailIdById(memberId);

            final Page<Matching> receivedToProfileMatchingItems = matchingQueryAdapter.findReceivedToProfile(emailId, pageable);

            return receivedToProfileMatchingItems.map(
                    this::toMatchingReceivedMenu
            );
        } else {
            return null;
        }
    }

    private MatchingReceivedMenu toMatchingReceivedMenu(
            final Matching receivedMatchingItem
    ) {
        return matchingMapper.toMatchingReceivedMenu(
                receivedMatchingItem
        );
    }

    public MatchingResponseDTO.AddMatchingResponse addMatching(
            final Long memberId,
            final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {

        if (addMatchingRequest.getSenderTeamCode() != null && addMatchingRequest.getReceiverAnnouncementId() != null) {
            throw MatchingRelationBadRequestException.EXCEPTION;
        }

        final Matching matching = Matching.builder()
                .id(null)
                .senderType(addMatchingRequest.getSenderType())
                .receiverType(addMatchingRequest.getReceiverType())
                .requestMessage(addMatchingRequest.getRequestMessage())
                .build();

        return matchingMapper.toAddMatchingResponse(matching);
    }
}
