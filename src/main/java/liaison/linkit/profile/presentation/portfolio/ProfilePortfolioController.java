package liaison.linkit.profile.presentation.portfolio;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItems;
import liaison.linkit.profile.service.ProfilePortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/portfolio")
@Slf4j
public class ProfilePortfolioController {
    private final ProfilePortfolioService profilePortfolioService;

    // 자격증 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<ProfilePortfolioItems> getProfilePortfolioItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 개인 포트폴리오 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profilePortfolioService.getProfilePortfolioItems(accessor.getMemberId()));
    }

    // 자격증 단일 조회
    @GetMapping("/{profilePortfolioId}")
    @MemberOnly
    public CommonResponse<ProfilePortfolioResponseDTO.ProfilePortfolioDetail> getProfilePortfolioDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long profilePortfolioId
    ) {
        log.info("memberId = {}의 포트폴리오 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profilePortfolioId);
        return CommonResponse.onSuccess(profilePortfolioService.getProfilePortfolioDetail(accessor.getMemberId(), profilePortfolioId));
    }
}
