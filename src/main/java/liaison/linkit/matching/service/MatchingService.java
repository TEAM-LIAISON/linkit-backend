package liaison.linkit.matching.service;

import liaison.linkit.matching.business.MatchingMapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
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

//    @Transactional(readOnly = true)
//    public MatchingResponseDTO.MatchingMenuResponse getMatchingMenu(
//            final Long memberId
//    ) {
//        int receivedMatchingNotificationCount = 0;
//        int requestedMatchingNotificationCount = 0;
//
//        // 내 프로필에 대한 수신함 우선 판단
//
//        // 해당 회원이 오너인 팀이 존재한다면
//        if (teamMemberQueryAdapter.existsTeamOwnerByMemberId(memberId)) {
//            // 해당 회원이 오너로 등록된 팀들의 teamCode를 가져온다.
//            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
//
//            // 2. 팀 코드 목록 추출
//            List<String> myTeamCodes = myTeams.stream()
//                    .map(Team::getTeamCode)
//                    .toList();
//
//            // "TEAM" 매칭(= 팀으로 수신) 건수
//            receivedMatchingCount += matchingQueryAdapter.countByReceiverTeamCodes(myTeamCodes);
//
//            // 2-2) 팀 ID 목록
//            List<Long> myTeamIds = myTeams.stream()
//                    .map(Team::getId)
//                    .toList();
//
//            // 3) 팀이 만든 공고 목록 -> 공고 ID 목록
//            List<TeamMemberAnnouncement> announcements =
//                    teamMemberAnnouncementQueryAdapter.getAllByTeamIds(myTeamIds);
//
//            List<Long> announcementIds = announcements.stream()
//                    .map(TeamMemberAnnouncement::getId)
//                    .toList();
//
//            // "ANNOUNCEMENT" 매칭(= 공고로 수신) 건수
//            receivedMatchingCount += matchingQueryAdapter.countByReceiverAnnouncementIds(announcementIds);
//        }
//
//        return matchingMapper.toMatchingMenuResponse(receivedMatchingCount, requestedMatchingCount);
//    }

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
