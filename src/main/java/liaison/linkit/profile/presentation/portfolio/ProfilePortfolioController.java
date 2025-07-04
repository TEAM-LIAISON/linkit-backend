package liaison.linkit.profile.presentation.portfolio;

import java.util.List;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfilePortfolioService;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/portfolio")
@Slf4j
public class ProfilePortfolioController {

    private final ProfilePortfolioService profilePortfolioService;

    // 포트폴리오 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(
            item = "Profile_Portfolio",
            action = "GET_PROFILE_PORTFOLIO_ITEMS",
            includeResult = true)
    public CommonResponse<ProfilePortfolioItems> getProfilePortfolioItems(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                profilePortfolioService.getProfilePortfolioItems(accessor.getMemberId()));
    }

    // 포트폴리오 뷰어 전체 조회
    @GetMapping("/view/{emailId}")
    @Logging(
            item = "Profile_Portfolio",
            action = "GET_PROFILE_PORTFOLIO_VIEW_ITEMS",
            includeResult = true)
    public CommonResponse<ProfilePortfolioItems> getProfilePortfolioViewItems(
            @PathVariable final String emailId) {
        return CommonResponse.onSuccess(
                profilePortfolioService.getProfilePortfolioViewItems(emailId));
    }

    // 포트폴리오 단일 조회
    @GetMapping("/{profilePortfolioId}")
    @Logging(
            item = "Profile_Portfolio",
            action = "GET_PROFILE_PORTFOLIO_DETAIL",
            includeResult = true)
    public CommonResponse<ProfilePortfolioResponseDTO.ProfilePortfolioDetail>
            getProfilePortfolioDetail(
                    @Auth final Accessor accessor, @PathVariable final Long profilePortfolioId) {
        if (accessor.isMember()) {
            return CommonResponse.onSuccess(
                    profilePortfolioService.getProfilePortfolioDetailInLoginState(
                            accessor.getMemberId(), profilePortfolioId));
        } else {
            return CommonResponse.onSuccess(
                    profilePortfolioService.getProfilePortfolioDetailInLogoutState(
                            profilePortfolioId));
        }
    }

    // 포트폴리오 단일 생성
    @PostMapping
    @MemberOnly
    @Logging(
            item = "Profile_Portfolio",
            action = "POST_ADD_PROFILE_PORTFOLIO",
            includeResult = true)
    public CommonResponse<ProfilePortfolioResponseDTO.AddProfilePortfolioResponse>
            addProfilePortfolio(
                    @Auth final Accessor accessor,
                    @RequestPart @Valid
                            final ProfilePortfolioRequestDTO.AddProfilePortfolioRequest
                                    addProfilePortfolioRequest,
                    @RequestPart(required = false) MultipartFile projectRepresentImage,
                    @RequestPart(required = false) List<MultipartFile> projectSubImages) {

        return CommonResponse.onSuccess(
                profilePortfolioService.addProfilePortfolio(
                        accessor.getMemberId(),
                        addProfilePortfolioRequest,
                        projectRepresentImage,
                        projectSubImages));
    }

    // 포트폴리오 단일 업데이트
    @PostMapping("/{profilePortfolioId}")
    @MemberOnly
    @Logging(
            item = "Profile_Portfolio",
            action = "POST_UPDATE_PROFILE_PORTFOLIO",
            includeResult = true)
    public CommonResponse<ProfilePortfolioResponseDTO.UpdateProfilePortfolioResponse>
            updateProfilePortfolio(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profilePortfolioId,
                    @RequestPart @Valid
                            final ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest
                                    updateProfilePortfolioRequest,
                    @RequestPart(required = false) MultipartFile projectRepresentImage,
                    @RequestPart(required = false) List<MultipartFile> projectSubImages) {
        return CommonResponse.onSuccess(
                profilePortfolioService.updateProfilePortfolio(
                        accessor.getMemberId(),
                        profilePortfolioId,
                        updateProfilePortfolioRequest,
                        projectRepresentImage,
                        projectSubImages));
    }

    @DeleteMapping("/{profilePortfolioId}")
    @MemberOnly
    @Logging(
            item = "Profile_Portfolio",
            action = "DELETE_REMOVE_PROFILE_PORTFOLIO",
            includeResult = true)
    public CommonResponse<ProfilePortfolioResponseDTO.RemoveProfilePortfolioResponse>
            removeProfilePortfolio(
                    @Auth final Accessor accessor, @PathVariable final Long profilePortfolioId) {
        return CommonResponse.onSuccess(
                profilePortfolioService.removeProfilePortfolio(
                        accessor.getMemberId(), profilePortfolioId));
    }
}
