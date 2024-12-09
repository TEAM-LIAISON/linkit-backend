package liaison.linkit.profile.business;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO.AddProfilePortfolioRequest;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.AddProfilePortfolioResponse;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioImages;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioSubImage;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectRoleAndContribution;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectSkillName;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.RemoveProfilePortfolioResponse;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.UpdateProfilePortfolioResponse;

@Mapper
public class ProfilePortfolioMapper {

    public ProfilePortfolio toAddProfilePortfolio(final Profile profile, final AddProfilePortfolioRequest addProfilePortfolioRequest) {
        return ProfilePortfolio
                .builder()
                .id(null)
                .profile(profile)
                .projectName(addProfilePortfolioRequest.getProjectName())
                .projectLineDescription(addProfilePortfolioRequest.getProjectLineDescription())
                .projectSize(addProfilePortfolioRequest.getProjectSize())
                .projectHeadCount(addProfilePortfolioRequest.getProjectHeadCount())
                .projectTeamComposition(addProfilePortfolioRequest.getProjectTeamComposition())
                .projectStartDate(addProfilePortfolioRequest.getProjectStartDate())
                .projectEndDate(addProfilePortfolioRequest.getProjectEndDate())
                .isProjectInProgress(addProfilePortfolioRequest.getIsProjectInProgress())
                .projectLink(addProfilePortfolioRequest.getProjectLink())
                .projectDescription(addProfilePortfolioRequest.getProjectDescription())
                .build();
    }

    public List<ProjectRoleContribution> toAddProjectRoleContributions(
            final ProfilePortfolio profilePortfolio,
            final List<ProfilePortfolioRequestDTO.ProjectRoleAndContribution> projectRoleAndContributions
    ) {
        return projectRoleAndContributions.stream()
                .map(projectRoleContribution -> ProjectRoleContribution.builder()
                        .profilePortfolio(profilePortfolio)
                        .projectRole(projectRoleContribution.getProjectRole())
                        .projectContribution(projectRoleContribution.getProjectContribution())
                        .build())
                .collect(Collectors.toList());
    }

    public ProfilePortfolioResponseDTO.AddProfilePortfolioResponse toAddProfilePortfolioResponse(
            final ProfilePortfolio profilePortfolio,
            final List<ProjectRoleAndContribution> projectRoleAndContributions,
            final List<ProjectSkillName> projectSkillNames,
            final PortfolioImages portfolioImages
    ) {
        return AddProfilePortfolioResponse
                .builder()
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

    public ProfilePortfolioResponseDTO.UpdateProfilePortfolioResponse toUpdateProfilePortfolioResponse(
            final ProfilePortfolio profilePortfolio,
            final List<ProjectRoleAndContribution> projectRoleAndContributions,
            final List<ProjectSkillName> projectSkillNames,
            final PortfolioImages portfolioImages
    ) {
        return UpdateProfilePortfolioResponse
                .builder()
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

    public List<ProfilePortfolioItem> profilePortfoliosToProfileProfilePortfolioItems(
            final List<ProfilePortfolio> profilePortfolios,
            final Map<Long, List<String>> projectRolesMap
    ) {
        return profilePortfolios.stream()
                .map(profilePortfolio -> {
                    List<String> projectRoles = projectRolesMap.getOrDefault(
                            profilePortfolio.getId(),
                            Collections.emptyList()
                    );
                    return toProfilePortfolioItem(profilePortfolio, projectRoles);
                })
                .collect(Collectors.toList());
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


    public List<ProjectSkill> toAddProjectSkills(
            final ProfilePortfolio profilePortfolio,
            final List<Skill> skills
    ) {
        return skills.stream()
                .map(skill -> ProjectSkill.builder()
                        .id(null)
                        .portfolio(profilePortfolio)
                        .skill(skill)
                        .build())
                .collect(Collectors.toList());
    }

    public ProfilePortfolioResponseDTO.RemoveProfilePortfolioResponse toRemoveProfilePortfolio(
            final Long profilePortfolioId
    ) {
        return RemoveProfilePortfolioResponse.builder()
                .profilePortfolioId(profilePortfolioId)
                .build();
    }
}
