package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.announcement.FlatAnnouncementDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnnouncementSearchService {

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final AnnouncementScrapRepository announcementScrapRepository;

    public AnnouncementListResponseDTO getFeaturedAnnouncements(
            final Optional<Long> optionalMemberId) {
        List<AnnouncementInformMenu> topAnnouncementDTOs =
                getTopHotAnnouncements(6, optionalMemberId);
        return AnnouncementListResponseDTO.of(topAnnouncementDTOs);
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
                && (sortType == null || sortType == AnnouncementSortType.LATEST);
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
        return List.of(79L, 82L, 80L, 71L, 70L, 69L);
    }

    private List<AnnouncementInformMenu> getTopHotAnnouncements(
            int limit, Optional<Long> optionalMemberId) {
        List<FlatAnnouncementDTO> raw =
                teamMemberAnnouncementRepository.findTopHotAnnouncements(limit);
        return getAnnouncementInformMenus(limit, optionalMemberId, raw);
    }

    @NotNull
    private List<AnnouncementInformMenu> getAnnouncementInformMenus(
            int limit, Optional<Long> optionalMemberId, List<FlatAnnouncementDTO> raw) {

        List<Long> announcementIds =
                raw.stream()
                        .map(FlatAnnouncementDTO::getTeamMemberAnnouncementId)
                        .distinct()
                        .toList();
        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        announcementScrapRepository
                                                .findScrappedAnnouncementIdsByMember(
                                                        memberId, announcementIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts =
                announcementScrapRepository.countScrapsGroupedByAnnouncement(announcementIds);

        List<AnnouncementInformMenu> menus =
                announcementInformMenuAssembler.assembleAnnouncementInformMenus(
                        raw, scraps, scrapCounts);

        return menus.size() > limit ? menus.subList(0, limit) : menus;
    }
}
