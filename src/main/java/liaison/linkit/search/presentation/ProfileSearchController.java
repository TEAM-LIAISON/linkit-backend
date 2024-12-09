package liaison.linkit.search.presentation;

import liaison.linkit.search.service.ProfileSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/search")
@Slf4j
public class ProfileSearchController { // 팀원 찾기 컨트롤러

    private final ProfileSearchService profileSearchService;

    /**
     * 팀원 검색 엔드포인트
     *
     * @param majorPosition 포지션 대분류 (선택적)
     * @param cityName      시/도 이름 (선택적)
     * @param page          페이지 번호 (기본값: 0)
     * @param size          페이지 크기 (기본값: 20)
     * @return 팀원 목록과 페이지 정보
     */

//    @GetMapping
//    public CommonResponse<Page<ProfileInformMenu>> searchProfiles(
//            @RequestParam(value = "majorPosition", required = false) List<String> majorPosition,
//            @RequestParam(value = "cityName", required = false) String cityName,
//            @RequestParam(value = "profileStateName", required = false) List<String> profileStateName,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "20") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
//        Page<ProfileResponseDTO> profiles = profileSearchService.searchProfiles(emailId, position, cityName, divisionName, pageable);
//        CommonResponse<Page<ProfileResponseDTO>> response = CommonResponse.onSuccess(profiles);
//        return CommonResponse.onSuccess(profileSearchService.searchProfiles());
//    }
}
