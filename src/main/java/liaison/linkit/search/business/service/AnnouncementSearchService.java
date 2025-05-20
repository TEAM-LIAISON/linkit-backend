package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import liaison.linkit.search.business.model.AnnouncementSearchCondition;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.announcement.FlatAnnouncementDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
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

    private static final List<Long> DEFAULT_EXCLUDE_ANNOUNCEMENT_IDS =
            List.of(102L, 101L, 100L, 90L, 98L, 84L);

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
            AnnouncementSearchCondition condition,
            CursorRequest cursorRequest) {
        if (condition.isDefault()) {
            // /api/v1/announcement/search?size=20
            if (!cursorRequest.hasNext()) {
                log.info("Default condition");
                List<AnnouncementInformMenu> results =
                        getFirstPageDefaultAnnouncements(cursorRequest.size(), optionalMemberId);
                Long nextCursor =
                        results.isEmpty()
                                ? null
                                : results.get(results.size() - 1).getTeamMemberAnnouncementId();
                return CursorResponse.of(
                        results, nextCursor != null ? nextCursor.toString() : null);
            }
            // /api/v1/profile/search?cursor={teamMemberAnnouncementId}&size=20
            log.info("Cursor condition");
            List<AnnouncementInformMenu> results =
                    getAllAnnouncementsWithoutFilter(
                            cursorRequest.size(), optionalMemberId, cursorRequest);
            Long nextCursor =
                    results.isEmpty()
                            ? null
                            : results.get(results.size() - 1).getTeamMemberAnnouncementId();
            return CursorResponse.of(results, nextCursor != null ? nextCursor.toString() : null);
        }

        // 필터가 포함된 경우
        log.info("Filtered condition");
        List<AnnouncementInformMenu> results =
                getAllAnnouncementsWithFilter(
                        cursorRequest.size(), optionalMemberId, condition, cursorRequest);
        Long nextCursor =
                results.isEmpty()
                        ? null
                        : results.get(results.size() - 1).getTeamMemberAnnouncementId();
        return CursorResponse.of(results, nextCursor != null ? nextCursor.toString() : null);
    }

    private List<AnnouncementInformMenu> getTopHotAnnouncements(
            int limit, Optional<Long> optionalMemberId) {
        List<FlatAnnouncementDTO> raw =
                teamMemberAnnouncementRepository.findTopHotAnnouncements(limit);
        return getAnnouncementInformMenus(limit, optionalMemberId, raw);
    }

    private List<AnnouncementInformMenu> getAllAnnouncementsWithoutFilter(
            int size, Optional<Long> optionalMemberId, CursorRequest cursorRequest) {
        List<FlatAnnouncementDTO> raw =
                teamMemberAnnouncementRepository.findAllAnnouncementsWithoutFilter(
                        DEFAULT_EXCLUDE_ANNOUNCEMENT_IDS, cursorRequest);
        return getAnnouncementInformMenus(size, optionalMemberId, raw);
    }

    private List<AnnouncementInformMenu> getFirstPageDefaultAnnouncements(
            int limit, Optional<Long> memberId) {
        List<FlatAnnouncementDTO> raw =
                teamMemberAnnouncementRepository.findFlatAnnouncementsWithoutCursor(
                        DEFAULT_EXCLUDE_ANNOUNCEMENT_IDS, limit);
        return getAnnouncementInformMenus(limit, memberId, raw);
    }

    private List<AnnouncementInformMenu> getAllAnnouncementsWithFilter(
            int size,
            Optional<Long> optionalMemberId,
            AnnouncementSearchCondition condition,
            CursorRequest cursorRequest) {
        List<FlatAnnouncementDTO> raw =
                teamMemberAnnouncementRepository.findFilteredFlatAnnouncementsWithCursor(
                        condition.subPosition(),
                        condition.cityName(),
                        condition.projectType(),
                        condition.workType(),
                        condition.sortBy(),
                        cursorRequest);
        return getAnnouncementInformMenus(size, optionalMemberId, raw);
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
