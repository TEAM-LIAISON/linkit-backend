package liaison.linkit.team.service.announcement;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.domain.Position;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.implement.position.PositionQueryAdapter;
import liaison.linkit.profile.implement.skill.SkillQueryAdapter;
import liaison.linkit.team.business.announcement.AnnouncementPositionMapper;
import liaison.linkit.team.business.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementPositionCommandAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillCommandAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
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

    @Transactional(readOnly = true)
    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems getTeamMemberAnnouncementItems(final Long memberId, final String teamName) {
        final Team team = teamQueryAdapter.findByTeamName(teamName);
        final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncements(team.getId());
        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementItems(teamMemberAnnouncements);
    }

    @Transactional(readOnly = true)
    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail getTeamMemberAnnouncementDetail(final Long memberId, final String teamName, final Long teamMemberAnnouncementId) {
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(teamMemberAnnouncementId);

        // 포지션 조회
        AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
            announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
        }

        // 스킬 조회
        List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncementId);
        List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementDetail(teamMemberAnnouncement, announcementPositionItem, announcementSkillNames);
    }

    // 팀원 공고 생성 메서드
    public TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse addTeamMemberAnnouncement(
            final Long memberId, final String teamName, final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest addTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀원 공고 추가 요청이 서비스 계층에 발생했습니다.", memberId, teamName);
        final Team team = teamQueryAdapter.findByTeamName(teamName);
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementMapper.toAddTeamMemberAnnouncement(team, addTeamMemberAnnouncementRequest);
        final TeamMemberAnnouncement savedTeamMemberAnnouncement = teamMemberAnnouncementCommandAdapter.addTeamMemberAnnouncement(teamMemberAnnouncement);

        // 포지션 저장
        final Position position = positionQueryAdapter.findByMajorPositionAndSubPosition(addTeamMemberAnnouncementRequest.getMajorPosition(), addTeamMemberAnnouncementRequest.getSubPosition());
        AnnouncementPosition announcementPosition = new AnnouncementPosition(null, savedTeamMemberAnnouncement, position);
        AnnouncementPosition savedAnnouncementPosition = announcementPositionCommandAdapter.save(announcementPosition);
        final AnnouncementPositionItem announcementPositionItem = announcementPositionMapper.toAnnouncementPositionItem(savedAnnouncementPosition);

        // 스킬 저장
        List<String> skillNames = addTeamMemberAnnouncementRequest.getAnnouncementSkillNames()
                .stream()
                .map(TeamMemberAnnouncementRequestDTO.AnnouncementSkillName::getAnnouncementSkillName) // 괄호 제거
                .collect(Collectors.toList());
        log.info("check bug 0");
        final List<Skill> skills = skillQueryAdapter.getSkillsBySkillNames(skillNames);
        log.info("check bug 1");
        final List<AnnouncementSkill> announcementSkills = announcementSkillMapper.toAddProjectSkills(savedTeamMemberAnnouncement, skills);
        log.info("check bug 2");
        announcementSkillCommandAdapter.saveAll(announcementSkills);
        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

        return teamMemberAnnouncementMapper.toAddTeamMemberAnnouncementResponse(
                teamMemberAnnouncement,
                announcementPositionItem,
                announcementSkillNames
        );
    }

    // 팀원 공고 수정 메서드
    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse updateTeamMemberAnnouncement(
            final Long memberId,
            final String teamName,
            final Long teamMemberAnnouncementId,
            final TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}의 팀원 공고 ID = {}에 대한 업데이트 요청이 서비스 계층에 발생했습니다.", memberId, teamName, teamMemberAnnouncementId);

        // 1. 기존 팀원 공고 조회
        final TeamMemberAnnouncement existingTeamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(teamMemberAnnouncementId);

        // 2. DTO를 통해 팀원 공고 업데이트
        final TeamMemberAnnouncement updatedTeamMemberAnnouncement =
                teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncement(existingTeamMemberAnnouncement, updateTeamMemberAnnouncementRequest);

        // 기존 포지션 삭제
        final Position position = positionQueryAdapter.findByMajorPositionAndSubPosition(updateTeamMemberAnnouncementRequest.getMajorPosition(), updateTeamMemberAnnouncementRequest.getSubPosition());
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncementId)) {
            announcementPositionCommandAdapter.deleteAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
        }

        // 새로운 포지션 추가
        final AnnouncementPosition announcementPosition = new AnnouncementPosition(null, updatedTeamMemberAnnouncement, position);
        final AnnouncementPosition savedAnnouncementPosition = announcementPositionCommandAdapter.save(announcementPosition);
        final AnnouncementPositionItem announcementPositionItem = announcementPositionMapper.toAnnouncementPositionItem(savedAnnouncementPosition);

        // 기존 공고 스킬 삭제
        List<AnnouncementSkill> existingAnnouncementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncementId);
        if (existingAnnouncementSkills != null & !existingAnnouncementSkills.isEmpty()) {
            announcementSkillCommandAdapter.deleteAll(existingAnnouncementSkills);
        }

        // 새로운 공고 스킬 추가
        List<String> newSkillNames = updateTeamMemberAnnouncementRequest.getAnnouncementSkillNames()
                .stream()
                .map(TeamMemberAnnouncementRequestDTO.AnnouncementSkillName::getAnnouncementSkillName)
                .toList();

        final List<Skill> newSkills = skillQueryAdapter.getSkillsBySkillNames(newSkillNames);
        final List<AnnouncementSkill> newAnnouncementSkills = announcementSkillMapper.toAddProjectSkills(updatedTeamMemberAnnouncement, newSkills);
        announcementSkillCommandAdapter.saveAll(newAnnouncementSkills);
        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(newAnnouncementSkills);

        return teamMemberAnnouncementMapper.toUpdateTeamMemberAnnouncementResponse(
                updatedTeamMemberAnnouncement,
                announcementPositionItem,
                announcementSkillNames
        );
    }

    public TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse removeTeamMemberAnnouncement(final Long memberId, final String teamName, final Long teamMemberAnnouncementId) {
        final TeamMemberAnnouncement existingTeamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(teamMemberAnnouncementId);

        // 기존 포지션 삭제
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncementId)) {
            announcementPositionCommandAdapter.deleteAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
        }

        List<AnnouncementSkill> existingAnnouncementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncementId);
        if (existingAnnouncementSkills != null & !existingAnnouncementSkills.isEmpty()) {
            announcementSkillCommandAdapter.deleteAll(existingAnnouncementSkills);
        }

        teamMemberAnnouncementCommandAdapter.removeTeamMemberAnnouncement(existingTeamMemberAnnouncement);

        return teamMemberAnnouncementMapper.toRemoveTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse updateTeamMemberAnnouncementPublicState(
            final Long memberId,
            final String teamName,
            final Long teamMemberAnnouncementId
    ) {
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(teamMemberAnnouncementId);
        final boolean isTeamMemberAnnouncementCurrentPublicState = teamMemberAnnouncement.isAnnouncementPublic();
        final TeamMemberAnnouncement updatedTeamMemberAnnouncement = teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncementPublicState(teamMemberAnnouncement,
                isTeamMemberAnnouncementCurrentPublicState);

        return teamMemberAnnouncementMapper.toUpdateTeamMemberAnnouncementPublicState(updatedTeamMemberAnnouncement);
    }
}
