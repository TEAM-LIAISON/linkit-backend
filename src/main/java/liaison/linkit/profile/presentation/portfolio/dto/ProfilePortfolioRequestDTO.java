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
public class ProfilePortfolioRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfilePortfolioRequest {

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
        private List<ProfilePortfolioRequestDTO.ProjectSkillName> projectSkillNames =
                new ArrayList<>();

        @Builder.Default
        private List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = new ArrayList<>();

        private String projectDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfilePortfolioRequest {

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
        private List<ProfilePortfolioRequestDTO.ProjectSkillName> projectSkillNames =
                new ArrayList<>();

        @Builder.Default
        private List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = new ArrayList<>();

        private String projectDescription;

        @Builder.Default private PortfolioImages portfolioImages = new PortfolioImages();
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
    public static class ProjectLinkNameAndUrls {

        private String projectLinkName;
        private String projectLinkUrl;
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
    public static class PortfolioImages {

        private String projectRepresentImagePath; // 대표 이미지

        @Builder.Default private List<PortfolioSubImage> portfolioSubImages = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioSubImage {

        private String projectSubImagePath;
    }
}
