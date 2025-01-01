package liaison.linkit.team.presentation.product;

import jakarta.validation.Valid;
import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO;
import liaison.linkit.team.business.service.product.TeamProductService;
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
@RequestMapping("/api/v1/team/{teamName}/product")
@Slf4j
public class TeamProductController {

    private final TeamProductService teamProductService;

    // 팀 프로덕트 뷰어 전체 조회
    @GetMapping("/view")
    public CommonResponse<TeamProductResponseDTO.TeamProductViewItems> getTeamProductViewItems(
            @PathVariable final String teamName
    ) {
        return CommonResponse.onSuccess(teamProductService.getTeamProductViewItems(teamName));
    }

    // 팀 프로덕트 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<TeamProductResponseDTO.TeamProductItems> getTeamProductItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 프로덕트 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamProductService.getTeamProductItems(accessor.getMemberId(), teamName));
    }

    // 팀 프로덕트 단일 조회
    @GetMapping("/{teamProductId}")
    @MemberOnly
    public CommonResponse<TeamProductResponseDTO.TeamProductDetail> getTeamProductDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamProductId
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 프로덕트 상세 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamProductService.getTeamProductDetail(accessor.getMemberId(), teamName, teamProductId));
    }

    // 팀 프로덕트 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<TeamProductResponseDTO.AddTeamProductResponse> addTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestPart @Valid final TeamProductRequestDTO.AddTeamProductRequest addTeamProductRequest,
            @RequestPart(required = false) MultipartFile productRepresentImage,
            @RequestPart(required = false) List<MultipartFile> productSubImages
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 프로덕트 단일 생성 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamProductService.addTeamProduct(accessor.getMemberId(), teamName, addTeamProductRequest, productRepresentImage, productSubImages));
    }

    // 팀 프로덕트 수정
    @PostMapping("/{teamProductId}")
    @MemberOnly
    public CommonResponse<TeamProductResponseDTO.UpdateTeamProductResponse> updateTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamProductId,
            @RequestPart @Valid final TeamProductRequestDTO.UpdateTeamProductRequest updateTeamProductRequest,
            @RequestPart(required = false) MultipartFile productRepresentImage,
            @RequestPart(required = false) List<MultipartFile> productSubImages
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 프로덕트 단일 수정 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamProductService.updateTeamProduct(accessor.getMemberId(), teamName, teamProductId, updateTeamProductRequest, productRepresentImage, productSubImages));
    }

    // 팀 프로덕트 삭제
    @DeleteMapping("/{teamProductId}")
    @MemberOnly
    public CommonResponse<TeamProductResponseDTO.RemoveTeamProductResponse> removeTeamProduct(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamProductId
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 프로덕트 단일 삭제 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamProductService.removeTeamProduct(accessor.getMemberId(), teamName, teamProductId));
    }
}
