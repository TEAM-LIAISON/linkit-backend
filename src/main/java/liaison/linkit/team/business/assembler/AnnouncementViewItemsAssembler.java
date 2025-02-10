package liaison.linkit.team.business.assembler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncemenItems;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementViewItemsAssembler {


    // Adapters
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;

    // Mapper 및 Assembler
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;

    /**
     * 팀 코드를 기반으로, 로그인 상태(Optional memberId 존재 여부)에 따라 팀원 공고 목록(TeamMemberAnnouncemenItems)을 조립하여 반환합니다.
     * <p>
     * 로그인 상태인 경우 관리자(오너/매니저)라면 전체 공고, 그렇지 않으면 공개 공고만 조회합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @param teamCode         조회할 팀의 코드.
     * @return 조립된 TeamMemberAnnouncemenItems DTO.
     */
    public TeamMemberAnnouncemenItems assembleTeamMemberAnnouncementViewItems(final Optional<Long> optionalMemberId, final String teamCode) {
        // 1. 팀 정보 조회
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        // 2. 로그인 여부 및 권한에 따라 공고 목록 필터링
        List<TeamMemberAnnouncement> targetAnnouncements = assembleTeamMemberAnnouncements(targetTeam, optionalMemberId);

        // 3. 공고 목록을 DTO 아이템으로 변환
        List<TeamMemberAnnouncementItem> items = convertAnnouncementsToItems(targetAnnouncements, optionalMemberId);

        // 4. 최종적으로 TeamMemberAnnouncemenItems DTO를 생성하여 반환
        return TeamMemberAnnouncemenItems.builder()
            .teamMemberAnnouncementItems(items)
            .build();
    }

    /**
     * 팀에 속한 팀원 공고 목록을 로그인 여부에 따라 필터링하여 조회합니다. - 로그인 상태이고, 로그인한 사용자가 해당 팀의 관리자(오너/매니저)인 경우에는 모든 공고를 조회합니다. - 그렇지 않으면 공개된 공고만 조회합니다.
     *
     * @param targetTeam       조회 대상 팀.
     * @param loggedInMemberId 로그인한 회원의 ID(Optional).
     * @return 조건에 맞는 팀원 공고 목록.
     */
    private List<TeamMemberAnnouncement> assembleTeamMemberAnnouncements(final Team targetTeam, final Optional<Long> loggedInMemberId) {
        if (loggedInMemberId.isPresent() && teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), loggedInMemberId.get())) {
            log.info("사용자 {}는 팀 {}의 관리자(오너/매니저)로 모든 공고 조회", loggedInMemberId.get(), targetTeam.getTeamCode());
            return teamMemberAnnouncementQueryAdapter.findAllAnnouncementsByTeamId(targetTeam.getId());
        } else {
            log.info("사용자 {}는 팀 {}의 일반 뷰어이므로 공개 공고만 조회", loggedInMemberId.orElse(null), targetTeam.getTeamCode());
            return teamMemberAnnouncementQueryAdapter.findPublicAnnouncementsByTeamId(targetTeam.getId());
        }
    }

    /**
     * 공고 목록을 DTO 아이템(List<TeamMemberAnnouncementItem>)으로 변환합니다.
     *
     * @param announcements    조회된 팀원 공고 목록.
     * @param optionalMemberId 로그인한 회원의 ID(Optional).
     * @return 공고 DTO 아이템 목록.
     */
    private List<TeamMemberAnnouncementItem> convertAnnouncementsToItems(final List<TeamMemberAnnouncement> announcements, final Optional<Long> optionalMemberId) {
        return announcements.stream().map(announcement -> {
            // 1. 포지션 정보 조회 및 대분류 추출
            AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(announcement.getId());
            String majorPosition = extractMajorPosition(announcementPosition);

            // 2. 공고 스킬 정보 조회
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(announcement.getId());
            List<AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

            // 3. 공고 스크랩 여부 및 스크랩 수 조회
            boolean isAnnouncementScrap = announcementInformMenuAssembler.checkAnnouncementScrap(announcement, optionalMemberId);
            int announcementScrapCount = announcementInformMenuAssembler.getAnnouncementScrapCount(announcement);

            // 4. D-Day 계산
            int dDay = announcementInformMenuAssembler.calculateAnnouncementDDay(announcement);

            // 5. DTO 아이템으로 변환
            return teamMemberAnnouncementMapper.toTeamMemberAnnouncementItem(
                announcement,
                dDay,
                majorPosition,
                announcementSkillNames,
                isAnnouncementScrap,
                announcementScrapCount
            );
        }).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // Public 헬퍼 메서드 (스크랩 및 D-Day 관련)
    // ─────────────────────────────────────────────────────────────

    /**
     * 포지션 정보를 바탕으로 대분류를 추출합니다.
     *
     * @param announcementPosition 조회된 공고 포지션.
     * @return 대분류 문자열; 정보가 없으면 "N/A" 반환.
     */
    public String extractMajorPosition(final AnnouncementPosition announcementPosition) {
        if (announcementPosition != null && announcementPosition.getPosition() != null) {
            return announcementPosition.getPosition().getMajorPosition();
        }
        return "N/A";
    }
}
