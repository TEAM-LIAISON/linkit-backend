package liaison.linkit.team.business.assembler;

import java.util.Optional;

import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementDetailAssembler {

    // Assembler
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementCommonAssembler announcementCommonAssembler;

    /**
     * 공고 상세 정보를 조립합니다. 로그인한 사용자와 로그아웃 상태 모두 처리할 수 있습니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @param teamCode 조회할 팀의 코드.
     * @param teamMemberAnnouncementId 조회할 팀원 공고의 ID.
     * @return 조립된 TeamDetail DTO.
     */
    public TeamMemberAnnouncementDetail assemblerTeamMemberAnnouncementDetail(
            final Optional<Long> optionalMemberId,
            final String teamCode,
            final Long teamMemberAnnouncementId) {
        // 1. 조회할 팀 정보
        final TeamMemberAnnouncement teamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);
        log.info("assembleTeamDetail - targetTeamMemberAnnouncement: {}", teamMemberAnnouncement);

        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementDetail(
                teamMemberAnnouncement,
                announcementCommonAssembler.checkAnnouncementScrap(
                        teamMemberAnnouncement, optionalMemberId),
                announcementCommonAssembler.getAnnouncementScrapCount(teamMemberAnnouncement),
                announcementCommonAssembler.fetchAnnouncementPositionItem(teamMemberAnnouncement),
                announcementCommonAssembler.fetchAnnouncementSkills(teamMemberAnnouncement));
    }
}
