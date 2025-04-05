package liaison.linkit.search.presentation;

import java.util.List;
import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/announcement/search")
@Slf4j
public class AnnouncementSearchController {

    private final AnnouncementSearchService announcementSearchService;

    /**
     * 공고 찾기 주요 공고 목록 조회 API - 지금 핫한 공고를 조회합니다.
     *
     * @return 지금 핫한 공고 목록
     */
    @GetMapping("/featured")
    @Logging(
            item = "Announcement_Search",
            action = "GET_ANNOUNCEMENT_SEARCH_INFO",
            includeResult = false)
    public CommonResponse<AnnouncementListResponseDTO> getFeaturedAnnouncements(
            @Auth final Accessor accessor) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        AnnouncementListResponseDTO featuredAnnouncements =
                announcementSearchService.getFeaturedAnnouncements(optionalMemberId);

        return CommonResponse.onSuccess(featuredAnnouncements);
    }

    /**
     * 공고 검색 엔드포인트
     *
     * @param subPosition 포지션 대분류 (선택적)
     * @param cityName 활동 지역 (선택적)
     * @param projectTypeName 프로젝트 유형 (선택적)
     * @param workTypeName 업무 형태 (선택적)
     * @param size 페이지 크기 (기본값: 20)
     * @return 팀원 목록과 페이지 정보
     */
    @GetMapping
    @Logging(
            item = "Announcement_Search_Filter",
            action = "GET_ANNOUNCEMENT_SEARCH_FILTER_INFO",
            includeResult = false)
    public CommonResponse<CursorResponse<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>>
            searchAnnouncements(
                    @Auth final Accessor accessor,
                    @RequestParam(value = "cursor", required = false) String cursor,
                    @RequestParam(value = "size", defaultValue = "100") int size,
                    @RequestParam(value = "subPosition", required = false) List<String> subPosition,
                    @RequestParam(value = "cityName", required = false) List<String> cityName,
                    @RequestParam(value = "projectType", required = false)
                            List<String> projectTypeName,
                    @RequestParam(value = "workType", required = false) List<String> workTypeName,
                    @RequestParam(value = "sortBy", defaultValue = "LATEST")
                            AnnouncementSortType sortType) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        // 공고에서 받는 cursor 값은 teamMemberAnnouncement id를 받는다.
        CursorRequest cursorRequest = new CursorRequest(cursor, size);

        CursorResponse<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>
                announcementSearchResult =
                        announcementSearchService.searchAnnouncementsWithCursor(
                                optionalMemberId,
                                subPosition,
                                cityName,
                                projectTypeName,
                                workTypeName,
                                sortType,
                                cursorRequest);

        return CommonResponse.onSuccess(announcementSearchResult);
    }
}
