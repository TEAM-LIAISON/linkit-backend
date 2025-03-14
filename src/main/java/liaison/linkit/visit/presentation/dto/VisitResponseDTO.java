package liaison.linkit.visit.presentation.dto;

import java.util.List;

import liaison.linkit.common.presentation.RegionResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitInforms {
        @Builder.Default private List<VisitInform> visitInforms = List.of();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitInform {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfileResponseDTO.ProfilePositionDetail profilePositionDetail =
                new ProfileResponseDTO.ProfilePositionDetail();

        @Builder.Default
        private RegionResponseDTO.RegionDetail regionDetail = new RegionResponseDTO.RegionDetail();
    }
}
