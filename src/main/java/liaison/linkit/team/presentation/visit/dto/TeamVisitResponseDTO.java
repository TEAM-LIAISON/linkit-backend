package liaison.linkit.team.presentation.visit.dto;

import java.util.List;

import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamVisitResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamVisitInforms {
        @Builder.Default private List<TeamVisitInform> teamVisitInforms = List.of();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamVisitInform {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfileResponseDTO.ProfilePositionDetail profilePositionDetail =
                new ProfileResponseDTO.ProfilePositionDetail();
    }
}
