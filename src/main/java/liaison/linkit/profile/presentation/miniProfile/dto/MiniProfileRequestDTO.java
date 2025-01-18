package liaison.linkit.profile.presentation.miniProfile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MiniProfileRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMiniProfileRequest {
        @NotBlank(message = "포지션 대분류는 필수값입니다.")
        private String majorPosition;

        @NotBlank(message = "포지션 소분류는 필수값입니다.")
        private String subPosition;

        @NotBlank(message = "활동지역 시/도를 입력해주세요.")
        private String cityName;

        @NotBlank(message = "활동지역 시/군/구를 입력해주세요.")
        private String divisionName;

        @NotNull(message = "프로필 상태를 입력해주세요")
        private List<String> profileStateNames;

        @NotNull(message = "프로필 공개 여부를 선택해주세요")
        private Boolean isProfilePublic;
    }
}
