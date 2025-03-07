package liaison.linkit.team.presentation.product;

import java.util.List;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.product.TeamProductService;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO;
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
@RequestMapping("/api/v1/team/{teamCode}/product")
@Slf4j
public class TeamProductController {

    private final TeamProductService teamProductService;

    // 팀 프로덕트 뷰어 전체 조회
    @GetMapping("/view")
    @Logging(item = "Team_Product", action = "GET_TEAM_PRODUCT_VIEW_ITEMS", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.TeamProductViewItems> getTeamProductViewItems(
            @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(teamProductService.getTeamProductViewItems(teamCode));
    }

    // 팀 프로덕트 전체 조회
    @GetMapping
    @MemberOnly
    @Logging(item = "Team_Product", action = "GET_TEAM_PRODUCT_ITEMS", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.TeamProductItems> getTeamProductItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 프로덕트 전체 조회 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamProductService.getTeamProductItems(accessor.getMemberId(), teamCode));
    }

    // 팀 프로덕트 단일 조회
    @GetMapping("/{teamProductId}")
    @MemberOnly
    @Logging(item = "Team_Product", action = "GET_TEAM_PRODUCT_DETAIL", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.TeamProductDetail> getTeamProductDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamProductId) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 프로덕트 상세 조회 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamProductService.getTeamProductDetail(
                        accessor.getMemberId(), teamCode, teamProductId));
    }

    // 팀 프로덕트 생성
    @PostMapping
    @MemberOnly
    @Logging(item = "Team_Product", action = "POST_ADD_TEAM_PRODUCT", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.AddTeamProductResponse> addTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestPart @Valid
                    final TeamProductRequestDTO.AddTeamProductRequest addTeamProductRequest,
            @RequestPart(required = false) MultipartFile productRepresentImage,
            @RequestPart(required = false) List<MultipartFile> productSubImages) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 프로덕트 단일 생성 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamProductService.addTeamProduct(
                        accessor.getMemberId(),
                        teamCode,
                        addTeamProductRequest,
                        productRepresentImage,
                        productSubImages));
    }

    // 팀 프로덕트 수정
    @PostMapping("/{teamProductId}")
    @MemberOnly
    @Logging(item = "Team_Product", action = "POST_UPDATE_TEAM_PRODUCT", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.UpdateTeamProductResponse> updateTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamProductId,
            @RequestPart @Valid
                    final TeamProductRequestDTO.UpdateTeamProductRequest updateTeamProductRequest,
            @RequestPart(required = false) MultipartFile productRepresentImage,
            @RequestPart(required = false) List<MultipartFile> productSubImages) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 프로덕트 단일 수정 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamProductService.updateTeamProduct(
                        accessor.getMemberId(),
                        teamCode,
                        teamProductId,
                        updateTeamProductRequest,
                        productRepresentImage,
                        productSubImages));
    }

    // 팀 프로덕트 삭제
    @DeleteMapping("/{teamProductId}")
    @MemberOnly
    @Logging(item = "Team_Product", action = "DELETE_REMOVE_TEAM_PRODUCT", includeResult = true)
    public CommonResponse<TeamProductResponseDTO.RemoveTeamProductResponse> removeTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamProductId) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 프로덕트 단일 삭제 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamProductService.removeTeamProduct(teamCode, teamProductId));
    }
}
