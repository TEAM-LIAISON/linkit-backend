package liaison.linkit.profile.presentation.miniProfile.dto;

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
    public static class SaveMiniProfileRequest {
        // 미확정
        private List<String> positionNames;
        private String cityName;
        private String divisionName;
        private List<String> profileStateNames;
        private boolean isProfilePublic;
    }
}
