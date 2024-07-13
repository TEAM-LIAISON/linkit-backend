package liaison.linkit.team.dto.response.announcement;

import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamMemberAnnouncementResponse {

    private final Long id;
    private final List<String> jobRoleNames;
    private final String mainBusiness;
    private final List<String> skillNames;
    private final String applicationProcess;


    public static TeamMemberAnnouncementResponse of(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final List<TeamMemberAnnouncementJobRole> teamMemberAnnouncementJobRoleList,
            final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList
    ) {

        List<JobRole> jobRoleList = teamMemberAnnouncementJobRoleList.stream()
                .map(TeamMemberAnnouncementJobRole::getJobRole)
                .toList();

        List<String> jobRoleNames = jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();

        List<Skill> skillList = teamMemberAnnouncementSkillList.stream()
                .map(TeamMemberAnnouncementSkill::getSkill)
                .toList();

        List<String> skillNames = skillList.stream()
                .map(Skill::getSkillName)
                .toList();

        return new TeamMemberAnnouncementResponse(
                teamMemberAnnouncement.getId(),
                jobRoleNames,
                teamMemberAnnouncement.getMainBusiness(),
                skillNames,
                teamMemberAnnouncement.getApplicationProcess()
        );
    }

}
