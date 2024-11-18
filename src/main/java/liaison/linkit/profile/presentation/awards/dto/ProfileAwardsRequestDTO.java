package liaison.linkit.profile.presentation.awards.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileAwardsRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileAwardsRequest {
        private String awardsName;
        private String awardsRanking;
        private String awardsDate;
        private String awardsOrganizer;
        private String awardsDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileAwardsRequest {
        private String awardsName;
        private String awardsRanking;
        private String awardsDate;
        private String awardsOrganizer;
        private String awardsDescription;
    }
}
