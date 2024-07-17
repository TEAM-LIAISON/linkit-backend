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
public class  TeamMemberAnnouncementResponse {

    private final Long id;
    private final String teamName;
    private final String jobRoleName;
    private final String mainBusiness;
    private final List<String> skillNames;
    private final String applicationProcess;

    public static TeamMemberAnnouncementResponse of(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final String teamName,
            final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole,
            final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList
    ) {

        final JobRole jobRole = teamMemberAnnouncementJobRole.getJobRole();

        final String jobRoleName = jobRole.getJobRoleName();

        final List<Skill> skillList = teamMemberAnnouncementSkillList.stream()
                .map(TeamMemberAnnouncementSkill::getSkill)
                .toList();

        final List<String> skillNames = skillList.stream()
                .map(Skill::getSkillName)
                .toList();

        return new TeamMemberAnnouncementResponse(
                teamMemberAnnouncement.getId(),
                teamName,
                jobRoleName,
                teamMemberAnnouncement.getMainBusiness(),
                skillNames,
                teamMemberAnnouncement.getApplicationProcess()
        );
    }

}
