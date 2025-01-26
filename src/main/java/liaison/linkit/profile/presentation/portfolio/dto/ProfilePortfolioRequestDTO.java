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
        private List<ProfilePortfolioRequestDTO.ProjectSkillName> projectSkillNames = new ArrayList<>();

        private String projectLink;
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
        private List<ProfilePortfolioRequestDTO.ProjectSkillName> projectSkillNames = new ArrayList<>();

        private String projectLink;
        private String projectDescription;
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
    public static class ProjectSkillName {
        private String projectSkillName;
    }
}
