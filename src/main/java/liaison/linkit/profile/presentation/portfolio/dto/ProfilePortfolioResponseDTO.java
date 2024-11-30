package liaison.linkit.profile.presentation.portfolio.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfilePortfolioResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfilePortfolioResponse {
        private String projectName;
        private String projectLineDescription;
        private ProjectSize projectSize;
        private int projectHeadCount;
        private String projectTeamComposition;

        private String projectStartDate;
        private String projectEndDate;
        private Boolean isProjectInProgress;

        @Builder.Default
        private List<ProjectRoleAndContribution> projectRoleAndContributions = new ArrayList<>();

        @Builder.Default
        private List<ProjectSkillName> projectSkillNames = new ArrayList<>();

        private String projectLink;
        private String projectDescription;

        @Builder.Default
        private PortfolioImages portfolioImages = new PortfolioImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfilePortfolioResponse {
        private Long profilePortfolioId;
        private String projectName;
        private String projectLineDescription;
        private ProjectSize projectSize;
        private int projectHeadCount;
        private String projectTeamComposition;

        private String projectStartDate;
        private String projectEndDate;
        private Boolean isProjectInProgress;

        @Builder.Default
        private List<ProjectRoleAndContribution> projectRoleAndContributions = new ArrayList<>();

        @Builder.Default
        private List<ProjectSkillName> projectSkillNames = new ArrayList<>();

        private String projectLink;
        private String projectDescription;

        @Builder.Default
        private PortfolioImages portfolioImages = new PortfolioImages();
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePortfolioItem {

        private Long profilePortfolioId;
        private String projectName;
        private String projectLineDescription;
        private ProjectSize projectSize;

        private String projectStartDate;
        private String projectEndDate;
        private Boolean isProjectInProgress;

        private List<String> projectRoles;

        private String projectRepresentImagePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePortfolioItems {
        @Builder.Default
        private List<ProfilePortfolioItem> profilePortfolioItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfilePortfolioDetail {
        private Long profilePortfolioId;
        private String projectName;
        private String projectLineDescription;
        private ProjectSize projectSize;
        private int projectHeadCount;
        private String projectTeamComposition;

        private String projectStartDate;
        private String projectEndDate;
        private Boolean isProjectInProgress;

        @Builder.Default
        private List<ProjectRoleAndContribution> projectRoleAndContributions = new ArrayList<>();

        @Builder.Default
        private List<ProjectSkillName> projectSkillNames = new ArrayList<>();

        private String projectLink;
        private String projectDescription;

        @Builder.Default
        private PortfolioImages portfolioImages = new PortfolioImages();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectRoleAndContribution {
        private String projectRole;
        private ProjectContribution projectContribution;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioImages {
        private String projectRepresentImagePath; // 대표 이미지

        @Builder.Default
        private List<PortfolioSubImage> portfolioSubImages = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectSkillName {
        private String projectSkillName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioSubImage {
        private String projectSubImagePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfilePortfolioResponse {
        private Long profilePortfolioId;
    }
}
