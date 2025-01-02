package liaison.linkit.search.presentation;

import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * 공고 검색 엔드포인트
     *
     * @param majorPosition 포지션 대분류 (선택적)
     * @param skillName     보유 스킬 (선택적)
     * @param cityName      활동 지역 (선택적)
     * @param scaleName     규모 (선택적)
     * @param page          페이지 번호 (기본값: 0)
     * @param size          페이지 크기 (기본값: 20)
     * @return 팀원 목록과 페이지 정보
     */

    @GetMapping
    public CommonResponse<Page<AnnouncementInformMenu>> searchAnnouncements(
            @Auth final Accessor accessor,
            @RequestParam(value = "majorPosition", required = false) List<String> majorPosition,
            @RequestParam(value = "skillName", required = false) List<String> skillName,
            @RequestParam(value = "cityName", required = false) List<String> cityName,
            @RequestParam(value = "scaleName", required = false) List<String> scaleName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        if (accessor.isMember()) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<AnnouncementInformMenu> announcements = announcementSearchService.searchAnnouncementsInLoginState(accessor.getMemberId(), majorPosition, skillName, cityName, scaleName, pageable);
            return CommonResponse.onSuccess(announcements);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            log.info("c e");
            Page<AnnouncementInformMenu> announcements = announcementSearchService.searchAnnouncementsInLogoutState(majorPosition, skillName, cityName, scaleName, pageable);
            return CommonResponse.onSuccess(announcements);
        }
    }
}
