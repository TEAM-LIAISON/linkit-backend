package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.MiniProfile;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.MiniProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_MINI_PROFILE_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MINI_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class MiniProfileService {

    private final MiniProfileRepository miniProfileRepository;
    private final ProfileRepository profileRepository;

    public Long validateMiniProfileByMember(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!miniProfileRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        } else {
            return miniProfileRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(final Long memberId,
                     final MiniProfileCreateRequest miniProfileCreateRequest,
                     final MultipartFile multipartFile
    ) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final MiniProfile newMiniProfile = MiniProfile.of(
                profile,
                miniProfileCreateRequest.getProfileTitle(),
                miniProfileCreateRequest.getUploadPeriod(),
                miniProfileCreateRequest.isUploadDeadline(),
                miniProfileCreateRequest.getMiniProfileImg(),
                miniProfileCreateRequest.getMyValue(),
                miniProfileCreateRequest.getSkillSets()
        );

        miniProfileRepository.save(newMiniProfile);
    }

    private MiniProfileResponse getMiniProfileResponse(final MiniProfile miniProfile) {
        return MiniProfileResponse.of(miniProfile);
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfileDetail(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));
        return MiniProfileResponse.personalMiniProfile(miniProfile);
    }

    public void update(final Long miniProfileId, final MiniProfileUpdateRequest miniProfileUpdateRequest) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(()-> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));

        miniProfile.update(miniProfileUpdateRequest);
        miniProfileRepository.save(miniProfile);
    }

    public void delete(final Long miniProfileId){
        if(!miniProfileRepository.existsById(miniProfileId)){
            throw new BadRequestException(NOT_FOUND_MINI_PROFILE_ID);
        }
        miniProfileRepository.deleteById(miniProfileId);
    }
}
