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
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileRequest;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
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
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

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
    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    // 미니 프로필 저장 메서드
    public void save(
            final Long memberId,
            final MiniProfileRequest miniProfileRequest,
            final MultipartFile miniProfileImage
    ) {
        final Profile profile = getProfile(memberId);

        if (miniProfileImage != null) {
            log.info("miniProfileImage가 null이 아닙니다.");
            if (miniProfileRepository.existsByProfileId(profile.getId())) {
                final MiniProfile miniProfile = getMiniProfile(profile.getId());
                s3Uploader.deleteImage(miniProfile.getMiniProfileImg());
                miniProfileKeywordRepository.deleteAllByMiniProfileId(miniProfile.getId());
                miniProfileRepository.deleteByProfileId(profile.getId());
            }

            final String miniProfileImageUrl = saveImage(miniProfileImage);

            final MiniProfile newMiniProfileByImage = MiniProfile.of(
                    profile,
                    miniProfileRequest.getProfileTitle(),
                    miniProfileImageUrl,
                    miniProfileRequest.isActivate()
            );

            final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfileByImage);

            final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                    .map(keyWordName -> new MiniProfileKeyword(null, savedMiniProfile, keyWordName))
                    .toList();

            miniProfileKeywordRepository.saveAll(miniProfileKeywordList);

            profile.updateIsMiniProfile(true);
        } else {
            // 미니 프로필 이미지가 null인 경우
            if (miniProfileRepository.existsByProfileId(profile.getId())) { // 생성 이력이 있는 경우
                // 기존 미니 프로필 조회
                final MiniProfile miniProfile = getMiniProfile(profile.getId());

                // 새로운 미니 프로필 이미지 객체 생성
                final MiniProfile newMiniProfileNoImage = MiniProfile.of(
                        profile,
                        miniProfileRequest.getProfileTitle(),
                        miniProfile.getMiniProfileImg(),
                        miniProfileRequest.isActivate()
                );

                miniProfileRepository.deleteByProfileId(profile.getId());
                miniProfileKeywordRepository.deleteAllByMiniProfileId(miniProfile.getId());

                final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfileNoImage);

                final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                        .map(keyWordName -> new MiniProfileKeyword(null, savedMiniProfile, keyWordName))
                        .toList();
                miniProfileKeywordRepository.saveAll(miniProfileKeywordList);

                profile.updateIsMiniProfile(true);
            } else {                                                        // 신규 생성인 경우
                final MiniProfile newMiniProfile = MiniProfile.of(
                        profile,
                        miniProfileRequest.getProfileTitle(),
                        null,
                        miniProfileRequest.isActivate()
                );
                final MiniProfile savedMiniProfile = miniProfileRepository.save(newMiniProfile);
                final List<MiniProfileKeyword> miniProfileKeywordList = miniProfileRequest.getMyKeywordNames().stream()
                        .map(keyWordName -> new MiniProfileKeyword(savedMiniProfile.getId(), null, keyWordName))
                        .toList();

                miniProfileKeywordRepository.saveAll(miniProfileKeywordList);
                profile.updateIsMiniProfile(true);
            }
        }
    }

    // 내 이력서 미니 프로필 조회 메서드
    @Transactional(readOnly = true)
    public MiniProfileResponse getPersonalMiniProfile(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final MiniProfile miniProfile = getMiniProfile(profile.getId());
        final List<MiniProfileKeyword> miniProfileKeywordList = getMiniProfileKeywords(miniProfile.getId());
        final MemberBasicInform memberBasicInform = getMemberBasicInform(memberId);
        return MiniProfileResponse.personalMiniProfile(miniProfile, miniProfileKeywordList, memberBasicInform.getMemberName());
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
}
