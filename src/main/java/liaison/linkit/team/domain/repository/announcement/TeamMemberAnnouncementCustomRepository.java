package liaison.linkit.team.domain.repository.announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamMemberAnnouncementCustomRepository {

    List<TeamMemberAnnouncement> getAllByTeamIds(final List<Long> teamIds);

    TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId);

    TeamMemberAnnouncement updateTeamMemberAnnouncement(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest);

    TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final boolean isTeamMemberAnnouncementCurrentPublicState);

    Page<TeamMemberAnnouncement> findAll(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable);

    Page<TeamMemberAnnouncement> findHotAnnouncements(final Pageable pageable);

    Page<TeamMemberAnnouncement> findExcludedAnnouncements(
            final List<Long> excludeAnnouncementIds, final Pageable pageable);

    CursorResponse<TeamMemberAnnouncement> findAllExcludingIdsWithCursor(
            final List<Long> excludeAnnouncementIds, final CursorRequest cursorRequest);

    CursorResponse<TeamMemberAnnouncement> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> scaleName,
            final CursorRequest cursorRequest);

    List<TeamMemberAnnouncement> findHomeTopTeamMemberAnnouncements(final int limit);

    Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(
            final List<Long> teamIds);

    Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamId(final Long teamId);

    void deleteAllByIds(final List<Long> announcementIds);

    List<TeamMemberAnnouncement> findAllAnnouncementsByTeamId(final Long teamId);

    List<TeamMemberAnnouncement> findPublicAnnouncementsByTeamId(final Long teamId);

    List<TeamMemberAnnouncement> findRecentPublicAnnouncementsNotAdvertised(
            final LocalDateTime since);

    TeamMemberAnnouncement updateTeamMemberAnnouncementClosedState(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final boolean isTeamMEmberAnnouncementInProgress);

    void incrementViewCount(final Long announcementId);
}
