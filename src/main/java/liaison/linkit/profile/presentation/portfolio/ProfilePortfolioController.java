package liaison.linkit.profile.presentation.portfolio;

import jakarta.validation.Valid;
import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItems;
import liaison.linkit.profile.service.ProfilePortfolioService;
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
    public CommonResponse<ProfilePortfolioItems> getProfilePortfolioItems(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 개인 포트폴리오 전체 조회 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profilePortfolioService.getProfilePortfolioItems(accessor.getMemberId()));
    }

    // 포트폴리오 단일 조회
    @GetMapping("/{profilePortfolioId}")
    @MemberOnly
    public CommonResponse<ProfilePortfolioResponseDTO.ProfilePortfolioDetail> getProfilePortfolioDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long profilePortfolioId
    ) {
        log.info("memberId = {}의 포트폴리오 ID = {}에 대한 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), profilePortfolioId);
        return CommonResponse.onSuccess(profilePortfolioService.getProfilePortfolioDetail(accessor.getMemberId(), profilePortfolioId));
    }

    // 포트폴리오 단일 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<ProfilePortfolioResponseDTO.AddProfilePortfolioResponse> addProfilePortfolio(
            @Auth final Accessor accessor,
            @RequestPart @Valid final ProfilePortfolioRequestDTO.AddProfilePortfolioRequest addProfilePortfolioRequest,
            @RequestPart(required = false) MultipartFile projectRepresentImage,
            @RequestPart(required = false) List<MultipartFile> projectSubImages
    ) {
        log.info("memberId = {}의 프로필 포트폴리오 추가 요청이 발생했습니다.", accessor.getMemberId());
        return CommonResponse.onSuccess(profilePortfolioService.addProfilePortfolio(accessor.getMemberId(), addProfilePortfolioRequest, projectRepresentImage, projectSubImages));
    }


    @PostMapping("/{profilePortfolioId}")
    @MemberOnly
    public CommonResponse<ProfilePortfolioResponseDTO.UpdateProfilePortfolioResponse> updateProfilePortfolio(
            @Auth final Accessor accessor,
            @PathVariable final Long profilePortfolioId,
            @RequestPart @Valid final ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest updateProfilePortfolioRequest,
            @RequestPart(required = true) MultipartFile projectRepresentImage,
            @RequestPart(required = false) List<MultipartFile> projectSubImages
    ) {
        log.info("memberId = {}의 profilePortfolioId = {}에 대한 프로필 포트폴리오 수정 요청이 발생했습니다.", accessor.getMemberId(), profilePortfolioId);
        return CommonResponse.onSuccess(
                profilePortfolioService.updateProfilePortfolio(accessor.getMemberId(), profilePortfolioId, updateProfilePortfolioRequest, projectRepresentImage, projectSubImages));
    }


    @DeleteMapping("/{profilePortfolioId}")
    @MemberOnly
    public CommonResponse<ProfilePortfolioResponseDTO.RemoveProfilePortfolioResponse> removeProfilePortfolio(
            @Auth final Accessor accessor,
            @PathVariable final Long profilePortfolioId
    ) {
        log.info("memberId = {}의 profilePortfolioId = {}에 대한 포트폴리오 단일 삭제 요청이 컨트롤러 계층에 발생했습니다.", accessor.getMemberId(), profilePortfolioId);
        return CommonResponse.onSuccess(profilePortfolioService.removeProfilePortfolio(accessor.getMemberId(), profilePortfolioId));
    }
}
