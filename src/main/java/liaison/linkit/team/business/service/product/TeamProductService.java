package liaison.linkit.team.business.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.team.business.mapper.product.TeamProductMapper;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.product.ProductSubImage;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.exception.teamMember.TeamAdminNotRegisteredException;
import liaison.linkit.team.implement.product.ProductLinkCommandAdapter;
import liaison.linkit.team.implement.product.ProductLinkQueryAdapter;
import liaison.linkit.team.implement.product.ProductSubImageCommandAdapter;
import liaison.linkit.team.implement.product.ProductSubImageQueryAdapter;
import liaison.linkit.team.implement.product.TeamProductCommandAdapter;
import liaison.linkit.team.implement.product.TeamProductQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductImages;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductLinkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamProductService {

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamProductQueryAdapter teamProductQueryAdapter;
    private final TeamProductCommandAdapter teamProductCommandAdapter;
    private final TeamProductMapper teamProductMapper;

    private final ProductLinkQueryAdapter productLinkQueryAdapter;
    private final ProductLinkCommandAdapter productLinkCommandAdapter;

    private final ProductSubImageQueryAdapter productSubImageQueryAdapter;
    private final ProductSubImageCommandAdapter productSubImageCommandAdapter;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    @Transactional(readOnly = true)
    public TeamProductResponseDTO.TeamProductViewItems getTeamProductViewItems(final String teamCode) {
        log.info("teamCode = {}에 대한 프로덕트 View Items 조회 요청이 서비스 계층에 발생했습니다.", teamCode);
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        final List<TeamProduct> teamProducts = teamProductQueryAdapter.getTeamProducts(team.getId());
        final Map<Long, List<ProductLink>> productLinksMap = productLinkQueryAdapter.getProductLinksMap(team.getId());
        return teamProductMapper.toTeamProductViewItems(teamProducts, productLinksMap, productSubImageQueryAdapter);
    }

    @Transactional(readOnly = true)
    public TeamProductResponseDTO.TeamProductItems getTeamProductItems(final Long memberId, final String teamCode) {
        log.info("memberId = {}의 teamCode = {}에 대한 프로덕트 Items 조회 요청이 서비스 계층에 발생했습니다.", memberId, teamCode);
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        log.info("team={}", team);
        final List<TeamProduct> teamProducts = teamProductQueryAdapter.getTeamProducts(team.getId());
        log.info("teamProducts={}", teamProducts);
        final Map<Long, List<ProductLink>> productLinksMap = productLinkQueryAdapter.getProductLinksMap(team.getId());
        log.info("productLinksMap={}", productLinksMap);
        return teamProductMapper.toTeamProductItems(teamProducts, productLinksMap);
    }

    @Transactional(readOnly = true)
    public TeamProductResponseDTO.TeamProductDetail getTeamProductDetail(final Long memberId, final String teamCode, final Long teamProductId) {
        log.info("memberId = {}의 teamCode = {}에 대한 프로덕트 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId, teamCode);
        final TeamProduct teamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 해당 포트폴리오(프로젝트)의 연결된 링크 조회
        final List<ProductLink> productLinks = productLinkQueryAdapter.getProductLinks(teamProductId);
        final List<TeamProductLinkResponse> teamProductLinkResponses = teamProductMapper.toTeamProductLinks(productLinks);

        // 해당 포트폴리오(프로젝트)의 연결된 이미지 조회
        final List<String> productSubImagePaths = productSubImageQueryAdapter.getProductSubImagePaths(teamProductId);
        final TeamProductImages teamProductImages = teamProductMapper.toTeamProductImages(teamProduct.getProductRepresentImagePath(), productSubImagePaths);

        return teamProductMapper.toTeamProductDetail(teamProduct, teamProductLinkResponses, teamProductImages);
    }

    public TeamProductResponseDTO.AddTeamProductResponse addTeamProduct(
            final Long memberId,
            final String teamCode,
            final TeamProductRequestDTO.AddTeamProductRequest addTeamProductRequest,
            final MultipartFile productRepresentImage,
            final List<MultipartFile> productSubImages
    ) {
        String productRepresentImagePath = null;

        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(team.getId(), memberId)) {
            throw TeamAdminNotRegisteredException.EXCEPTION;
        }
        final TeamProduct teamProduct = teamProductMapper.toAddTeamProduct(team, addTeamProductRequest);
        final TeamProduct savedTeamProduct = teamProductCommandAdapter.addTeamProduct(teamProduct); // 포트폴리오 객체 우선 저장

        // 대표 이미지 저장
        if (imageValidator.validatingImageUpload(productRepresentImage)) {
            productRepresentImagePath = s3Uploader.uploadTeamProductRepresentImage(new ImageFile(productRepresentImage));
            savedTeamProduct.updateProductRepresentImagePath(productRepresentImagePath);
        }

        // 6. 보조 이미지 업로드 및 저장
        List<String> productSubImagePaths = new ArrayList<>();
        if (productSubImages != null && !productSubImages.isEmpty()) {
            // 최대 4개까지만 허용
            if (productSubImages.size() > 4) {
                throw new IllegalArgumentException("보조 이미지는 최대 4개까지 첨부할 수 있습니다.");
            }

            List<ProductSubImage> productSubImageEntities = new ArrayList<>();
            for (MultipartFile subImage : productSubImages) {
                if (imageValidator.validatingImageUpload(subImage)) {
                    String subImagePath = s3Uploader.uploadTeamProductSubImage(new ImageFile(subImage));
                    productSubImagePaths.add(subImagePath);
                    ProductSubImage productSubImage = ProductSubImage.builder()
                            .teamProduct(savedTeamProduct)
                            .productSubImagePath(subImagePath)
                            .build();
                    productSubImageEntities.add(productSubImage);
                }
            }
            productSubImageCommandAdapter.saveAll(productSubImageEntities);
        }

        final TeamProductImages teamProductImages =
                teamProductMapper.toTeamProductImages(productRepresentImagePath, productSubImagePaths);

        // 역할 및 기여도 저장
        final List<ProductLink> productLinks =
                teamProductMapper.toAddProductLinks(savedTeamProduct, addTeamProductRequest.getTeamProductLinks());
        final List<ProductLink> savedProductLinks = productLinkCommandAdapter.addProductLinks(productLinks);
        final List<TeamProductResponseDTO.TeamProductLinkResponse> productLinkResponses = teamProductMapper.toTeamProductLinks(savedProductLinks);

        return teamProductMapper.toAddTeamProductResponse(savedTeamProduct, productLinkResponses, teamProductImages);
    }

    @Transactional
    public TeamProductResponseDTO.UpdateTeamProductResponse updateTeamProduct(
            final Long memberId,
            final String teamCode,
            final Long teamProductId,
            final TeamProductRequestDTO.UpdateTeamProductRequest updateTeamProductRequest,
            final MultipartFile productRepresentImage,
            final List<MultipartFile> productSubImages
    ) {
        // 1) 기존 TeamProduct 조회
        final TeamProduct existingTeamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 2) DTO 업데이트(텍스트 필드 등)
        final TeamProduct updatedTeamProduct =
                teamProductCommandAdapter.updateTeamProduct(existingTeamProduct, updateTeamProductRequest);

        // =====================================
        // 3) 대표 이미지 처리 (단일 파일)
        // =====================================
        if (productRepresentImage != null && !productRepresentImage.isEmpty()) {
            // 새 대표 이미지가 업로드됨
            if (!imageValidator.validatingImageUpload(productRepresentImage)) {
                throw new IllegalArgumentException("유효하지 않은 대표 이미지 파일입니다.");
            }

            // (3-1) 기존 대표 이미지가 있었다면 S3에서 삭제
            String oldRepresentPath = updatedTeamProduct.getProductRepresentImagePath();
            if (oldRepresentPath != null) {
                s3Uploader.deleteS3File(oldRepresentPath);
                log.info("Old represent image deleted from S3: {}", oldRepresentPath);
            }

            // (3-2) 새 대표 이미지 업로드
            String newRepresentImagePath = s3Uploader.uploadTeamProductRepresentImage(
                    new ImageFile(productRepresentImage)
            );

            // (3-3) DB에 반영
            updatedTeamProduct.updateProductRepresentImagePath(newRepresentImagePath);
        }
        // else: 새 파일이 없으면 기존 대표 이미지를 유지
        // 만약 “대표 이미지를 삭제”만 원한다면, 별도 플래그/로직 처리

        // =====================================
        // 4) 보조(서브) 이미지 부분 업데이트
        // =====================================
        // (4-a) 유지할 서브 이미지 경로
        List<String> keepPaths = new ArrayList<>();
        if (updateTeamProductRequest.getTeamProductImages() != null) {
            keepPaths = updateTeamProductRequest.getTeamProductImages().getProductSubImages()
                    .stream()
                    .map(dto -> dto.getProductSubImagePath())
                    .filter(Objects::nonNull)
                    .toList();
        }

        // (4-b) 기존 DB 서브 이미지 목록
        List<ProductSubImage> existingSubImages = productSubImageQueryAdapter.getProductSubImages(teamProductId);

        // (4-c) 기존 중 keepPaths에 없는 것 => 삭제
        if (existingSubImages != null && !existingSubImages.isEmpty()) {
            for (ProductSubImage oldSub : existingSubImages) {
                String oldPath = oldSub.getProductSubImagePath();
                if (!keepPaths.contains(oldPath)) {
                    s3Uploader.deleteS3File(oldPath);
                    productSubImageCommandAdapter.delete(oldSub);
                    log.info("Deleted sub-image from S3 & DB: {}", oldPath);
                }
            }
        }

        // (4-d) 새로 업로드할 파일들 처리
        // (선택) 총개수 제한: if (keepPaths.size() + productSubImages.size() > 4) { ... }
        List<String> newProductSubImagePaths = new ArrayList<>(); // for response
        if (productSubImages != null && !productSubImages.isEmpty()) {
            List<ProductSubImage> newSubImageEntities = new ArrayList<>();
            for (MultipartFile subImageFile : productSubImages) {
                if (!imageValidator.validatingImageUpload(subImageFile)) {
                    throw new IllegalArgumentException("유효하지 않은 보조 이미지 파일이 포함되어 있습니다.");
                }
                // S3 업로드
                String newPath = s3Uploader.uploadTeamProductSubImage(new ImageFile(subImageFile));
                newProductSubImagePaths.add(newPath);

                // DB entity 생성
                ProductSubImage productSubImage = ProductSubImage.builder()
                        .teamProduct(updatedTeamProduct)
                        .productSubImagePath(newPath)
                        .build();
                newSubImageEntities.add(productSubImage);
            }
            productSubImageCommandAdapter.saveAll(newSubImageEntities);
        }

        // =====================================
        // 5) 링크(ProductLink) 처리 (전부삭제 후 새로추가)
        // =====================================
        List<ProductLink> existingProductLink = productLinkQueryAdapter.getProductLinks(teamProductId);
        if (existingProductLink != null && !existingProductLink.isEmpty()) {
            productLinkCommandAdapter.deleteAll(existingProductLink);
        }

        final List<ProductLink> newProductLinks = teamProductMapper.toAddProductLinks(
                updatedTeamProduct,
                updateTeamProductRequest.getTeamProductLinks()
        );
        productLinkCommandAdapter.addProductLinks(newProductLinks);

        final List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinkResponses =
                teamProductMapper.toTeamProductLinks(newProductLinks);

        // =====================================
        // 6) 응답 DTO 구성
        // =====================================
        final TeamProductImages teamProductImages = teamProductMapper.toTeamProductImages(
                updatedTeamProduct.getProductRepresentImagePath(),
                newProductSubImagePaths
        );

        return teamProductMapper.toUpdateTeamProductResponse(
                updatedTeamProduct,
                teamProductLinkResponses,
                teamProductImages
        );
    }


    // 팀 프로덕트 삭제
    public TeamProductResponseDTO.RemoveTeamProductResponse removeTeamProduct(final String teamCode, final Long teamProductId) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);

        final TeamProduct teamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 기존 역할 및 기여도 삭제
        List<ProductLink> existingProductLinks = productLinkQueryAdapter.getProductLinks(teamProductId);
        if (existingProductLinks != null && !existingProductLinks.isEmpty()) {
            productLinkCommandAdapter.deleteAll(existingProductLinks);
        }

        // 기존 대표 이미지 삭제 (선택 사항)
        if (teamProduct.getProductRepresentImagePath() != null) {
            s3Uploader.deleteS3File(teamProduct.getProductRepresentImagePath());
        }

        // 기존 보조 이미지 삭제 (선택 사항)
        List<ProductSubImage> existingSubImages = productSubImageQueryAdapter.getProductSubImages(teamProductId);
        if (existingSubImages != null && !existingSubImages.isEmpty()) {
            for (ProductSubImage subImage : existingSubImages) {
                s3Uploader.deleteS3File(subImage.getProductSubImagePath());
            }
            productSubImageCommandAdapter.deleteAll(existingSubImages);
        }

        teamProductCommandAdapter.removeTeamProduct(teamProduct);

        return teamProductMapper.toRemoveTeamProduct(teamProductId);
    }
}
