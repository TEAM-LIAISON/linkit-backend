package liaison.linkit.search.presentation;

import java.util.Optional;

import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.model.AnnouncementSearchCondition;
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
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

    @GetMapping("/featured")
    public CommonResponse<AnnouncementListResponseDTO> getFeaturedAnnouncements(
            @CurrentMemberId Optional<Long> memberId) {
        return CommonResponse.onSuccess(
                announcementSearchService.getFeaturedAnnouncements(memberId));
    }

    /* 커서 기반 페이지네이션과 필터를 이용해 공고 검색을 수행합니다. */
    @GetMapping
    public CommonResponse<CursorResponse<AnnouncementInformMenu>> searchAnnouncements(
            @CurrentMemberId Optional<Long> memberId,
            CursorRequest cursorRequest,
            AnnouncementSearchCondition condition) {
        return CommonResponse.onSuccess(
                announcementSearchService.searchAnnouncementsWithCursor(
                        memberId, condition, cursorRequest));
    }
}
