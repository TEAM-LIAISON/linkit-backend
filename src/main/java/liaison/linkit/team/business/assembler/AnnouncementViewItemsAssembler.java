package liaison.linkit.team.business.assembler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
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
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems;
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
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;

    // Mapper 및 Assembler
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final AnnouncementCommonAssembler announcementCommonAssembler;

    /**
     * 팀 코드를 기반으로, 로그인 상태(Optional memberId 존재 여부)에 따라 팀원 공고 목록(TeamMemberAnnouncementItems)을 조립하여
     * 반환합니다.
     *
     * <p>로그인 상태인 경우 관리자(오너/매니저)라면 전체 공고, 그렇지 않으면 공개 공고만 조회합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @param teamCode 조회할 팀의 코드.
     * @return 조립된 TeamMemberAnnouncementItems DTO.
     */
    public TeamMemberAnnouncementItems assembleTeamMemberAnnouncementViewItems(
            final Optional<Long> optionalMemberId, final String teamCode) {
        // 1. 팀 정보 조회
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        // 2. 로그인 여부 및 권한에 따라 공고 목록 필터링
        List<TeamMemberAnnouncement> targetAnnouncements =
                assembleTeamMemberAnnouncements(targetTeam, optionalMemberId);

        // 3. 공고 목록을 DTO 아이템으로 변환
        List<TeamMemberAnnouncementItem> items =
                convertAnnouncementsToItems(targetAnnouncements, optionalMemberId);

        // 4. 최종적으로 TeamMemberAnnouncementItems DTO를 생성하여 반환
        return TeamMemberAnnouncementItems.builder().teamMemberAnnouncementItems(items).build();
    }

    /**
     * 팀에 속한 팀원 공고 목록을 로그인 여부에 따라 필터링하여 조회합니다. - 로그인 상태이고, 로그인한 사용자가 해당 팀의 관리자(오너/매니저)인 경우에는 모든 공고를
     * 조회합니다. - 그렇지 않으면 공개된 공고만 조회합니다.
     *
     * @param targetTeam 조회 대상 팀.
     * @param loggedInMemberId 로그인한 회원의 ID(Optional).
     * @return 조건에 맞는 팀원 공고 목록.
     */
    private List<TeamMemberAnnouncement> assembleTeamMemberAnnouncements(
            final Team targetTeam, final Optional<Long> loggedInMemberId) {
        if (loggedInMemberId.isPresent()
                && teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                        targetTeam.getId(), loggedInMemberId.get())) {

            return teamMemberAnnouncementQueryAdapter.findAllAnnouncementsByTeamId(
                    targetTeam.getId());
        } else {
            return teamMemberAnnouncementQueryAdapter.findPublicAnnouncementsByTeamId(
                    targetTeam.getId());
        }
    }

    /**
     * 공고 목록을 DTO 아이템(List<TeamMemberAnnouncementItem>)으로 변환합니다.
     *
     * @param announcements 조회된 팀원 공고 목록.
     * @param optionalMemberId 로그인한 회원의 ID(Optional).
     * @return 공고 DTO 아이템 목록.
     */
    private List<TeamMemberAnnouncementItem> convertAnnouncementsToItems(
            final List<TeamMemberAnnouncement> announcements,
            final Optional<Long> optionalMemberId) {
        return announcements.stream()
                .map(
                        announcement -> {
                            // 1. 포지션 정보 조회 및 대분류 추출
                            AnnouncementPosition announcementPosition =
                                    announcementPositionQueryAdapter
                                            .findAnnouncementPositionByTeamMemberAnnouncementId(
                                                    announcement.getId());
                            String majorPosition =
                                    announcementCommonAssembler.extractMajorPosition(
                                            announcementPosition);

                            // 2. 공고 스킬 정보 조회
                            List<AnnouncementSkill> announcementSkills =
                                    announcementSkillQueryAdapter.getAnnouncementSkills(
                                            announcement.getId());
                            List<AnnouncementSkillName> announcementSkillNames =
                                    announcementSkillMapper.toAnnouncementSkillNames(
                                            announcementSkills);

                            // 3. 공고 스크랩 여부 및 스크랩 수 조회
                            boolean isAnnouncementScrap =
                                    announcementCommonAssembler.checkAnnouncementScrap(
                                            announcement, optionalMemberId);
                            int announcementScrapCount =
                                    announcementCommonAssembler.getAnnouncementScrapCount(
                                            announcement);

                            // 4. D-Day 계산
                            int dDay =
                                    announcementCommonAssembler.calculateAnnouncementDDay(
                                            announcement);

                            boolean isClosed =
                                    announcementCommonAssembler.calculateAnnouncementIsClosed(
                                            announcement);

                            // 5. DTO 아이템으로 변환
                            return teamMemberAnnouncementMapper.toTeamMemberAnnouncementItem(
                                    announcement,
                                    dDay,
                                    isClosed,
                                    majorPosition,
                                    announcementSkillNames,
                                    isAnnouncementScrap,
                                    announcementScrapCount);
                        })
                .collect(Collectors.toList());
    }
}
