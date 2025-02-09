package liaison.linkit.search.presentation;

import java.util.List;
import java.util.Optional;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.search.business.service.ProfileSearchService;
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
@RequestMapping("/api/v1/profile/search")
@Slf4j
public class ProfileSearchController { // 팀원 찾기 컨트롤러

    private final ProfileSearchService profileSearchService;

    /**
     * 팀원 검색 엔드포인트
     *
     * @param majorPosition    포지션 대분류 (선택적)
     * @param skillName        스킬 이름 (선택적)
     * @param cityName         시/도 이름 (선택적)
     * @param profileStateName 프로필 상태 (선택적)
     * @param page             페이지 번호 (기본값: 0)
     * @param size             페이지 크기 (기본값: 20)
     * @param accessor         인증 정보 (로그인 여부를 판단하기 위한 객체)
     * @return 팀원 목록과 페이지 정보
     */
    @GetMapping
    public CommonResponse<Page<ProfileInformMenu>> searchProfiles(
        @Auth final Accessor accessor,
        @RequestParam(value = "majorPosition", required = false) List<String> majorPosition,
        @RequestParam(value = "skillName", required = false) List<String> skillName,
        @RequestParam(value = "cityName", required = false) List<String> cityName,
        @RequestParam(value = "profileStateName", required = false) List<String> profileStateName,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        // 로그인 여부에 따라 Optional 생성
        Optional<Long> optionalMemberId = accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        // Pageable 객체 한 번만 생성 (정렬 기준도 통일)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        Page<ProfileInformMenu> profiles = profileSearchService.searchProfiles(optionalMemberId, majorPosition, skillName, cityName, profileStateName, pageable);
        return CommonResponse.onSuccess(profiles);
    }
}
