package liaison.linkit.search.presentation;

import java.util.List;
import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.search.business.service.ProfileSearchService;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.ProfileListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/search")
@Slf4j
public class ProfileSearchController { // 팀원 찾기 컨트롤러

    private final ProfileSearchService profileSearchService;

    /**
     * 팀원 찾기 주요 팀원 목록 조회 API - 프로필 완성도가 높은 팀원을 조회합니다.
     *
     * @return 프로필 완성도가 높은 팀원 목록
     */
    @GetMapping("/featured")
    public CommonResponse<ProfileListResponseDTO> getFeaturedProfiles(
            @Auth final Accessor accessor) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        log.info("주요 팀원 목록 조회 요청");

        ProfileListResponseDTO featuredProfiles =
                profileSearchService.getFeaturedProfiles(optionalMemberId);

        return CommonResponse.onSuccess(featuredProfiles);
    }

    /**
     * 팀원 검색 엔드포인트
     *
     * @param cursor 마지막으로 조회한 팀의 ID (선택적)
     * @param size 페이지 크기 (기본값: 20)
     * @param subPosition 포지션 소분류 (선택적)
     * @param cityName 시/도 이름 (선택적)
     * @param profileStateName 프로필 상태 (선택적)
     * @return 팀원 목록과 페이지 정보
     */
    @GetMapping
    public CommonResponse<CursorResponse<ProfileResponseDTO.ProfileInformMenu>> searchProfiles(
            @Auth final Accessor accessor,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "subPosition", required = false) List<String> subPosition,
            @RequestParam(value = "cityName", required = false) List<String> cityName,
            @RequestParam(value = "profileStateName", required = false)
                    List<String> profileStateName) {
        // 로그인 여부에 따라 Optional 생성
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        CursorRequest cursorRequest = new CursorRequest(cursor, size);

        CursorResponse<ProfileResponseDTO.ProfileInformMenu> profileSearchResult =
                profileSearchService.searchProfilesWithCursor(
                        optionalMemberId, subPosition, cityName, profileStateName, cursorRequest);

        return CommonResponse.onSuccess(profileSearchResult);
    }
}
