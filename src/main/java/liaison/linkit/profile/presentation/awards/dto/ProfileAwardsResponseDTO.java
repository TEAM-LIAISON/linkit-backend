package liaison.linkit.profile.presentation.awards.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileAwardsResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileAwardsItems {
        @Builder.Default
        private List<ProfileAwardsItem> profileAwardsItems = new ArrayList<>();
    }

    // 전체 조회에 보이는 항목 (이름, 훈격, 수상시기)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileAwardsItem {
        private Long profileAwardsId;
        private String awardsName;
        private String awardsRanking;
        private String awardsDate;
        private Boolean isAwardsVerified;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileAwardsDetail {
        private Long profileAwardsId;
        private String awardsName;
        private String awardsRanking;
        private String awardsDate;
        private String awardsOrganizer;
        private String awardsDescription;

        // 증명서 및 인증 정보
        private Boolean isAwardsCertified;
        private Boolean isAwardsVerified;
        private String awardsCertificationAttachFileName;
        private String awardsCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileAwardsResponse {
        private Long profileAwardsId;
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
    public static class UpdateProfileAwardsResponse {
        private Long profileAwardsId;
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
    public static class ProfileAwardsCertificationResponse {
        private Boolean isAwardsCertified;
        private Boolean isAwardsVerified;
        private String awardsCertificationAttachFileName;
        private String awardsCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileAwardsCertificationResponse {
        private Long profileAwardsId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileAwardsResponse {
        private Long profileAwardsId;
    }
}

