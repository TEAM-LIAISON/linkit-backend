package liaison.linkit.team.domain.repository.announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import liaison.linkit.search.presentation.dto.announcement.FlatAnnouncementDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementDynamicResponse;
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
            final List<String> projectTypeName,
            final List<String> workTypeName,
            final AnnouncementSortType sortType,
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

    List<TeamMemberAnnouncement> findAllByIsNotPermanentRecruitment();

    List<AnnouncementDynamicResponse> findAllDynamicVariablesWithTeamMemberAnnouncement();

    // 홈화면
    List<FlatAnnouncementDTO> findHomeTopAnnouncements(int limit);

    // 지금 핫한 공고에요
    List<FlatAnnouncementDTO> findTopHotAnnouncements(int limit);

    // cursor 없는 초기 조회
    List<FlatAnnouncementDTO> findFlatAnnouncementsWithoutCursor(
            List<Long> excludeProfileIds, int size);

    // cursor 값만 있는 스크롤 조회
    List<FlatAnnouncementDTO> findAllAnnouncementsWithoutFilter(
            List<Long> excludeProfileIds, CursorRequest cursorRequest);

    // cursor 값을 포함하는 필터 있는 스크롤 조회
    List<FlatAnnouncementDTO> findFilteredFlatAnnouncementsWithCursor(
            List<String> subPosition,
            List<String> cityName,
            List<String> projectTypeName,
            List<String> workTypeName,
            AnnouncementSortType sortBy,
            CursorRequest cursorRequest);
}
