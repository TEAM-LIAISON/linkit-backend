package liaison.linkit.team.presentation.product.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

        @NotBlank(message = "팀 프로덕트 이름을 입력해주세요.")
        @Size(min = 1, message = "팀 프로덕트 이름은 1자 이상 입력해주세요.")
        private String productName;

        @NotBlank(message = "팀 프로덕트 한 줄 소개를 입력해주세요.")
        @Size(min = 1, message = "팀 프로덕트 한 줄 소개는 1자 이상 입력해주세요.")
        private String productLineDescription;

        @NotBlank(message = "팀 프로덕트 분야를 입력해주세요.")
        @Size(min = 1, message = "팀 프로덕트 분야는 1자 이상 입력해주세요.")
        private String productField;

        @NotBlank(message = "팀 프로덕트 시작 기간을 입력해주세요.")
        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String productStartDate;

        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String productEndDate;

        @NotNull(message = "프로젝트 진행 여부를 입력해주세요.")
        private Boolean isProductInProgress;

        @Valid @Builder.Default
        private List<TeamProductLinkRequest> teamProductLinks = new ArrayList<>();

        private String productDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamProductRequest {
        @Size(min = 1, message = "팀 프로덕트 이름은 1자 이상 입력해주세요.")
        private String productName;

        @Size(min = 1, message = "팀 프로덕트 한 줄 소개는 1자 이상 입력해주세요.")
        private String productLineDescription;

        @Size(min = 1, message = "팀 프로덕트 분야는 1자 이상 입력해주세요.")
        private String productField;

        @NotBlank(message = "팀 프로덕트 시작 기간을 입력해주세요.")
        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String productStartDate;

        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String productEndDate;

        private Boolean isProductInProgress;

        @Valid @Builder.Default
        private List<TeamProductLinkRequest> teamProductLinks = new ArrayList<>();

        private String productDescription;

        @Builder.Default private TeamProductImages teamProductImages = new TeamProductImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamProductLinkRequest {

        @NotBlank(message = "링크 이름을 입력해주세요.")
        private String productLinkName;

        @NotBlank(message = "링크 주소를 입력해주세요.")
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
