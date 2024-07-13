package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.repository.jobRole.JobRoleRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementJobRoleRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementSkillRepository;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberAnnouncementService {

    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamMemberAnnouncementJobRoleRepository teamMemberAnnouncementJobRoleRepository;
    private final TeamMemberAnnouncementSkillRepository teamMemberAnnouncementSkillRepository;

    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;


    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    private TeamMemberAnnouncement getTeamMemberAnnouncement(
            final Long teamMemberAnnouncementId
    ) {
        return teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));
    }

    public void validateTeamMemberAnnouncement(final Long memberId) {
        if (!teamMemberAnnouncementRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_MEMBER_ANNOUNCEMENT_WITH_PROFILE);
        }
    }

    @Transactional(readOnly = true)
    public List<TeamMemberAnnouncementResponse> getTeamMemberAnnouncements(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());

        return teamMemberAnnouncements.stream()
                .map(this::getTeamMemberAnnouncementResponse)
                .toList();
    }

    private TeamMemberAnnouncementResponse getTeamMemberAnnouncementResponse(
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        final List<TeamMemberAnnouncementJobRole> teamMemberAnnouncementJobRoleList = getTeamMemberAnnouncementJobRoles(teamMemberAnnouncement.getId());
        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(teamMemberAnnouncement.getId());
        return TeamMemberAnnouncementResponse.of(teamMemberAnnouncement, teamMemberAnnouncementJobRoleList, teamMemberAnnouncementSkillList);
    }

    public void saveAnnouncement(
            final Long memberId,
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        final TeamMemberAnnouncement savedTeamMemberAnnouncement = saveTeamMemberAnnouncement(teamProfile, teamMemberAnnouncementRequest);
        saveTeamMemberAnnouncementJobRole(savedTeamMemberAnnouncement, teamMemberAnnouncementRequest);
        saveTeamMemberAnnouncementSkill(savedTeamMemberAnnouncement, teamMemberAnnouncementRequest);

        // 팀원 공고가 존재하지 않았던 경우
        if (!teamProfile.getIsTeamMemberAnnouncement()) {
            teamProfile.updateIsTeamMemberAnnouncement(true);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }

    // 팀원 공고 생성/수정
    public void saveAnnouncements(
            final Long memberId,
            final List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 팀원 공고 이력이 있다 -> 전체 삭제 -> 재등록 필요
        if (teamMemberAnnouncementRepository.existsByTeamProfileId(teamProfile.getId())) {
            // 전체 삭제 완료
            teamMemberAnnouncementRepository.deleteAllByTeamProfileId(teamProfile.getId());

            // 팀 프로필 업데이트 필요
            // 상태 업데이트 필요

            // 순차적으로 1개씩 저장
            teamMemberAnnouncementRequestList.forEach(request -> {
                final TeamMemberAnnouncement savedTeamMemberAnnouncement = saveTeamMemberAnnouncement(teamProfile, request);
                saveTeamMemberAnnouncementJobRole(savedTeamMemberAnnouncement, request);
                saveTeamMemberAnnouncementSkill(savedTeamMemberAnnouncement, request);
            });
        }

        // 팀원 공고가 존재하지 않는다 -> 새로운 생성 필요
        else {
            teamMemberAnnouncementRequestList.forEach(request -> {
                final TeamMemberAnnouncement savedTeamMemberAnnouncement = saveTeamMemberAnnouncement(teamProfile, request);
                saveTeamMemberAnnouncementJobRole(savedTeamMemberAnnouncement, request);
                saveTeamMemberAnnouncementSkill(savedTeamMemberAnnouncement, request);
            });
        }
    }

    // 보유 역량 저장 메서드
    private void saveTeamMemberAnnouncementSkill(
            final TeamMemberAnnouncement savedTeamMemberAnnouncement,
            final TeamMemberAnnouncementRequest request
    ) {
        final List<Skill> skills = skillRepository
                .findSkillsBySkillNames(request.getSkillNames());

        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkills = skills.stream()
                .map(skill -> new TeamMemberAnnouncementSkill(null, savedTeamMemberAnnouncement, skill))
                .toList();

        teamMemberAnnouncementSkillRepository.saveAll(teamMemberAnnouncementSkills);
    }

    // 직무/역할 저장 메서드
    private void saveTeamMemberAnnouncementJobRole(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final TeamMemberAnnouncementRequest request
    ) {

        final List<JobRole> jobRoles = jobRoleRepository
                .findJobRoleByJobRoleNames(request.getJobRoleNames());

        final List<TeamMemberAnnouncementJobRole> teamMemberAnnouncementJobRoles = jobRoles.stream()
                .map(jobRole -> new TeamMemberAnnouncementJobRole(null, teamMemberAnnouncement, jobRole))
                .toList();

        teamMemberAnnouncementJobRoleRepository.saveAll(teamMemberAnnouncementJobRoles);
    }

    // 팀원 공고 메인 객체 저장 메서드
    private TeamMemberAnnouncement saveTeamMemberAnnouncement(
            final TeamProfile teamProfile,
            final TeamMemberAnnouncementRequest request
    ) {
        // 저장용 객체 생성
        final TeamMemberAnnouncement newTeamMemberAnnouncement = TeamMemberAnnouncement.of(
                teamProfile,
                request.getMainBusiness(),
                request.getApplicationProcess()
        );
        return teamMemberAnnouncementRepository.save(newTeamMemberAnnouncement);
    }


    // 팀원 공고 1개 삭제 메서드
    public void deleteTeamMemberAnnouncement(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);

        teamMemberAnnouncementRepository.deleteById(teamMemberAnnouncement.getId());
        if (!teamMemberAnnouncementRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.cancelTeamPerfectionFifteen();
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }


    public void updateTeamMemberAnnouncement(
            final Long teamMemberAnnouncementId,
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        // 삭제 먼저 진행
        teamMemberAnnouncementSkillRepository.deleteAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
        teamMemberAnnouncementJobRoleRepository.deleteAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
        teamMemberAnnouncementRepository.deleteById(teamMemberAnnouncementId);

        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);
        saveTeamMemberAnnouncementJobRole(teamMemberAnnouncement, teamMemberAnnouncementRequest);
        saveTeamMemberAnnouncementSkill(teamMemberAnnouncement, teamMemberAnnouncementRequest);
        teamMemberAnnouncement.update(teamMemberAnnouncementRequest);
    }

    private List<TeamMemberAnnouncementJobRole> getTeamMemberAnnouncementJobRoles(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementJobRoleRepository.findAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    private List<TeamMemberAnnouncementSkill> getTeamMemberAnnouncementSkills(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementSkillRepository.findAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }
}
