package liaison.linkit.team.business.announcement;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;

@Mapper
public class AnnouncementSkillMapper {

    public List<AnnouncementSkill> toAddProjectSkills(final TeamMemberAnnouncement teamMemberAnnouncement, final List<Skill> skills) {
        return skills.stream()
                .map(skill -> AnnouncementSkill.builder()
                        .id(null)
                        .teamMemberAnnouncement(teamMemberAnnouncement)
                        .skill(skill)
                        .build())
                .collect(Collectors.toList());
    }

    public List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> toAnnouncementSkillNames(final List<AnnouncementSkill> announcementSkills) {
        return announcementSkills.stream()
                .map(as -> TeamMemberAnnouncementResponseDTO.AnnouncementSkillName.builder()
                        .announcementSkillName(as.getSkill().getSkillName())
                        .build()
                ).collect(Collectors.toList());
    }
}
