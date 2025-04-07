package liaison.linkit.search.presentation;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.CurrentMemberId;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.auth.utils.AuthUtils;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.search.business.model.ProfileSearchCondition;
import liaison.linkit.search.business.service.ProfileSearchService;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.ProfileListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프로필(팀원) 검색을 위한 API Controller
 *
 * <p>프로필 완성도가 높은 팀원 목록 조회와 커서 기반 페이지네이션을 활용한 팀원 검색 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/search")
@Slf4j
public class ProfileSearchController { // 팀원 찾기 컨트롤러

    private final ProfileSearchService profileSearchService;

    /**
     * 프로필 완성도가 높은 팀원 목록을 조회합니다.
     *
     * @param accessor 인증된 사용자 정보
     * @return 팀원 목록을 포함한 공통 응답
     */
    @GetMapping("/featured")
    @Logging(item = "Profile", action = "GET_FEATURED_PROFILES")
    public CommonResponse<ProfileListResponseDTO> getFeaturedProfiles(
            @Auth final Accessor accessor) {
        Optional<Long> memberIdOptional = AuthUtils.extractMemberId(accessor);
        return CommonResponse.onSuccess(profileSearchService.getFeaturedProfiles(memberIdOptional));
    }

    /** 커서 기반 페이지네이션과 필터를 이용해 팀원 검색을 수행합니다. */
    @GetMapping
    public CommonResponse<CursorResponse<ProfileInformMenu>> searchProfiles(
            @CurrentMemberId Optional<Long> memberId,
            CursorRequest cursorRequest,
            ProfileSearchCondition condition) {
        return CommonResponse.onSuccess(
                profileSearchService.searchProfilesWithCursor(memberId, condition, cursorRequest));
    }
}
