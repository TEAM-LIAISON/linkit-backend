package liaison.linkit.team.implement.announcement;

import java.util.List;
import java.util.Set;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.exception.announcement.TeamMemberAnnouncementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adapter
@RequiredArgsConstructor
public class TeamMemberAnnouncementQueryAdapter {
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    public TeamMemberAnnouncement findById(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> TeamMemberAnnouncementNotFoundException.EXCEPTION);
    }

    public List<TeamMemberAnnouncement> getTeamMemberAnnouncements(final Long teamId) {
        return teamMemberAnnouncementRepository.getTeamMemberAnnouncements(teamId);
    }

    public List<TeamMemberAnnouncement> getAllByTeamIds(final List<Long> teamIds) {
        return teamMemberAnnouncementRepository.getAllByTeamIds(teamIds);
    }

    public TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementRepository.getTeamMemberAnnouncement(teamMemberAnnouncementId);
    }

    public Page<TeamMemberAnnouncement> findAll(
            final List<String> majorPosition,
            final List<String> skillName,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable
    ) {
        return teamMemberAnnouncementRepository.findAll(majorPosition, skillName, cityName, scaleName, pageable);
    }

    public List<TeamMemberAnnouncement> findTopTeamMemberAnnouncements(final int limit) {
        return teamMemberAnnouncementRepository.findTopTeamMemberAnnouncements(limit);
    }

    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(final List<Long> teamIds) {
        return teamMemberAnnouncementRepository.getAllDeletableTeamMemberAnnouncementsByTeamIds(teamIds);
    }
}
