package liaison.linkit.profile.presentation.visit.dto;

import java.util.List;

import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileVisitResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileVisitInforms {
        @Builder.Default private List<ProfileVisitInform> profileVisitInforms = List.of();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileVisitInform {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfileResponseDTO.ProfilePositionDetail profilePositionDetail =
                new ProfileResponseDTO.ProfilePositionDetail();
    }
}
