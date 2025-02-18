package liaison.linkit.profile.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.business.mapper.ProfilePortfolioMapper;
import liaison.linkit.profile.domain.portfolio.ProjectLink;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.portfolio.ProjectSubImage;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.implement.portfolio.ProjectLinkCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectLinkQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectRoleContributionCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectRoleContributionQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectSkillCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectSkillQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectSubImageCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectSubImageQueryAdapter;
import liaison.linkit.profile.implement.skill.SkillQueryAdapter;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioImages;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectLinkNameAndUrls;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectRoleAndContribution;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectSkillName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfilePortfolioService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProfilePortfolioCommandAdapter profilePortfolioCommandAdapter;

    private final ProjectRoleContributionQueryAdapter projectRoleContributionQueryAdapter;
    private final ProjectRoleContributionCommandAdapter projectRoleContributionCommandAdapter;

    private final ProjectSkillQueryAdapter projectSkillQueryAdapter;
    private final ProjectSkillCommandAdapter projectSkillCommandAdapter;

    private final ProjectLinkQueryAdapter projectLinkQueryAdapter;
    private final ProjectLinkCommandAdapter projectLinkCommandAdapter;

    private final ProjectSubImageQueryAdapter projectSubImageQueryAdapter;
    private final ProjectSubImageCommandAdapter projectSubImageCommandAdapter;

    private final SkillQueryAdapter skillQueryAdapter;

    private final ProfilePortfolioMapper profilePortfolioMapper;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioItems getProfilePortfolioItems(final Long memberId) {
        log.info("memberId = {}의 포트폴리오 Items 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(profile.getId());
        log.info("profilePortfolios = {}가 성공적으로 조회되었습니다.", profilePortfolios);

        final Map<Long, List<String>> projectRolesMap = projectRoleContributionQueryAdapter.getProjectRolesByProfileId(profile.getId());
        log.info("projectRoleMap 조회에 성공했습니다. = {}", projectRolesMap);

        return profilePortfolioMapper.toProfilePortfolioItems(profilePortfolios, projectRolesMap);
    }

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioItems getProfilePortfolioViewItems(final String emailId) {
        final Profile profile = profileQueryAdapter.findByEmailId(emailId);

        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(profile.getId());

        final Map<Long, List<String>> projectRolesMap = projectRoleContributionQueryAdapter.getProjectRolesByProfileId(profile.getId());

        return profilePortfolioMapper.toProfilePortfolioItems(profilePortfolios, projectRolesMap);
    }

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioDetail getProfilePortfolioDetailInLoginState(final Long memberId, final Long profilePortfolioId) {
        log.info("memberId = {}의 포트폴리오 Detail 조회 요청이 서비스 계층에 발생했습니다.", memberId);

        final ProfilePortfolio profilePortfolio = profilePortfolioQueryAdapter.getProfilePortfolio(profilePortfolioId);
        log.info("profilePortfolio = {}가 성공적으로 조회되었습니다.", profilePortfolio);

        // 해당 포트폴리오(프로젝트)의 연결된 역할 및 기여도 조회
        final List<ProjectRoleContribution> projectRoleContributions = projectRoleContributionQueryAdapter.getProjectRoleContributions(profilePortfolioId);
        final List<ProjectRoleAndContribution> projectRoleAndContributions = profilePortfolioMapper.toProjectRoleAndContributions(projectRoleContributions);

        // 해당 포트폴리오(프로젝트)의 연결된 사용 스킬 조회
        final List<ProjectSkill> projectSkills = projectSkillQueryAdapter.getProjectSkills(profilePortfolio.getId());
        final List<ProjectSkillName> projectSkillNames = profilePortfolioMapper.toProjectSkillNames(projectSkills);

        // 해당 포트폴리오(프로젝트)의 연결된 이미지 조회
        final List<String> projectSubImagePaths = projectSubImageQueryAdapter.getProjectSubImagePaths(profilePortfolio.getId());
        final PortfolioImages portfolioImages = profilePortfolioMapper.toPortfolioImages(profilePortfolio.getProjectRepresentImagePath(), projectSubImagePaths);

        final List<ProjectLink> projectLinks = projectLinkQueryAdapter.getProjectLinks(profilePortfolio.getId());
        final List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = profilePortfolioMapper.toProjectLinks(projectLinks);

        return profilePortfolioMapper.toProfilePortfolioDetail(
            profilePortfolio,
            projectRoleAndContributions,
            projectSkillNames,
            projectLinkNameAndUrls,
            portfolioImages
        );
    }

    @Transactional(readOnly = true)
    public ProfilePortfolioResponseDTO.ProfilePortfolioDetail getProfilePortfolioDetailInLogoutState(final Long profilePortfolioId) {
        final ProfilePortfolio profilePortfolio = profilePortfolioQueryAdapter.getProfilePortfolio(profilePortfolioId);
        log.info("profilePortfolio = {}가 성공적으로 조회되었습니다.", profilePortfolio);

        // 해당 포트폴리오(프로젝트)의 연결된 역할 및 기여도 조회
        final List<ProjectRoleContribution> projectRoleContributions = projectRoleContributionQueryAdapter.getProjectRoleContributions(profilePortfolioId);
        final List<ProjectRoleAndContribution> projectRoleAndContributions = profilePortfolioMapper.toProjectRoleAndContributions(projectRoleContributions);

        // 해당 포트폴리오(프로젝트)의 연결된 사용 스킬 조회
        final List<ProjectSkill> projectSkills = projectSkillQueryAdapter.getProjectSkills(profilePortfolio.getId());
        final List<ProjectSkillName> projectSkillNames = profilePortfolioMapper.toProjectSkillNames(projectSkills);

        // 해당 포트폴리오(프로젝트)의 연결된 이미지 조회
        final List<String> projectSubImagePaths = projectSubImageQueryAdapter.getProjectSubImagePaths(profilePortfolio.getId());
        final PortfolioImages portfolioImages = profilePortfolioMapper.toPortfolioImages(profilePortfolio.getProjectRepresentImagePath(), projectSubImagePaths);

        // 해당 포트폴리오(프로젝트)의 연결된 프로젝트 링크 조회
        final List<ProjectLink> projectLinks = projectLinkQueryAdapter.getProjectLinks(profilePortfolio.getId());
        final List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = profilePortfolioMapper.toProjectLinks(projectLinks);

        return profilePortfolioMapper.toProfilePortfolioDetail(profilePortfolio, projectRoleAndContributions, projectSkillNames, projectLinkNameAndUrls, portfolioImages);
    }


    public ProfilePortfolioResponseDTO.AddProfilePortfolioResponse addProfilePortfolio(
        final Long memberId,
        final ProfilePortfolioRequestDTO.AddProfilePortfolioRequest addProfilePortfolioRequest,
        final MultipartFile projectRepresentImage,
        final List<MultipartFile> projectSubImages
    ) {
        log.info("memberId = {}의 프로필 포트폴리오 추가 요청이 서비스 계층에 발생했습니다.", memberId);
        String projectRepresentImagePath = null;

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final ProfilePortfolio profilePortfolio = profilePortfolioMapper.toAddProfilePortfolio(profile, addProfilePortfolioRequest);
        final ProfilePortfolio savedProfilePortfolio = profilePortfolioCommandAdapter.addProfilePortfolio(profilePortfolio); // 포트폴리오 객체 우선 저장

        // 대표 이미지 저장
        if (imageValidator.validatingImageUpload(projectRepresentImage)) {
            projectRepresentImagePath = s3Uploader.uploadProfileProjectRepresentImage(new ImageFile(projectRepresentImage));
            savedProfilePortfolio.updateProjectRepresentImagePath(projectRepresentImagePath);
        }

        // 6. 보조 이미지 업로드 및 저장
        List<String> projectSubImagePaths = new ArrayList<>();
        if (projectSubImages != null && !projectSubImages.isEmpty()) {
            // 최대 4개까지만 허용
            if (projectSubImages.size() > 4) {
                throw new IllegalArgumentException("보조 이미지는 최대 4개까지 첨부할 수 있습니다.");
            }

            List<ProjectSubImage> projectSubImageEntities = new ArrayList<>();
            for (MultipartFile subImage : projectSubImages) {
                if (imageValidator.validatingImageUpload(subImage)) {
                    String subImagePath = s3Uploader.uploadProfileProjectSubImage(new ImageFile(subImage));
                    projectSubImagePaths.add(subImagePath);
                    ProjectSubImage projectSubImage = ProjectSubImage.builder()
                        .profilePortfolio(profilePortfolio)
                        .projectSubImagePath(subImagePath)
                        .build();
                    projectSubImageEntities.add(projectSubImage);
                }
            }
            projectSubImageCommandAdapter.saveAll(projectSubImageEntities);
        }

        final PortfolioImages portfolioImages =
            profilePortfolioMapper.toPortfolioImages(projectRepresentImagePath, projectSubImagePaths);

        // 역할 및 기여도 저장
        final List<ProjectRoleContribution> projectRoleContributions =
            profilePortfolioMapper.toAddProjectRoleContributions(savedProfilePortfolio, addProfilePortfolioRequest.getProjectRoleAndContributions());
        final List<ProjectRoleContribution> savedProjectRoleContributions = projectRoleContributionCommandAdapter.addProjectRoleContributions(projectRoleContributions);
        final List<ProjectRoleAndContribution> projectRoleAndContributions = profilePortfolioMapper.toProjectRoleAndContributions(savedProjectRoleContributions);

        // 사용 스킬 저장
        List<String> skillNames = addProfilePortfolioRequest.getProjectSkillNames()
            .stream()
            .map(ProfilePortfolioRequestDTO.ProjectSkillName::getProjectSkillName) // 괄호 제거
            .collect(Collectors.toList());

        final List<Skill> skills = skillQueryAdapter.getSkillsBySkillNames(skillNames);
        final List<ProjectSkill> projectSkills = profilePortfolioMapper.toAddProjectSkills(savedProfilePortfolio, skills);
        projectSkillCommandAdapter.saveAll(projectSkills);
        final List<ProjectSkillName> projectSkillNames = profilePortfolioMapper.toProjectSkillNames(projectSkills);

        // 프로젝트 링크 저장

        final List<ProjectLink> savedProjectLinks = projectLinkCommandAdapter.addProjectLinks(
            profilePortfolioMapper.toAddProjectLinks(savedProfilePortfolio, addProfilePortfolioRequest.getProjectLinkNameAndUrls())
        );
        final List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = profilePortfolioMapper.toProjectLinks(savedProjectLinks);

        if (!profile.isProfilePortfolio()) {
            profile.setIsProfilePortfolio(true);
            profile.addProfilePortfolioCompletion();
        }

        return profilePortfolioMapper.toAddProfilePortfolioResponse(savedProfilePortfolio, projectRoleAndContributions, projectSkillNames, projectLinkNameAndUrls, portfolioImages);
    }

    @Transactional
    public ProfilePortfolioResponseDTO.UpdateProfilePortfolioResponse updateProfilePortfolio(
        final Long memberId,
        final Long profilePortfolioId,
        final ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest updateProfilePortfolioRequest,
        final MultipartFile projectRepresentImage,
        final List<MultipartFile> projectSubImages
    ) {
        log.info("memberId = {}의 포트폴리오 ID = {} 업데이트 요청이 서비스 계층에 발생했습니다.", memberId, profilePortfolioId);

        // 1. 기존 포트폴리오 조회
        final ProfilePortfolio existingProfilePortfolio = profilePortfolioQueryAdapter.getProfilePortfolio(profilePortfolioId);

        // 2. DTO를 통해 포트폴리오 업데이트 (텍스트 필드 등)
        final ProfilePortfolio updatedProfilePortfolio = profilePortfolioCommandAdapter.updateProfilePortfolio(
            existingProfilePortfolio,
            updateProfilePortfolioRequest
        );

        // =====================================
        // 3) 대표 이미지 처리 (단일 파일)
        // =====================================
        if (projectRepresentImage != null && !projectRepresentImage.isEmpty()) {
            if (!imageValidator.validatingImageUpload(projectRepresentImage)) {
                throw new IllegalArgumentException("유효하지 않은 대표 이미지 파일입니다.");
            }

            // 기존 대표 이미지가 있으면 S3 삭제
            if (updatedProfilePortfolio.getProjectRepresentImagePath() != null) {
                s3Uploader.deleteS3File(updatedProfilePortfolio.getProjectRepresentImagePath());
                log.info("Old represent image deleted: {}", updatedProfilePortfolio.getProjectRepresentImagePath());
            }

            // 새 대표 이미지 업로드
            String newRepresentImagePath = s3Uploader.uploadProfileProjectRepresentImage(
                new ImageFile(projectRepresentImage)
            );
            // DB 반영
            updatedProfilePortfolio.updateProjectRepresentImagePath(newRepresentImagePath);
        }
        // else: 새 대표 이미지가 없으면 기존 대표 이미지를 유지
        // (만약 “대표 이미지를 삭제만” 하고 싶으면, 별도 flag 로직 추가 가능)

        // =====================================
        // 4) 보조(서브) 이미지 부분 업데이트
        // =====================================
        // (4-a) 유지할 서브 이미지 경로(keepPaths)를 DTO에서 추출
        List<String> keepPaths = new ArrayList<>();
        if (updateProfilePortfolioRequest.getPortfolioImages() != null) {
            keepPaths = updateProfilePortfolioRequest.getPortfolioImages().getPortfolioSubImages()
                .stream()
                .map(dto -> dto.getProjectSubImagePath())
                .filter(Objects::nonNull)
                .toList();
        }

        // (4-b) 기존 DB 서브 이미지 목록
        List<ProjectSubImage> existingSubImages = projectSubImageQueryAdapter.getProjectSubImages(profilePortfolioId);

        // (4-c) 기존 중 keepPaths에 없는 것 => 삭제 (DB + S3)
        if (existingSubImages != null && !existingSubImages.isEmpty()) {
            for (ProjectSubImage oldSub : existingSubImages) {
                String oldPath = oldSub.getProjectSubImagePath();
                if (!keepPaths.contains(oldPath)) {
                    s3Uploader.deleteS3File(oldPath);
                    projectSubImageCommandAdapter.delete(oldSub);
                    log.info("Deleted old sub-image: {}", oldPath);
                }
            }
        }

        // (4-d) 새로 업로드할 파일들 처리
        //       (optional) 개수 제한: if (keepPaths.size() + projectSubImages.size() > 4) { ... }
        List<String> newProjectSubImagePaths = new ArrayList<>();
        if (projectSubImages != null && !projectSubImages.isEmpty()) {
            // 최대 4개까지만 허용
            if (keepPaths.size() + projectSubImages.size() > 4) {
                throw new IllegalArgumentException("보조 이미지는 최대 4개까지 첨부할 수 있습니다.");
            }

            List<ProjectSubImage> newProjectSubImageEntities = new ArrayList<>();
            for (MultipartFile subImage : projectSubImages) {
                if (!imageValidator.validatingImageUpload(subImage)) {
                    throw new IllegalArgumentException("유효하지 않은 보조 이미지 파일이 포함되어 있습니다.");
                }
                // 새 파일 업로드
                String subImagePath = s3Uploader.uploadProfileProjectSubImage(new ImageFile(subImage));
                newProjectSubImagePaths.add(subImagePath);

                // DB 엔티티 생성
                ProjectSubImage newSubImage = ProjectSubImage.builder()
                    .profilePortfolio(updatedProfilePortfolio)
                    .projectSubImagePath(subImagePath)
                    .build();
                newProjectSubImageEntities.add(newSubImage);
            }
            projectSubImageCommandAdapter.saveAll(newProjectSubImageEntities);
        }

        // 5. PortfolioImages DTO 생성 (대표+서브 이미지 경로들)
        final PortfolioImages portfolioImages = profilePortfolioMapper.toPortfolioImages(
            updatedProfilePortfolio.getProjectRepresentImagePath(),
            // 기존 유지 경로 + 새로 업로드된 경로를 합쳐서 응답에 넘길 수도 있음
            Stream.concat(
                keepPaths.stream(),
                newProjectSubImagePaths.stream()
            ).toList()
        );

        // =====================================
        // 6) 역할 및 기여도 업데이트 (전부 삭제 후 재추가)
        // =====================================
        // 기존 역할 및 기여도 삭제
        List<ProjectRoleContribution> existingRoleContributions = projectRoleContributionQueryAdapter.getProjectRoleContributions(profilePortfolioId);
        if (existingRoleContributions != null && !existingRoleContributions.isEmpty()) {
            projectRoleContributionCommandAdapter.deleteAll(existingRoleContributions);
        }

        // 새로운 역할 및 기여도 추가
        final List<ProjectRoleContribution> newProjectRoleContributions = profilePortfolioMapper.toAddProjectRoleContributions(
            updatedProfilePortfolio,
            updateProfilePortfolioRequest.getProjectRoleAndContributions()
        );
        projectRoleContributionCommandAdapter.addProjectRoleContributions(newProjectRoleContributions);
        final List<ProjectRoleAndContribution> projectRoleAndContributions =
            profilePortfolioMapper.toProjectRoleAndContributions(newProjectRoleContributions);

        // =====================================
        // 7) 스킬 업데이트 (전부 삭제 후 새로 추가)
        // =====================================
        List<ProjectSkill> existingProjectSkills = projectSkillQueryAdapter.getProjectSkills(profilePortfolioId);
        if (existingProjectSkills != null && !existingProjectSkills.isEmpty()) {
            projectSkillCommandAdapter.deleteAll(existingProjectSkills);
        }

        List<String> newSkillNames = updateProfilePortfolioRequest.getProjectSkillNames()
            .stream()
            .map(ProfilePortfolioRequestDTO.ProjectSkillName::getProjectSkillName)
            .toList();

        final List<Skill> newSkills = skillQueryAdapter.getSkillsBySkillNames(newSkillNames);
        final List<ProjectSkill> newProjectSkills = profilePortfolioMapper.toAddProjectSkills(updatedProfilePortfolio, newSkills);
        projectSkillCommandAdapter.saveAll(newProjectSkills);
        final List<ProjectSkillName> projectSkillNames = profilePortfolioMapper.toProjectSkillNames(newProjectSkills);

        List<ProjectLink> existingProjectLinks = projectLinkQueryAdapter.getProjectLinks(profilePortfolioId);
        if (existingProjectLinks != null && !existingProjectLinks.isEmpty()) {
            projectLinkCommandAdapter.deleteAll(existingProjectLinks);
        }

        final List<ProjectLink> newProjectLinks = profilePortfolioMapper.toAddProjectLinks(
            updatedProfilePortfolio,
            updateProfilePortfolioRequest.getProjectLinkNameAndUrls()
        );
        projectLinkCommandAdapter.addProjectLinks(newProjectLinks);
        final List<ProjectLinkNameAndUrls> projectLinkNameAndUrls = profilePortfolioMapper.toProjectLinks(newProjectLinks);

        // =====================================
        // 8. 응답 DTO 생성 및 반환
        // =====================================
        return profilePortfolioMapper.toUpdateProfilePortfolioResponse(
            updatedProfilePortfolio,
            projectRoleAndContributions,
            projectSkillNames,
            projectLinkNameAndUrls,
            portfolioImages
        );
    }

    public ProfilePortfolioResponseDTO.RemoveProfilePortfolioResponse removeProfilePortfolio(final Long memberId, final Long profilePortfolioId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final ProfilePortfolio profilePortfolio = profilePortfolioQueryAdapter.getProfilePortfolio(profilePortfolioId);
        // 기존 역할 및 기여도 삭제
        List<ProjectRoleContribution> existingRoleContributions = projectRoleContributionQueryAdapter.getProjectRoleContributions(profilePortfolioId);
        if (existingRoleContributions != null && !existingRoleContributions.isEmpty()) {
            projectRoleContributionCommandAdapter.deleteAll(existingRoleContributions);
        }

        // 기존 스킬 삭제
        List<ProjectSkill> existingProjectSkills = projectSkillQueryAdapter.getProjectSkills(profilePortfolioId);
        if (existingProjectSkills != null && !existingProjectSkills.isEmpty()) {
            projectSkillCommandAdapter.deleteAll(existingProjectSkills);
        }

        // 기존 대표 이미지 삭제 (선택 사항)
        if (profilePortfolio.getProjectRepresentImagePath() != null) {
            s3Uploader.deleteS3File(profilePortfolio.getProjectRepresentImagePath());
        }

        // 기존 보조 이미지 삭제 (선택 사항)
        List<ProjectSubImage> existingSubImages = projectSubImageQueryAdapter.getProjectSubImages(profilePortfolioId);
        if (existingSubImages != null && !existingSubImages.isEmpty()) {
            for (ProjectSubImage subImage : existingSubImages) {
                s3Uploader.deleteS3File(subImage.getProjectSubImagePath());
            }
            projectSubImageCommandAdapter.deleteAll(existingSubImages);
        }

        profilePortfolioCommandAdapter.removeProfilePortfolio(profilePortfolio);

        if (!profilePortfolioQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfilePortfolio(false);
            profile.removeProfilePortfolioCompletion();
        }

        return profilePortfolioMapper.toRemoveProfilePortfolio(profilePortfolioId);
    }
}
