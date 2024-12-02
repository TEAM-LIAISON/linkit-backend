package liaison.linkit.team.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.team.business.product.TeamProductMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.product.ProductSubImage;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.product.ProductLinkCommandAdapter;
import liaison.linkit.team.implement.product.ProductLinkQueryAdapter;
import liaison.linkit.team.implement.product.ProductSubImageCommandAdapter;
import liaison.linkit.team.implement.product.ProductSubImageQueryAdapter;
import liaison.linkit.team.implement.product.TeamProductCommandAdapter;
import liaison.linkit.team.implement.product.TeamProductQueryAdapter;
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


    @Transactional(readOnly = true)
    public TeamProductResponseDTO.TeamProductItems getTeamProductItems(final Long memberId, final String teamName) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 프로덕트 Items 조회 요청이 서비스 계층에 발생했습니다.", memberId, teamName);

        final Team team = teamQueryAdapter.findByTeamName(teamName);

        final List<TeamProduct> teamProducts = teamProductQueryAdapter.getTeamProducts(team.getId());

        final Map<Long, List<ProductLink>> productLinksMap = productLinkQueryAdapter.getProductLinksMap(team.getId());

        return teamProductMapper.toTeamProductItems(teamProducts, productLinksMap);
    }

    @Transactional(readOnly = true)
    public TeamProductResponseDTO.TeamProductDetail getTeamProductDetail(final Long memberId, final String teamName, final Long teamProductId) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 프로덕트 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId, teamName);
        final TeamProduct teamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 해당 포트폴리오(프로젝트)의 연결된 링크 조회
        final List<ProductLink> productLinks = productLinkQueryAdapter.getProductLinks(teamProductId);
        final List<TeamProductLinkResponse> teamProductLinkResponses = teamProductMapper.toTeamProductLinks(productLinks);

        // 해당 포트폴리오(프로젝트)의 연결된 이미지 조회
        final List<String> productSubImagePaths = productSubImageQueryAdapter.getProjectSubImagePaths(teamProductId);
        final TeamProductImages teamProductImages = teamProductMapper.toTeamProductImages(teamProduct.getProductRepresentImagePath(), productSubImagePaths);

        return teamProductMapper.toTeamProductDetail(teamProduct, teamProductLinkResponses, teamProductImages);
    }

    public TeamProductResponseDTO.AddTeamProductResponse addTeamProduct(
            final Long memberId,
            final String teamName,
            final TeamProductRequestDTO.AddTeamProductRequest addTeamProductRequest,
            final MultipartFile productRepresentImage,
            final List<MultipartFile> productSubImages
    ) {
        String productRepresentImagePath = null;

        final Team team = teamQueryAdapter.findByTeamName(teamName);

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

    // 팀 프로덕트 업데이트
    public TeamProductResponseDTO.UpdateTeamProductResponse updateTeamProduct(
            final Long memberId,
            final String teamName,
            final Long teamProductId,
            final TeamProductRequestDTO.UpdateTeamProductRequest updateTeamProductRequest,
            final MultipartFile productRepresentImage,
            final List<MultipartFile> productSubImages
    ) {
        // 1. 기존 포트폴리오 조회
        final TeamProduct existingTeamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 2. DTO를 통해 포트폴리오 업데이트
        final TeamProduct updatedTeamProduct = teamProductCommandAdapter.updateTeamProduct(existingTeamProduct, updateTeamProductRequest);

        // 3. 대표 이미지 처리
        if (productRepresentImage != null && !productRepresentImage.isEmpty()) {
            if (imageValidator.validatingImageUpload(productRepresentImage)) {
                // 기존 대표 이미지 삭제 (선택 사항)
                if (updatedTeamProduct.getProductRepresentImagePath() != null) {
                    s3Uploader.deleteFile(updatedTeamProduct.getProductRepresentImagePath());
                }
                // 새로운 대표 이미지 업로드
                String newRepresentImagePath = s3Uploader.uploadTeamProductRepresentImage(new ImageFile(productRepresentImage));
                updatedTeamProduct.updateProductRepresentImagePath(newRepresentImagePath);
            } else {
                throw new IllegalArgumentException("유효하지 않은 대표 이미지 파일입니다.");
            }
        }

        // 4. 보조 이미지 처리
        List<String> newProductSubImagePaths = new ArrayList<>();
        if (productSubImages != null && !productSubImages.isEmpty()) {
            // 최대 4개까지만 허용
            if (productSubImages.size() > 4) {
                throw new IllegalArgumentException("보조 이미지는 최대 4개까지 첨부할 수 있습니다.");
            }

            // 기존 보조 이미지 삭제 (선택 사항)
            List<ProductSubImage> existingSubImages = productSubImageQueryAdapter.getProjectSubImages(teamProductId);
            if (existingSubImages != null && !existingSubImages.isEmpty()) {
                for (ProductSubImage subImage : existingSubImages) {
                    s3Uploader.deleteFile(subImage.getProductSubImagePath());
                }
                productSubImageCommandAdapter.deleteAll(existingSubImages);
            }

            // 새로운 보조 이미지 업로드 및 저장
            List<ProductSubImage> newProductSubImageEntities = new ArrayList<>();
            for (MultipartFile subImage : productSubImages) {
                if (imageValidator.validatingImageUpload(subImage)) {
                    String subImagePath = s3Uploader.uploadTeamProductSubImage(new ImageFile(subImage));
                    newProductSubImagePaths.add(subImagePath);
                    ProductSubImage productSubImage = ProductSubImage.builder()
                            .teamProduct(updatedTeamProduct)
                            .productSubImagePath(subImagePath)
                            .build();
                    newProductSubImageEntities.add(productSubImage);
                } else {
                    throw new IllegalArgumentException("유효하지 않은 보조 이미지 파일이 포함되어 있습니다.");
                }
            }
            productSubImageCommandAdapter.saveAll(newProductSubImageEntities);
        }

        // 5. PortfolioImages 업데이트
        final TeamProductImages teamProductImages = teamProductMapper.toTeamProductImages(
                updatedTeamProduct.getProductRepresentImagePath(),
                newProductSubImagePaths
        );

        List<ProductLink> existingProductLink = productLinkQueryAdapter.getProductLinks(teamProductId);
        if (existingProductLink != null && !existingProductLink.isEmpty()) {
            productLinkCommandAdapter.deleteAll(existingProductLink);
        }

        // 새로운 역할 및 기여도 추가
        final List<ProductLink> newProductLinks =
                teamProductMapper.toAddProductLinks(updatedTeamProduct, updateTeamProductRequest.getTeamProductLinks());
        productLinkCommandAdapter.addProductLinks(newProductLinks);
        final List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinkResponses = teamProductMapper.toTeamProductLinks(newProductLinks);

        // 8. 응답 DTO 생성 및 반환
        return teamProductMapper.toUpdateTeamProductResponse(
                updatedTeamProduct,
                teamProductLinkResponses,
                teamProductImages
        );
    }


    // 팀 프로덕트 삭제
    public TeamProductResponseDTO.RemoveTeamProductResponse removeTeamProduct(final Long memberId, final String teamName, final Long teamProductId) {
        final Team team = teamQueryAdapter.findByTeamName(teamName);

        final TeamProduct teamProduct = teamProductQueryAdapter.getTeamProduct(teamProductId);

        // 기존 역할 및 기여도 삭제
        List<ProductLink> existingProductLinks = productLinkQueryAdapter.getProductLinks(teamProductId);
        if (existingProductLinks != null && !existingProductLinks.isEmpty()) {
            productLinkCommandAdapter.deleteAll(existingProductLinks);
        }

        // 기존 대표 이미지 삭제 (선택 사항)
        if (teamProduct.getProductRepresentImagePath() != null) {
            s3Uploader.deleteFile(teamProduct.getProductRepresentImagePath());
        }

        // 기존 보조 이미지 삭제 (선택 사항)
        List<ProductSubImage> existingSubImages = productSubImageQueryAdapter.getProjectSubImages(teamProductId);
        if (existingSubImages != null && !existingSubImages.isEmpty()) {
            for (ProductSubImage subImage : existingSubImages) {
                s3Uploader.deleteFile(subImage.getProductSubImagePath());
            }
            productSubImageCommandAdapter.deleteAll(existingSubImages);
        }

        teamProductCommandAdapter.removeTeamProduct(teamProduct);

        return teamProductMapper.toRemoveTeamProduct(teamProductId);
    }
}
