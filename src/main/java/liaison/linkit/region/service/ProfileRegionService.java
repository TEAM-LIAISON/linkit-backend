package liaison.linkit.region.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.region.domain.ProfileRegion;
import liaison.linkit.profile.domain.repository.ProfileRegionRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.region.dto.request.ProfileRegionCreateRequest;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import liaison.linkit.region.domain.Region;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileRegionService {

    final ProfileRegionRepository profileRegionRepository;
    final ProfileRepository profileRepository;
    final RegionRepository regionRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // "내 이력서"에 1대 1로 매핑되어 있는 미니 프로필 조회 메서드
    private ProfileRegion getProfileRegion(final Long profileId) {
        return profileRegionRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_REGION_BY_PROFILE_ID));
    }

    // 멤버 아이디로 미니 프로필의 유효성을 검증하는 로직
    public void validateProfileRegionByMember(final Long memberId) {
        if (!profileRegionRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_PROFILE_REGION_BY_MEMBER_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    public void save(
            final Long memberId,
            final ProfileRegionCreateRequest profileRegionCreateRequest
    ) {
        try {
            final Profile profile = getProfile(memberId);

            if (profileRegionRepository.existsByProfileId(profile.getId())) {
                profileRegionRepository.deleteByProfileId(profile.getId());
                profile.updateIsProfileRegion(false);
                profile.updateMemberProfileTypeByCompletion();
            }

            final Region region = regionRepository
                    .findRegionByCityNameAndDivisionName(profileRegionCreateRequest.getCityName(), profileRegionCreateRequest.getDivisionName()
            );

            if (region == null) {
                throw new IllegalArgumentException("Region not found for city: " +
                        profileRegionCreateRequest.getCityName() + " and division: " +
                        profileRegionCreateRequest.getDivisionName());
            }

            ProfileRegion newProfileRegion = new ProfileRegion(null, profile, region);

            profileRegionRepository.save(newProfileRegion);

            profile.updateIsProfileRegion(true);
            profile.updateMemberProfileTypeByCompletion();
        } catch (IllegalArgumentException e) {
            // Handle known exceptions here
            throw e;  // or return a custom response or error code
        } catch (Exception e) {
            // Handle unexpected exceptions
            throw new RuntimeException("An unexpected error occurred while saving profile region data", e);
        }
    }

    @Transactional(readOnly = true)
    public ProfileRegionResponse getPersonalProfileRegion(final Long memberId) {
        final Profile profile = getProfile(memberId);
        ProfileRegion profileRegion = getProfileRegion(profile.getId());
        return new ProfileRegionResponse(profileRegion.getRegion().getCityName(), profileRegion.getRegion().getDivisionName());
    }

}
