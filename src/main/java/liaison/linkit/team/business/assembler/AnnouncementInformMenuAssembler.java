package liaison.linkit.team.business.assembler;

import java.util.List;
import java.util.Optional;

import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementInformMenuAssembler {

    // Adapters
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

    // Mappers
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;

    // Assembler
    private final AnnouncementCommonAssembler announcementCommonAssembler;

    /**
     * 홈 화면에 표시할 공고 목록(AnnouncementInformMenus)을 조립하여 반환합니다. 로그인 상태(Optional memberId 값 존재)와 로그아웃
     * 상태(Optional.empty()) 모두 처리합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @return 조립된 AnnouncementInformMenus DTO.
     */
    public AnnouncementInformMenus assembleHomeAnnouncementInformMenus(
            final Optional<Long> optionalMemberId) {
        // 최대 9개의 공고 조회 (예시)
        List<TeamMemberAnnouncement> announcements =
                teamMemberAnnouncementQueryAdapter.findHomeTopTeamMemberAnnouncements(9);

        // 각 공고를 AnnouncementInformMenu DTO로 매핑
        List<AnnouncementInformMenu> announcementInformMenus =
                announcements.stream()
                        .map(
                                announcement ->
                                        mapToAnnouncementInformMenu(announcement, optionalMemberId))
                        .toList();

        // 최종 목록 DTO로 변환하여 반환
        return teamMemberAnnouncementMapper.toAnnouncementInformMenus(announcementInformMenus);
    }

    /**
     * 공고(TeamMemberAnnouncement)를 AnnouncementInformMenu DTO로 변환합니다. 로그인 상태(Optional 값 존재)와 로그아웃
     * 상태를 모두 처리할 수 있습니다.
     *
     * @param teamMemberAnnouncement 조회할 공고 엔티티.
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 스크랩 여부를 조회합니다.
     * @return AnnouncementInformMenu DTO.
     */
    public AnnouncementInformMenu mapToAnnouncementInformMenu(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final Optional<Long> optionalMemberId) {
        // 1. 팀 정보 조회
        final Team team = teamMemberAnnouncement.getTeam();

        // 2. 공고 스킬 정보 조회
        List<AnnouncementSkillName> announcementSkillNames =
                announcementCommonAssembler.fetchAnnouncementSkills(teamMemberAnnouncement);

        // 3. D-Day 계산
        int announcementDDay =
                announcementCommonAssembler.calculateAnnouncementDDay(teamMemberAnnouncement);

        boolean isClosed =
                announcementCommonAssembler.calculateAnnouncementIsClosed(teamMemberAnnouncement);

        // 4. 공고 스크랩 여부 조회
        boolean isAnnouncementScrap =
                announcementCommonAssembler.checkAnnouncementScrap(
                        teamMemberAnnouncement, optionalMemberId);

        // 5. 공고 스크랩 수 조회
        int announcementScrapCount =
                announcementCommonAssembler.getAnnouncementScrapCount(teamMemberAnnouncement);

        // 6. 추가 정보 조회: 팀 규모, 팀 지역, 공고 포지션 정보
        TeamScaleItem teamScaleItem = announcementCommonAssembler.fetchTeamScaleItem(team);
        RegionDetail regionDetail = announcementCommonAssembler.fetchRegionDetail(team);
        AnnouncementPositionItem announcementPositionItem =
                announcementCommonAssembler.fetchAnnouncementPositionItem(teamMemberAnnouncement);

        // 7. 최종 DTO 변환 및 반환
        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementInform(
                team.getTeamLogoImagePath(),
                team.getTeamName(),
                team.getTeamCode(),
                teamScaleItem,
                regionDetail,
                teamMemberAnnouncement,
                announcementDDay,
                isClosed,
                isAnnouncementScrap,
                announcementScrapCount,
                announcementPositionItem,
                announcementSkillNames);
    }
}
