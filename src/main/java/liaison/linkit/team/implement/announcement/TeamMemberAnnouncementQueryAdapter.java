package liaison.linkit.team.implement.announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
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
        return teamMemberAnnouncementRepository
                .findById(teamMemberAnnouncementId)
                .orElseThrow(() -> TeamMemberAnnouncementNotFoundException.EXCEPTION);
    }

    public List<TeamMemberAnnouncement> getAllByTeamIds(final List<Long> teamIds) {
        return teamMemberAnnouncementRepository.getAllByTeamIds(teamIds);
    }

    public TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementRepository.getTeamMemberAnnouncement(teamMemberAnnouncementId);
    }

    public Page<TeamMemberAnnouncement> findAll(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable) {
        return teamMemberAnnouncementRepository.findAll(subPosition, cityName, scaleName, pageable);
    }

    public CursorResponse<TeamMemberAnnouncement> findAllExcludingIdsWithCursor(
            final List<Long> excludeAnnouncementIds, final CursorRequest cursorRequest) {
        return teamMemberAnnouncementRepository.findAllExcludingIdsWithCursor(
                excludeAnnouncementIds, cursorRequest);
    }

    public CursorResponse<TeamMemberAnnouncement> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> projectTypeName,
            final List<String> workTypeName,
            final AnnouncementSortType sortType,
            final CursorRequest cursorRequest) {
        return teamMemberAnnouncementRepository.findAllByFilteringWithCursor(
                subPosition, cityName, projectTypeName, workTypeName, sortType, cursorRequest);
    }

    public Page<TeamMemberAnnouncement> findHotAnnouncements(final Pageable pageable) {
        return teamMemberAnnouncementRepository.findHotAnnouncements(pageable);
    }

    public Page<TeamMemberAnnouncement> findExcludedAnnouncements(
            final List<Long> excludeAnnouncementIds, final Pageable pageable) {
        return teamMemberAnnouncementRepository.findExcludedAnnouncements(
                excludeAnnouncementIds, pageable);
    }

    public List<TeamMemberAnnouncement> findHomeTopTeamMemberAnnouncements(final int limit) {
        return teamMemberAnnouncementRepository.findHomeTopTeamMemberAnnouncements(limit);
    }

    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(
            final List<Long> teamIds) {
        return teamMemberAnnouncementRepository.getAllDeletableTeamMemberAnnouncementsByTeamIds(
                teamIds);
    }

    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamId(
            final Long teamId) {
        return teamMemberAnnouncementRepository.getAllDeletableTeamMemberAnnouncementsByTeamId(
                teamId);
    }

    public List<TeamMemberAnnouncement> findAllAnnouncementsByTeamId(final Long teamId) {
        return teamMemberAnnouncementRepository.findAllAnnouncementsByTeamId(teamId);
    }

    public List<TeamMemberAnnouncement> findPublicAnnouncementsByTeamId(final Long teamId) {
        return teamMemberAnnouncementRepository.findPublicAnnouncementsByTeamId(teamId);
    }

    public List<TeamMemberAnnouncement> findRecentPublicAnnouncementsNotAdvertised(
            final LocalDateTime since) {
        return teamMemberAnnouncementRepository.findRecentPublicAnnouncementsNotAdvertised(since);
    }

    public List<TeamMemberAnnouncement> findAllByIsNotPermanentRecruitment() {
        return teamMemberAnnouncementRepository.findAllByIsNotPermanentRecruitment();
    }
}
