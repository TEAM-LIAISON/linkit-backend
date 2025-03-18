package liaison.linkit.team.business.service.announcement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.common.domain.Position;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.implement.position.PositionQueryAdapter;
import liaison.linkit.profile.implement.skill.SkillQueryAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.team.business.assembler.announcement.AnnouncementDetailAssembler;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.business.assembler.announcement.AnnouncementViewItemsAssembler;
import liaison.linkit.team.business.mapper.announcement.AnnouncementPositionMapper;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.projectType.ProjectType;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.exception.announcement.TeamMemberAnnouncementClosedBadRequestException;
import liaison.linkit.team.exception.teamMember.TeamAdminNotRegisteredException;
import liaison.linkit.team.implement.announcement.AnnouncementPositionCommandAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillCommandAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.projectType.ProjectTypeQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMemberAnnouncementService {

    private final TeamQueryAdapter teamQueryAdapter;

    private final SkillQueryAdapter skillQueryAdapter;

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;

    private final PositionQueryAdapter positionQueryAdapter;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final AnnouncementPositionCommandAdapter announcementPositionCommandAdapter;
    private final AnnouncementPositionMapper announcementPositionMapper;

    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillCommandAdapter announcementSkillCommandAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;
    private final AnnouncementDetailAssembler announcementDetailAssembler;
    private final AnnouncementViewItemsAssembler announcementViewItemsAssembler;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;

    private final ViewCountService viewCountService;
    private final ProjectTypeQueryAdapter projectTypeQueryAdapter;

    @Transactional(readOnly = true)
    public TeamMemberAnnouncementItems getTeamMemberAnnouncementViewItems(
            final Optional<Long> optionalMemberId, final String teamCode) {
        return announcementViewItemsAssembler.assembleTeamMemberAnnouncementViewItems(
                optionalMemberId, teamCode);
    }

    // 팀원 공고 생성 메서드
    public TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse
            addTeamMemberAnnouncement(
                    final Long memberId,
                    final String teamCode,
                    final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest
                            addTeamMemberAnnouncementRequest) {

        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(team.getId(), memberId)) {
            throw TeamAdminNotRegisteredException.EXCEPTION;
        }

        final TeamMemberAnnouncement teamMemberAnnouncement =
                teamMemberAnnouncementMapper.toAddTeamMemberAnnouncement(
                        team, addTeamMemberAnnouncementRequest);

        final TeamMemberAnnouncement savedTeamMemberAnnouncement =
                teamMemberAnnouncementCommandAdapter.addTeamMemberAnnouncement(
                        teamMemberAnnouncement);

        // 포지션 저장
        final Position position =
                positionQueryAdapter.findByMajorPositionAndSubPosition(
                        addTeamMemberAnnouncementRequest.getMajorPosition(),
                        addTeamMemberAnnouncementRequest.getSubPosition());
        AnnouncementPosition announcementPosition =
                new AnnouncementPosition(null, savedTeamMemberAnnouncement, position);
        AnnouncementPosition savedAnnouncementPosition =
                announcementPositionCommandAdapter.save(announcementPosition);
        final AnnouncementPositionItem announcementPositionItem =
                announcementPositionMapper.toAnnouncementPositionItem(savedAnnouncementPosition);

        // 스킬 저장 (필수값 아님)
        List<String> skillNames =
                Optional.ofNullable(addTeamMemberAnnouncementRequest.getAnnouncementSkillNames())
                        .orElse(Collections.emptyList()) // null 방지
                        .stream()
                        .map(
                                TeamMemberAnnouncementRequestDTO.AnnouncementSkillName
                                        ::getAnnouncementSkillName)
                        .collect(Collectors.toList());

        List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames =
                Collections.emptyList();

        if (!skillNames.isEmpty()) {
            final List<Skill> skills = skillQueryAdapter.getSkillsBySkillNames(skillNames);

            if (!skills.isEmpty()) {
                final List<AnnouncementSkill> announcementSkills =
                        announcementSkillMapper.toAddProjectSkills(
                                savedTeamMemberAnnouncement, skills);
                announcementSkillCommandAdapter.saveAll(announcementSkills);
                announcementSkillNames =
                        announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);
            }
        }

        final ProjectType projectType =
                projectTypeQueryAdapter.findByProjectTypeName(
                        addTeamMemberAnnouncementRequest.getProjectTypeName());

        return teamMemberAnnouncementMapper.toAddTeamMemberAnnouncementResponse(
                teamMemberAnnouncement, announcementPositionItem, announcementSkillNames);
    }

    // 팀원 공고 수정 메서드
    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse
            updateTeamMemberAnnouncement(
                    final Long memberId,
                    final String teamCode,
                    final Long teamMemberAnnouncementId,
                    final TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest
                            updateTeamMemberAnnouncementRequest) {

        // 1. 기존 팀원 공고 조회
        final TeamMemberAnnouncement existingTeamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        teamMemberAnnouncementId);

        // 기존 공고 업데이트 전 상태 로직 처리
        boolean isNewPermanentRecruitment =
                updateTeamMemberAnnouncementRequest.getIsPermanentRecruitment();
        String newEndDate = updateTeamMemberAnnouncementRequest.getAnnouncementEndDate();
        boolean isAnnouncementInProgress =
                calculateIsAnnouncementInProgress(newEndDate, isNewPermanentRecruitment);

        updateTeamMemberAnnouncementRequest.setIsAnnouncementInProgress(isAnnouncementInProgress);

        // 2. DTO를 통해 팀원 공고 업데이트
        final TeamMemberAnnouncement updatedTeamMemberAnnouncement =
                teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncement(
                        existingTeamMemberAnnouncement, updateTeamMemberAnnouncementRequest);

        // 기존 포지션 삭제
        final Position position =
                positionQueryAdapter.findByMajorPositionAndSubPosition(
                        updateTeamMemberAnnouncementRequest.getMajorPosition(),
                        updateTeamMemberAnnouncementRequest.getSubPosition());
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(
                teamMemberAnnouncementId)) {
            announcementPositionCommandAdapter.deleteAllByTeamMemberAnnouncementId(
                    teamMemberAnnouncementId);
        }

        // 새로운 포지션 추가
        final AnnouncementPosition announcementPosition =
                new AnnouncementPosition(null, updatedTeamMemberAnnouncement, position);

        final AnnouncementPosition savedAnnouncementPosition =
                announcementPositionCommandAdapter.save(announcementPosition);

        final AnnouncementPositionItem announcementPositionItem =
                announcementPositionMapper.toAnnouncementPositionItem(savedAnnouncementPosition);

        // 기존 공고 스킬 삭제
        List<AnnouncementSkill> existingAnnouncementSkills =
                announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncementId);
        if (existingAnnouncementSkills != null & !existingAnnouncementSkills.isEmpty()) {
            announcementSkillCommandAdapter.deleteAll(existingAnnouncementSkills);
        }

        // 새로운 공고 스킬 추가
        List<String> newSkillNames =
                updateTeamMemberAnnouncementRequest.getAnnouncementSkillNames().stream()
                        .map(
                                TeamMemberAnnouncementRequestDTO.AnnouncementSkillName
                                        ::getAnnouncementSkillName)
                        .toList();

        final List<Skill> newSkills = skillQueryAdapter.getSkillsBySkillNames(newSkillNames);
        final List<AnnouncementSkill> newAnnouncementSkills =
                announcementSkillMapper.toAddProjectSkills(
                        updatedTeamMemberAnnouncement, newSkills);
        announcementSkillCommandAdapter.saveAll(newAnnouncementSkills);
        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames =
                announcementSkillMapper.toAnnouncementSkillNames(newAnnouncementSkills);

        return teamMemberAnnouncementMapper.toUpdateTeamMemberAnnouncementResponse(
                updatedTeamMemberAnnouncement, announcementPositionItem, announcementSkillNames);
    }

    public TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse
            removeTeamMemberAnnouncement(
                    final Long memberId,
                    final String teamCode,
                    final Long teamMemberAnnouncementId) {
        final TeamMemberAnnouncement existingTeamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        teamMemberAnnouncementId);

        // 스크랩 당한 공고가 삭제 될 때 스크랩도 삭제
        announcementScrapCommandAdapter.deleteAllByAnnouncementIds(
                List.of(teamMemberAnnouncementId));

        // 기존 포지션 삭제
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(
                teamMemberAnnouncementId)) {
            announcementPositionCommandAdapter.deleteAllByTeamMemberAnnouncementId(
                    teamMemberAnnouncementId);
        }

        List<AnnouncementSkill> existingAnnouncementSkills =
                announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncementId);
        if (existingAnnouncementSkills != null & !existingAnnouncementSkills.isEmpty()) {
            announcementSkillCommandAdapter.deleteAll(existingAnnouncementSkills);
        }

        teamMemberAnnouncementCommandAdapter.removeTeamMemberAnnouncement(
                existingTeamMemberAnnouncement);

        return teamMemberAnnouncementMapper.toRemoveTeamMemberAnnouncementId(
                teamMemberAnnouncementId);
    }

    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse
            updateTeamMemberAnnouncementPublicState(
                    final Long memberId,
                    final String teamCode,
                    final Long teamMemberAnnouncementId) {
        final TeamMemberAnnouncement teamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        teamMemberAnnouncementId);
        final boolean isTeamMemberAnnouncementCurrentPublicState =
                teamMemberAnnouncement.isAnnouncementPublic();
        final TeamMemberAnnouncement updatedTeamMemberAnnouncement =
                teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncementPublicState(
                        teamMemberAnnouncement, isTeamMemberAnnouncementCurrentPublicState);

        return teamMemberAnnouncementMapper.toUpdateTeamMemberAnnouncementPublicState(
                updatedTeamMemberAnnouncement);
    }

    /**
     * 홈 화면에 표시할 공고 목록(AnnouncementInformMenus)을 반환합니다. 로그인 상태(Optional memberId 값 존재)와 로그아웃
     * 상태(Optional.empty()) 모두 처리합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @return AnnouncementInformMenus DTO.
     */
    @Transactional(readOnly = true)
    public AnnouncementInformMenus getHomeAnnouncementInformMenus(
            final Optional<Long> optionalMemberId) {
        return announcementInformMenuAssembler.assembleHomeAnnouncementInformMenus(
                optionalMemberId);
    }

    /**
     * 공고 상세 정보를 반환합니다. 로그인한 사용자와 로그아웃 상태 모두 처리할 수 있습니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @param teamCode 조회할 팀의 코드.
     * @param teamMemberAnnouncementId 조회할 팀원 공고의 ID.
     * @return 조립된 TeamDetail DTO.
     */
    @Transactional(readOnly = true)
    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail
            getTeamMemberAnnouncementDetail(
                    final Optional<Long> optionalMemberId,
                    final String teamCode,
                    final Long teamMemberAnnouncementId) {
        return announcementDetailAssembler.assemblerTeamMemberAnnouncementDetail(
                optionalMemberId, teamCode, teamMemberAnnouncementId);
    }

    public TeamMemberAnnouncementResponseDTO.CloseTeamMemberAnnouncementResponse
            closeTeamMemberAnnouncement(
                    final Long memberId,
                    final String teamCode,
                    final Long teamMemberAnnouncementId) {

        // 모집 마감일 데이터는 그대로두고 isAnnouncementInProgress false 강제 지정
        // 제일 상단의 변수를 강제 지정하여 모집 공고 마감 여부 응답에 문제가 없도록 설정

        final TeamMemberAnnouncement teamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        teamMemberAnnouncementId);

        if (!teamMemberAnnouncement.isAnnouncementInProgress()) {
            throw TeamMemberAnnouncementClosedBadRequestException.EXCEPTION;
        }

        final TeamMemberAnnouncement updatedTeamMemberAnnouncement =
                teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncementClosedState(
                        teamMemberAnnouncement, false);

        return teamMemberAnnouncementMapper.toCloseTeamMemberAnnouncement(
                updatedTeamMemberAnnouncement);
    }

    private boolean calculateIsAnnouncementInProgress(
            String endDate, boolean isPermanentRecruitment) {
        if (isPermanentRecruitment) {
            return true; // 상시 모집인 경우 항상 진행 중
        }

        if (endDate != null && !endDate.isEmpty()) {
            return !DateUtils.calculateAnnouncementClosed(endDate);
        }

        return false; // endDate가 null이거나 빈 문자열인 경우 기본적으로 false
    }
}
