package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;

    public Long validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        } else {
            return profileRepository.findByMemberId(memberId).getId();
        }
    }

    @Transactional(readOnly = true)
    public ProfileIntroductionResponse getProfileIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));
        return ProfileIntroductionResponse.profileIntroduction(profile);
    }

    public void update(final Long profileId, final ProfileUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        // 사용자가 입력한 정보로 업데이트한다.
        profile.update(updateRequest);

        // 저장되었으므로, 완성도 상태를 변경한다.
        profile.updateIsIntroduction(true);

        // 상태 판단 함수가 필요하다 -> +- 20에 따른 상태 판단 진행
        profile.updateMemberProfileTypeByCompletion();

        // DB에 저장한다.
        profileRepository.save(profile);
    }

    public void deleteIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        profile.deleteIntroduction();
        profile.updateIsIntroduction(false);
        profile.updateMemberProfileTypeByCompletion();

        profileRepository.save(profile);
    }

}
