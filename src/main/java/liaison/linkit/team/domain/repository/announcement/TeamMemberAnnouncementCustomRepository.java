package liaison.linkit.team.domain.repository.announcement;

import java.util.List;
import java.util.Set;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamMemberAnnouncementCustomRepository {
    List<TeamMemberAnnouncement> getTeamMemberAnnouncements(final Long teamId);

    List<TeamMemberAnnouncement> getAllByTeamIds(final List<Long> teamIds);

    TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId);

    TeamMemberAnnouncement updateTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement, final UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest);

    TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(final TeamMemberAnnouncement teamMemberAnnouncement, final boolean isTeamMemberAnnouncementCurrentPublicState);

    Page<TeamMemberAnnouncement> findAll(
            final List<String> majorPosition,
            final List<String> skillName,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable
    );

    List<TeamMemberAnnouncement> findTopTeamMemberAnnouncements(final int limit);

    Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(final List<Long> teamIds);

    void deleteAllByIds(final List<Long> announcementIds);
}
