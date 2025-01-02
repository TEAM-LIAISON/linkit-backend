package liaison.linkit.team.presentation.product.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamProductRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamProductRequest {
        private String productName;
        private String productLineDescription;
        private String productField;
        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;

        @Builder.Default
        private List<TeamProductLinkRequest> teamProductLinks = new ArrayList<>();

        private String productDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamProductRequest {
        private String productName;
        private String productLineDescription;
        private String productField;
        private String productStartDate;
        private String productEndDate;
        private Boolean isProductInProgress;

        @Builder.Default
        private List<TeamProductLinkRequest> teamProductLinks = new ArrayList<>();

        private String productDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductLinkRequest {
        private String productLinkName;
        private String productLinkPath;
    }
}
