package liaison.linkit.profile.service;

import java.util.List;
import java.util.Map;
import liaison.linkit.profile.business.ProfilePortfolioMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioImages;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectRoleAndContribution;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectSkillName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfilePortfolioService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProfilePortfolioMapper profilePortfolioMapper;

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioItems getProfilePortfolioItems(final Long memberId) {
        log.info("memberId = {}의 포트폴리오 Items 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(profile.getId());
        log.info("profilePortfolios = {}가 성공적으로 조회되었습니다.", profilePortfolios);

        final Map<Long, List<String>> projectRolesMap = profilePortfolioQueryAdapter.getProjectRolesByProfileId(profile.getId());

        return profilePortfolioMapper.toProfilePortfolioItems(profilePortfolios, projectRolesMap);
    }

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioDetail getProfilePortfolioDetail(final Long memberId, final Long profilePortfolioId) {
        log.info("memberId = {}의 포트폴리오 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfilePortfolio profilePortfolio = profilePortfolioQueryAdapter.getProfilePortfolio(profilePortfolioId);
        log.info("profilePortfolio = {}가 성공적으로 조회되었습니다.", profilePortfolio);

        // 해당 포트폴리오(프로젝트)의 연결된 역할 및 기여도 조회
        final List<ProjectRoleContribution> projectRoleContributions = profilePortfolioQueryAdapter.getProjectRoleContributions(profilePortfolioId);
        final List<ProjectRoleAndContribution> projectRoleAndContributions = profilePortfolioMapper.toProjectRoleAndContributions(projectRoleContributions);

        // 해당 포트폴리오(프로젝트)의 연결된 사용 스킬 조회
        final List<ProjectSkill> projectSkills = profilePortfolioQueryAdapter.getProjectSkills(profilePortfolio.getId());
        final List<ProjectSkillName> projectSkillNames = profilePortfolioMapper.toProjectSkillNames(projectSkills);

        // 해당 포트폴리오(프로젝트)의 연결된 이미지 조회
        final List<String> projectSubImagePaths = profilePortfolioQueryAdapter.getProjectSubImagePaths(profilePortfolio.getId());
        final PortfolioImages portfolioImages = profilePortfolioMapper.toPortfolioImages(profilePortfolio.getProjectRepresentImagePath(), projectSubImagePaths);

        return profilePortfolioMapper.toProfilePortfolioDetail(profilePortfolio, projectRoleAndContributions, projectSkillNames, portfolioImages);
    }
}
