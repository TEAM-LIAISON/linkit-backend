package liaison.linkit.team.business.mapper.product;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.product.ProductSubImageQueryAdapter;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.AddTeamProductResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.ProductSubImage;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductDetail;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductImages;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductItem;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductItems;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductLinkResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductViewItem;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.UpdateTeamProductResponse;

@Mapper
public class TeamProductMapper {

    public TeamProductResponseDTO.TeamProductDetail toTeamProductDetail(
            final TeamProduct teamProduct,
            final List<TeamProductLinkResponse> teamProductLinkResponses,
            final TeamProductImages teamProductImages) {
        return TeamProductDetail.builder()
                .teamProductId(teamProduct.getId())
                .productName(teamProduct.getProductName())
                .productLineDescription(teamProduct.getProductLineDescription())
                .productField(teamProduct.getProductField())
                .productStartDate(teamProduct.getProductStartDate())
                .productEndDate(teamProduct.getProductEndDate())
                .isProductInProgress(teamProduct.isProductInProgress())
                .teamProductLinks(teamProductLinkResponses)
                .productDescription(teamProduct.getProductDescription())
                .teamProductImages(teamProductImages)
                .build();
    }

    public TeamProductResponseDTO.TeamProductViewItems toTeamProductViewItems(
            final boolean isTeamManager,
            final List<TeamProduct> teamProducts,
            final Map<Long, List<ProductLink>> productLinksMap,
            final ProductSubImageQueryAdapter productSubImageQueryAdapter) {
        // 각 TeamProduct를 순회하며 ViewItem 생성
        List<TeamProductResponseDTO.TeamProductViewItem> items =
                teamProducts.stream()
                        .map(
                                teamProduct -> {
                                    // 1) SubImagePaths 조회
                                    List<String> productSubImagePaths =
                                            productSubImageQueryAdapter.getProductSubImagePaths(
                                                    teamProduct.getId());

                                    // 2) 대표이미지 + 서브이미지를 묶은 TeamProductImages 생성
                                    TeamProductImages teamProductImages =
                                            toTeamProductImages(
                                                    teamProduct.getProductRepresentImagePath(),
                                                    productSubImagePaths);

                                    // 3) ProductLink 목록 조회
                                    List<ProductLink> productLinks =
                                            productLinksMap.getOrDefault(
                                                    teamProduct.getId(), Collections.emptyList());

                                    // 4) ViewItem 생성
                                    return toTeamProductViewItem(
                                            teamProduct, productLinks, teamProductImages);
                                })
                        .toList();

        // 5) ViewItems 래퍼 객체 생성
        return TeamProductResponseDTO.TeamProductViewItems.builder()
                .isTeamManager(isTeamManager)
                .teamProductViewItems(items)
                .build();
    }

    public TeamProductResponseDTO.TeamProductViewItem toTeamProductViewItem(
            final TeamProduct teamProduct,
            final List<ProductLink> productLinks,
            final TeamProductImages teamProductImages) {
        List<TeamProductLinkResponse> linkResponses =
                productLinks.stream()
                        .map(this::toTeamProductLinkResponse)
                        .collect(Collectors.toList());

        return TeamProductViewItem.builder()
                .teamProductId(teamProduct.getId())
                .productName(teamProduct.getProductName())
                .productLineDescription(teamProduct.getProductLineDescription())
                .productField(teamProduct.getProductField())
                .productStartDate(teamProduct.getProductStartDate())
                .productEndDate(teamProduct.getProductEndDate())
                .productRepresentImagePath(teamProduct.getProductRepresentImagePath())
                .isProductInProgress(teamProduct.isProductInProgress())
                .teamProductLinks(linkResponses)
                .productDescription(teamProduct.getProductDescription())
                .teamProductImages(teamProductImages)
                .build();
    }

    public TeamProductResponseDTO.TeamProductItems toTeamProductItems(
            List<TeamProduct> teamProducts, Map<Long, List<ProductLink>> productLinksMap) {
        List<TeamProductItem> items =
                teamProducts.stream()
                        .map(
                                teamProduct -> {
                                    List<ProductLink> productLinks =
                                            productLinksMap.getOrDefault(
                                                    teamProduct.getId(), Collections.emptyList());
                                    return toTeamProductItem(teamProduct, productLinks);
                                })
                        .collect(Collectors.toList());

        return TeamProductItems.builder().teamProductItems(items).build();
    }

