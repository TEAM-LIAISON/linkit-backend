package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.member.domain.type.MemberProfileType;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileResponse;
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
    public ProfileResponse getProfileDetail(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));
        return ProfileResponse.personalProfile(profile);
    }

    public void update(final Long profileId, final ProfileUpdateRequest updateRequest) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        // 사용자가 입력한 정보로 업데이트한다.
        profile.update(updateRequest);

        // 저장되었으므로, 완성도 상태를 변경한다.
        profile.updateIsIntroduction(true);

        // 상태 판단 함수가 필요하다
        updateMemberProfileTypeByCompletion(profile);

        // DB에 저장한다.
        profileRepository.save(profile);
    }

    public void deleteIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_ID));

        profile.deleteIntroduction();
        profile.updateIsIntroduction(false);
        profileRepository.save(profile);
    }

    private void updateMemberProfileTypeByCompletion(final Profile profile) {
        // 현재 계산된 완성도 값을 호출한다.
        final int presentCompletion = profile.getCompletion();
        // 현재 사용자의 권한 상태를 호출한다.
        final MemberProfileType memberProfileType = profile.getMember().getMemberProfileType();

        if (presentCompletion >= 0 && presentCompletion < 50) {
            if (MemberProfileType.NO_PERMISSION.equals(memberProfileType)) {
                // 아무 조치를 하지 않는다.
                return;
            } else {
                // 같지 않으면 기존에 프로필 열람 및 매칭 요청 권한이 부여된 상태이다.
                // false 전달하여 NO_PERMISSION 상태로 변경을 진행한다.
                profile.getMember().openAndClosePermission(false);
            }
        } else if (presentCompletion >= 50 && presentCompletion < 80) {
            if (MemberProfileType.PROFILE_OPEN_PERMISSION.equals(memberProfileType)) {
                return;
            } else {
                // true 전달하여 PROFILE_OPEN_PERMISSION 상태로 변경한다.
                profile.getMember().openAndClosePermission(true);
            }
        } else {
            if (MemberProfileType.MATCHING_PERMISSION.equals(memberProfileType)) {
                return;
            } else {
                profile.getMember().changeAndOpenPermission(true);
            }
        }
    }
}
