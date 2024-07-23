package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.S3ImageEvent;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileRequest;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.wish.domain.repository.PrivateWishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MiniProfileService {

    private final ProfileRepository profileRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    private final PrivateWishRepository privateWishRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // "내 이력서"에 1대 1로 매핑되어 있는 미니 프로필 조회 메서드
    private MiniProfile getMiniProfile(final Long profileId) {
        return miniProfileRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_PROFILE_ID));
    }

    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {
        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }

    // 멤버 아이디로 미니 프로필의 유효성을 검증하는 로직
    public void validateMiniProfileByMember(final Long memberId) {
        if (!miniProfileRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_MINI_PROFILE_BY_MEMBER_ID);
        }
    }

    public void validateMiniProfileByTargetProfileId(final Long browseTargetPrivateProfileId) {
        if (!miniProfileRepository.existsByProfileId(browseTargetPrivateProfileId)) {
            throw new AuthException(NOT_FOUND_MINI_PROFILE_BY_PROFILE_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    // 미니 프로필 저장 메서드
    public void save(
            final Long memberId,
            final MiniProfileRequest miniProfileRequest,
            final MultipartFile miniProfileImage
    ) {
        final Profile profile = getProfile(memberId);
        // 미니 프로필 이미지가 같이 전송된 경우
        if (miniProfileImage != null) {
            log.info("요청 객체의 miniProfileImage가 null이 아닙니다.");

            // 미니 프로필이 기존에 존재했었다면
            if (miniProfileRepository.existsByProfileId(profile.getId())) {
                final MiniProfile miniProfile = getMiniProfile(profile.getId());

                if (miniProfile.getMiniProfileImg() != null) {
                    s3Uploader.deleteImage(miniProfile.getMiniProfileImg());
                }

                log.info("miniProfileImage != null case : 기존 미니프로필 이미지 삭제 완료");
                miniProfileKeywordRepository.deleteAllByMiniProfileId(miniProfile.getId());
                log.info("miniProfileImage != null case : 기존 미니프로필 키워드 삭제 완료");
                miniProfileRepository.deleteByProfileId(profile.getId());
                log.info("miniProfileImage != null case : 기존 미니프로필 객체 삭제 완료");
            }

            final String miniProfileImageUrl = saveImage(miniProfileImage);

            final MiniProfile newMiniProfileByImage = MiniProfile.of(
                    profile,
                    miniProfileRequest.getProfileTitle(),
                    miniProfileImageUrl,
                    miniProfileRequest.getIsActivate()
            );

            // 미니 프로필 저장된 객체
            final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfileByImage);

            final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                    .map(keyWordName -> new MiniProfileKeyword(null, savedMiniProfile, keyWordName))
                    .toList();

            miniProfileKeywordRepository.saveAll(miniProfileKeywordList);

            profile.updateIsMiniProfile(true);
        }
        // 요청 객체의 미니 프로필 이미지가 null인 경우
        // 기존 이미지 유지로 간주
        else {
            log.info("요청 객체의 miniProfileImage가 null입니다.");
            // 미니 프로필 객체가 존재했었다면
            if (miniProfileRepository.existsByProfileId(profile.getId())) { // 생성 이력이 있는 경우
                // 기존 미니 프로필 조회
                final MiniProfile miniProfile = getMiniProfile(profile.getId());

                // 새로운 미니 프로필 이미지 객체 생성
                final MiniProfile newMiniProfileNoImage = MiniProfile.of(
                        profile,
                        miniProfileRequest.getProfileTitle(),
                        miniProfile.getMiniProfileImg(),
                        miniProfileRequest.getIsActivate()
                );
                miniProfileKeywordRepository.deleteAllByMiniProfileId(miniProfile.getId());
                miniProfileRepository.deleteByProfileId(profile.getId());

                final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfileNoImage);
                final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                        .map(keyWordName -> new MiniProfileKeyword(null, savedMiniProfile, keyWordName))
                        .toList();

                miniProfileKeywordRepository.saveAll(miniProfileKeywordList);
                profile.updateIsMiniProfile(true);
            }

            // 미니 프로필 객체가 존재하지 않는다면 (새로운 객체)
            else {                                                        // 신규 생성인 경우
                final MiniProfile newMiniProfile = MiniProfile.of(
                        profile,
                        miniProfileRequest.getProfileTitle(),
                        null,
                        miniProfileRequest.getIsActivate()
                );

                final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfile);
                final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                        .map(keyWordName -> new MiniProfileKeyword(null, savedMiniProfile, keyWordName))
                        .toList();

                miniProfileKeywordRepository.saveAll(miniProfileKeywordList);
                profile.updateIsMiniProfile(true);
            }
        }
    }

    // 내 이력서 미니 프로필 조회 메서드
    @Transactional(readOnly = true)
    public MiniProfileResponse getPersonalMiniProfile(final Long memberId) {
        // 대상 객체의 내 이력서 조회
        final Profile profile = getProfile(memberId);

        // 미니 프로필 관련
        final MiniProfile miniProfile = getMiniProfile(profile.getId());
        log.info("대상 객체의 미니 프로필 객체를 조회하였습니다.");
        final List<MiniProfileKeyword> miniProfileKeywordList = getMiniProfileKeywords(miniProfile.getId());
        log.info("대상 객체의 미니 프로필 키워드 리스트를 조회하였습니다.");

        // 이름 관련
        final MemberBasicInform memberBasicInform = getMemberBasicInform(memberId);
        log.info("대상 객체의 회원 기본 정보를 조회하였습니다.");
        // 직무, 역할 관련
        final List<String> jobRoleNames = getJobRoleNames(memberId);
        log.info("대상 객체의 희망 직무 및 역할을 조회하였습니다.");

        return MiniProfileResponse.personalMiniProfile(miniProfile, miniProfileKeywordList, memberBasicInform.getMemberName(), jobRoleNames);
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getBrowsePersonalMiniProfile(final Long memberId, final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

        // 미니 프로필 관련
        final MiniProfile miniProfile = getMiniProfile(profileId);
        log.info("대상 객체의 미니 프로필 객체를 조회하였습니다.");
        final List<MiniProfileKeyword> miniProfileKeywordList = getMiniProfileKeywords(miniProfile.getId());
        log.info("대상 객체의 미니 프로필 키워드 리스트를 조회하였습니다.");

        // 이름 관련
        final MemberBasicInform memberBasicInform = getMemberBasicInform(profile.getMember().getId());
        log.info("대상 객체의 회원 기본 정보를 조회하였습니다.");
        // 직무, 역할 관련
        final List<String> jobRoleNames = getJobRoleNames(profile.getMember().getId());
        log.info("대상 객체의 희망 직무 및 역할을 조회하였습니다.");

        final boolean isPrivateSaved = privateWishRepository.findByMemberIdAndProfileId(memberId, miniProfile.getProfile().getId());

        return MiniProfileResponse.personalBrowseMiniProfile(miniProfile, miniProfileKeywordList, memberBasicInform.getMemberName(), jobRoleNames, isPrivateSaved);
    }

    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
        return profileJobRoleRepository.findAllByProfileId(profileId);
    }

    private List<MiniProfileKeyword> getMiniProfileKeywords(final Long miniProfileId) {
        return miniProfileKeywordRepository.findAllByMiniProfileId(miniProfileId);
    }


    private boolean getSavedImageUrl(final Profile profile) {
            final MiniProfile miniProfile = getMiniProfile(profile.getId());
            if (miniProfile.getMiniProfileImg() != null) {
                return true;
            } else {
                return false;
            }
    }

    // 나중에 리팩토링 필요한 부분

    private String saveImage(final MultipartFile miniProfileImage) {
        // 이미지 유효성 검증
        validateSizeOfImage(miniProfileImage);
        // 이미지 파일 객체 생성 (file, HashedName)
        final ImageFile imageFile = new ImageFile(miniProfileImage);
        // 파일 이름을 반환한다? No! -> 파일 경로를 반환해야함.
        return uploadMiniProfileImage(imageFile);
    }

    private String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        try {
            return s3Uploader.uploadMiniProfileImage(miniProfileImageFile);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(miniProfileImageFile.getHashedName()));
            throw e;
        }
    }

    private void validateSizeOfImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }

    public String getMemberName(final Long memberId) {
        final MemberBasicInform memberBasicInform = getMemberBasicInform(memberId);
        return memberBasicInform.getMemberName();
    }

    public List<String> getJobRoleNames(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(profile.getId());

        List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        return jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();
    }

    public boolean getIsPrivateSaved(final Long memberId, final Long profileId) {
        final MiniProfile miniProfile = getMiniProfile(profileId);
        return privateWishRepository.findByMemberIdAndProfileId(memberId, miniProfile.getProfile().getId());
    }
}