    private TeamProductItem toTeamProductItem(
            final TeamProduct teamProduct, final List<ProductLink> productLinks) {
        List<TeamProductLinkResponse> linkResponses =
                productLinks.stream()
                        .map(this::toTeamProductLinkResponse)
                        .collect(Collectors.toList());

        return TeamProductItem.builder()
                .teamProductId(teamProduct.getId())
                .productName(teamProduct.getProductName())
                .productLineDescription(teamProduct.getProductLineDescription())
                .productField(teamProduct.getProductField())
                .productStartDate(teamProduct.getProductStartDate())
                .productEndDate(teamProduct.getProductEndDate())
                .productRepresentImagePath(teamProduct.getProductRepresentImagePath())
                .isProductInProgress(teamProduct.isProductInProgress())
                .teamProductLinks(linkResponses)
                .productDescription(teamProduct.getProductDescription())
                .build();
    }

    public List<ProductLink> toAddProductLinks(
            final TeamProduct teamProduct,
            final List<TeamProductRequestDTO.TeamProductLinkRequest> teamProductLinkRequests) {
        return teamProductLinkRequests.stream()
                .map(
                        productLink ->
                                ProductLink.builder()
                                        .teamProduct(teamProduct)
                                        .productLinkName(productLink.getProductLinkName())
                                        .productLinkPath(productLink.getProductLinkPath())
                                        .build())
                .collect(Collectors.toList());
    }

    public TeamProduct toAddTeamProduct(
            final Team team, final TeamProductRequestDTO.AddTeamProductRequest request) {
        return TeamProduct.builder()
                .id(null)
                .team(team)
                .productName(request.getProductName())
                .productLineDescription(request.getProductLineDescription())
                .productField(request.getProductField())
                .productStartDate(request.getProductStartDate())
                .productEndDate(request.getProductEndDate())
                .isProductInProgress(request.getIsProductInProgress())
                .productDescription(request.getProductDescription())
                .build();
    }

    private TeamProductLinkResponse toTeamProductLinkResponse(final ProductLink productLink) {
        return TeamProductLinkResponse.builder()
                .productLinkId(productLink.getId())
                .productLinkName(productLink.getProductLinkName())
                .productLinkPath(productLink.getProductLinkPath())
                .build();
    }

    public List<TeamProductLinkResponse> toTeamProductLinks(final List<ProductLink> productLinks) {
        return productLinks.stream()
                .map(
                        pls ->
                                TeamProductLinkResponse.builder()
                                        .productLinkId(pls.getId())
                                        .productLinkName(pls.getProductLinkName())
                                        .productLinkPath(pls.getProductLinkPath())
                                        .build())
                .collect(Collectors.toList());
    }

    public TeamProductImages toTeamProductImages(
            final String productRepresentImagePath, final List<String> productSubImagePaths) {
        List<TeamProductResponseDTO.ProductSubImage> productSubImages =
                productSubImagePaths.stream()
                        .map(path -> ProductSubImage.builder().productSubImagePath(path).build())
                        .toList();

        return TeamProductImages.builder()
                .productRepresentImagePath(productRepresentImagePath)
                .productSubImages(productSubImages)
                .build();
    }

    public TeamProductResponseDTO.AddTeamProductResponse toAddTeamProductResponse(
            final TeamProduct teamProduct,
            final List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinkResponses,
            final TeamProductImages teamProductImages) {
        return AddTeamProductResponse.builder()
                .teamProductId(teamProduct.getId())
                .productName(teamProduct.getProductName())
                .productLineDescription(teamProduct.getProductLineDescription())
                .productField(teamProduct.getProductField())
                .productStartDate(teamProduct.getProductStartDate())
                .productEndDate(teamProduct.getProductEndDate())
                .isProductInProgress(teamProduct.isProductInProgress())
                .teamProductLinks(teamProductLinkResponses)
                .productDescription(teamProduct.getProductDescription())
                .teamProductImages(teamProductImages)
                .build();
    }

    public TeamProductResponseDTO.UpdateTeamProductResponse toUpdateTeamProductResponse(
            final TeamProduct teamProduct,
            final List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinkResponses,
            final TeamProductImages teamProductImages) {
        return UpdateTeamProductResponse.builder()
                .teamProductId(teamProduct.getId())
                .productName(teamProduct.getProductName())
                .productLineDescription(teamProduct.getProductLineDescription())
                .productField(teamProduct.getProductField())
                .productStartDate(teamProduct.getProductStartDate())
                .productEndDate(teamProduct.getProductEndDate())
                .isProductInProgress(teamProduct.isProductInProgress())
                .teamProductLinks(teamProductLinkResponses)
                .productDescription(teamProduct.getProductDescription())
                .teamProductImages(teamProductImages)
                .build();
    }

    public TeamProductResponseDTO.RemoveTeamProductResponse toRemoveTeamProduct(
            final Long teamProductId) {
        return TeamProductResponseDTO.RemoveTeamProductResponse.builder()
                .teamProductId(teamProductId)
                .build();
    }
}
