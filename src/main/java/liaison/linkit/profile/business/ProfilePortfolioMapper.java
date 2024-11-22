package liaison.linkit.profile.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioImages;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioSubImage;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectRoleAndContribution;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectSkillName;

@Mapper
public class ProfilePortfolioMapper {

    public ProfilePortfolioResponseDTO.ProfilePortfolioDetail toProfilePortfolioDetail(
            final ProfilePortfolio profilePortfolio,
            final List<ProjectRoleAndContribution> projectRoleAndContributions,
            final List<ProjectSkillName> projectSkillNames,
            final PortfolioImages portfolioImages
    ) {

        return ProfilePortfolioResponseDTO.ProfilePortfolioDetail
                .builder()
                .profilePortfolioId(profilePortfolio.getId())
                .projectName(profilePortfolio.getProjectName())
                .projectLineDescription(profilePortfolio.getProjectLineDescription())
                .projectSize(profilePortfolio.getProjectSize())
                .projectHeadCount(profilePortfolio.getProjectHeadCount())
                .projectTeamComposition(profilePortfolio.getProjectTeamComposition())
                .projectStartDate(profilePortfolio.getProjectStartDate())
                .projectEndDate(profilePortfolio.getProjectEndDate())
                .isProjectInProgress(profilePortfolio.isProjectInProgress())
                .projectRoleAndContributions(projectRoleAndContributions)
                .projectSkillNames(projectSkillNames)
                .projectLink(profilePortfolio.getProjectLink())
                .projectDescription(profilePortfolio.getProjectDescription())
                .portfolioImages(portfolioImages)
                .build();
    }

    public ProfilePortfolioResponseDTO.ProfilePortfolioItem toProfilePortfolioItem(final ProfilePortfolio profilePortfolio, final List<String> projectRoles) {
        return ProfilePortfolioResponseDTO.ProfilePortfolioItem.builder()
                .profilePortfolioId(profilePortfolio.getId())
                .projectName(profilePortfolio.getProjectName())
                .projectLineDescription(profilePortfolio.getProjectLineDescription())
                .projectSize(profilePortfolio.getProjectSize())
                .projectStartDate(profilePortfolio.getProjectStartDate())
                .projectEndDate(profilePortfolio.getProjectEndDate())
                .isProjectInProgress(profilePortfolio.isProjectInProgress())
                .projectRoles(projectRoles)
                .projectRepresentImagePath(profilePortfolio.getProjectRepresentImagePath())
                .build();
    }

    public ProfilePortfolioResponseDTO.ProfilePortfolioItems toProfilePortfolioItems(final List<ProfilePortfolio> profilePortfolios, final Map<Long, List<String>> projectRolesMap) {
        List<ProfilePortfolioItem> items = profilePortfolios.stream()
                .map(profilePortfolio -> {
                    List<String> projectRoles = projectRolesMap.getOrDefault(profilePortfolio.getId(), Collections.emptyList());
                    return toProfilePortfolioItem(profilePortfolio, projectRoles);
                })
                .collect(Collectors.toList());

        return ProfilePortfolioResponseDTO.ProfilePortfolioItems.builder()
                .profilePortfolioItems(items)
                .build();
    }

    public List<ProjectRoleAndContribution> toProjectRoleAndContributions(final List<ProjectRoleContribution> projectRoleContributions) {
        return projectRoleContributions.stream()
                .map(prc -> ProjectRoleAndContribution.builder()
                        .projectRole(prc.getProjectRole())
                        .projectContribution(prc.getProjectContribution())
                        .build())
                .collect(Collectors.toList());
    }

    public PortfolioImages toPortfolioImages(final String projectRepresentImagePath, final List<String> projectSubImagePaths) {
        List<PortfolioSubImage> portfolioSubImages = projectSubImagePaths.stream()
                .map(path -> PortfolioSubImage.builder()
                        .projectSubImagePath(path)
                        .build())
                .collect(Collectors.toList());

        return PortfolioImages.builder()
                .projectRepresentImagePath(projectRepresentImagePath)
                .portfolioSubImages(portfolioSubImages)
                .build();
    }


    public List<ProjectSkillName> toProjectSkillNames(final List<ProjectSkill> projectSkills) {
        return projectSkills.stream()
                .map(ps -> ProjectSkillName.builder()
                        .projectSkillName(ps.getSkill().getSkillName())
                        .build()
                ).collect(Collectors.toList());
    }

}
