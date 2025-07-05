package liaison.linkit.scrap.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.business.mapper.AnnouncementScrapMapper;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.exception.announcementScrap.AnnouncementScrapBadRequestException;
import liaison.linkit.scrap.exception.profileScrap.ProfileScrapBadRequestException;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapRequestDTO.UpdateAnnouncementScrapRequest;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementScrapService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;

    private final AnnouncementScrapMapper announcementScrapMapper;

    //    private final ScrapValidator scrapValidator;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;

    public AnnouncementScrapResponseDTO.UpdateAnnouncementScrap updateAnnouncementScrap(
            final Long memberId,
            final Long teamMemberAnnouncementId,
            final UpdateAnnouncementScrapRequest updateAnnouncementScrapRequest) {

        boolean shouldAddScrap = updateAnnouncementScrapRequest.isChangeScrapValue();

        //        scrapValidator.validateSelfTeamMemberAnnouncementScrap(memberId,
        // teamMemberAnnouncementId);     // 자기 자신의 프로필 선택에 대한 예외 처리
        //        scrapValidator.validateMemberMaxTeamMemberAnnouncementScrap(memberId);         //
        // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists =
                announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(
                        memberId, teamMemberAnnouncementId);

        if (scrapExists) {
            handleExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        }

        return announcementScrapMapper.toUpdateAnnouncementScrap(
                teamMemberAnnouncementId, shouldAddScrap);
    }

    public AnnouncementInformMenus getAnnouncementScraps(final Long memberId) {
        // 1) memberId로 Announcement 목록 조회
        final List<AnnouncementScrap> announcementScraps =
                announcementScrapQueryAdapter.findAllByMemberId(memberId);

        // 2) TeamScrap -> Team 리스트 추출
        final List<TeamMemberAnnouncement> teamMemberAnnouncements =
                announcementScraps.stream()
                        .map(AnnouncementScrap::getTeamMemberAnnouncement)
                        .toList();

        final List<AnnouncementInformMenu> announcementInformMenus = new ArrayList<>();

        for (TeamMemberAnnouncement teamMemberAnnouncement : teamMemberAnnouncements) {
            announcementInformMenus.add(
                    announcementInformMenuAssembler.mapToAnnouncementInformMenu(
                            teamMemberAnnouncement, Optional.ofNullable(memberId)));
        }

        return announcementScrapMapper.toAnnouncementInformMenus(announcementInformMenus);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(
            Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            announcementScrapCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(
                    memberId, teamMemberAnnouncementId);
        } else {
            throw AnnouncementScrapBadRequestException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(
            Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            TeamMemberAnnouncement teamMemberAnnouncement =
                    teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);
            AnnouncementScrap announcementScrap =
                    new AnnouncementScrap(null, member, teamMemberAnnouncement);
            announcementScrapCommandAdapter.addAnnouncementScrap(announcementScrap);
        } else {
            throw ProfileScrapBadRequestException.EXCEPTION;
        }
    }
}
