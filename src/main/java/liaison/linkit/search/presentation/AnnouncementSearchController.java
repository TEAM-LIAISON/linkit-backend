package liaison.linkit.search.presentation;

import java.util.Optional;

import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.search.business.model.AnnouncementSearchCondition;
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @CurrentMemberId Optional<Long> memberId) {

        AnnouncementListResponseDTO featuredAnnouncements =
                announcementSearchService.getFeaturedAnnouncements(memberId);

        return CommonResponse.onSuccess(featuredAnnouncements);
    }

    /* 커서 기반 페이지네이션과 필터를 이용해 모집 공고 검색을 수행합니다. */

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
                    @CurrentMemberId Optional<Long> memberId,
                    CursorRequest cursorRequest,
                    AnnouncementSearchCondition condition) {

        CursorResponse<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>
                announcementSearchResult =
                        announcementSearchService.searchAnnouncementsWithCursor(
                                memberId, condition, cursorRequest);

        return CommonResponse.onSuccess(announcementSearchResult);
    }
}
