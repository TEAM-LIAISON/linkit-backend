package liaison.linkit.team.presentation.product.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamProductResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductDetail {
        private Long teamProductId;
        private String productName;
        private String productLineDescription;
        private String productField;
        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;

        @Builder.Default
        private List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinks =
                new ArrayList<>();

        private String productDescription;

        @Builder.Default private TeamProductImages teamProductImages = new TeamProductImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductViewItems {
        private Boolean isMyTeam;
        @Builder.Default private List<TeamProductViewItem> teamProductViewItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductViewItem {
        private Long teamProductId;
        private String productName;
        private String productLineDescription;
        private String productField;
        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;
        private String productRepresentImagePath;

        @Builder.Default
        private List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinks =
                new ArrayList<>();

        private String productDescription;

        @Builder.Default private TeamProductImages teamProductImages = new TeamProductImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductItem {
        private Long teamProductId;
        private String productName;
        private String productLineDescription;
        private String productField;
        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;
        private String productRepresentImagePath;

        @Builder.Default
        private List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinks =
                new ArrayList<>();

        private String productDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductItems {
        @Builder.Default private List<TeamProductItem> teamProductItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamProductResponse {
        private Long teamProductId;

        private String productName;
        private String productLineDescription;
        private String productField;

        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;

        @Builder.Default
        private List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinks =
                new ArrayList<>();

        private String productDescription;

        @Builder.Default private TeamProductImages teamProductImages = new TeamProductImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamProductResponse {
        private Long teamProductId;
        private String productName;
        private String productLineDescription;
        private String productField;

        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;

        @Builder.Default
        private List<TeamProductResponseDTO.TeamProductLinkResponse> teamProductLinks =
                new ArrayList<>();

        private String productDescription;

        @Builder.Default private TeamProductImages teamProductImages = new TeamProductImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamProductResponse {
        private Long teamProductId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductLinkResponse {
        private Long productLinkId;
        private String productLinkName;
        private String productLinkPath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductImages {
        private String productRepresentImagePath; // 대표 이미지

        @Builder.Default private List<ProductSubImage> productSubImages = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSubImage {
        private String productSubImagePath;
    }
}
