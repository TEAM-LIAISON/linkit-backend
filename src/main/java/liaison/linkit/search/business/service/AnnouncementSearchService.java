package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Optional;

import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnnouncementSearchService {

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;

    public AnnouncementListResponseDTO getFeaturedAnnouncements(
            final Optional<Long> optionalMemberId) {
        Pageable pageable = PageRequest.of(0, 6);
        Page<TeamMemberAnnouncement> hotAnnouncements =
                teamMemberAnnouncementQueryAdapter.findHotAnnouncements(pageable);

        List<AnnouncementInformMenu> hotAnnouncementDTOs =
                hotAnnouncements.stream()
                        .map(
                                teamMemberAnnouncement ->
                                        announcementInformMenuAssembler.mapToAnnouncementInformMenu(
                                                teamMemberAnnouncement, optionalMemberId))
                        .toList();

        return AnnouncementListResponseDTO.of(hotAnnouncementDTOs);
    }

    public CursorResponse<AnnouncementInformMenu> searchAnnouncementsWithCursor(
            final Optional<Long> optionalMemberId,
            List<String> subPosition,
            List<String> cityName,
            List<String> projectTypeName,
            List<String> workTypeName,
            AnnouncementSortType sortType,
            CursorRequest cursorRequest) {
        if (isDefaultSearch(subPosition, cityName, projectTypeName, workTypeName, sortType)) {
            List<Long> excludeAnnouncementIds = getExcludeAnnouncementIds();

            CursorResponse<TeamMemberAnnouncement> teamMemberAnnouncements =
                    teamMemberAnnouncementQueryAdapter.findAllExcludingIdsWithCursor(
                            excludeAnnouncementIds, cursorRequest);

            return convertTeamMemberAnnouncementsToDTOs(teamMemberAnnouncements, optionalMemberId);
        } else {
            // 필터링 검색
            CursorResponse<TeamMemberAnnouncement> teamMemberAnnouncements =
                    teamMemberAnnouncementQueryAdapter.findAllByFilteringWithCursor(
                            subPosition,
                            cityName,
                            projectTypeName,
                            workTypeName,
                            sortType,
                            cursorRequest);

            return convertTeamMemberAnnouncementsToDTOs(teamMemberAnnouncements, optionalMemberId);
        }
    }

    /** 기본 검색 여부를 판단합니다. */
    private boolean isDefaultSearch(
            List<String> subPosition,
            List<String> cityName,
            List<String> projectTypeNames,
            List<String> workTypeNames,
            AnnouncementSortType sortType) {

        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (projectTypeNames == null || projectTypeNames.isEmpty())
                && (workTypeNames == null || workTypeNames.isEmpty())
                && (sortType == AnnouncementSortType.LATEST);
    }

    private CursorResponse<AnnouncementInformMenu> convertTeamMemberAnnouncementsToDTOs(
            CursorResponse<TeamMemberAnnouncement> teamMemberAnnouncements,
            Optional<Long> optionalMemberId) {
        List<AnnouncementInformMenu> announcementDTOs =
                teamMemberAnnouncements.getContent().stream()
                        .map(
                                announcement ->
                                        announcementInformMenuAssembler.mapToAnnouncementInformMenu(
                                                announcement, optionalMemberId))
                        .toList();

        return CursorResponse.of(announcementDTOs, teamMemberAnnouncements.getNextCursor());
    }

    /**
     * 제외할 팀 ID 목록 (강제 지정)
     *
     * @return 제외할 팀 ID 목록
     */
    public List<Long> getExcludeAnnouncementIds() {
        // 큐닷 51L
        // 애프터액션 35L
        // 하우스테이너 27L
        // 코지메이커스 37L
        // 독스헌트 4L
        // 앤유 50L
        return List.of(51L, 35L, 27L, 37L, 4L, 50L);
    }
}
