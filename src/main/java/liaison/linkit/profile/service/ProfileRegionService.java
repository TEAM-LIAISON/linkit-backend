package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.domain.repository.ProfileRegionRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.ProfileRegionCreateRequest;
import liaison.linkit.profile.dto.response.ProfileRegionResponse;
import liaison.linkit.team.domain.activity.Region;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_REGION_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileRegionService {

    final ProfileRegionRepository profileRegionRepository;
    final ProfileRepository profileRepository;
    final RegionRepository regionRepository;

    public Long validateProfileRegionByMember(final Long memberId) {
        final Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileRegionRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_REGION_WITH_MEMBER);
        } else {
            return profileRegionRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(
            final Long memberId,
            final ProfileRegionCreateRequest profileRegionCreateRequest
    ) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        if (profileRegionRepository.existsByProfileId(profile.getId())) {
            final ProfileRegion savedProfileRegion = profileRegionRepository.findByProfileId(profile.getId());
            profileRegionRepository.delete(savedProfileRegion);
        }

        final Region region = regionRepository
                .findRegionByCityNameAndDivisionName(profileRegionCreateRequest.getCityName(), profileRegionCreateRequest.getDivisionName());
        ProfileRegion newProfileRegion = new ProfileRegion((null), profile, region);
        profileRegionRepository.save(newProfileRegion);

        // 해당 프로필에 활동 위치 및 지역이 생성된 것을 지정해준다.
        profile.updateIsProfileRegion(true);
    }

    @Transactional(readOnly = true)
    public ProfileRegionResponse getProfileRegion(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        ProfileRegion profileRegion = profileRegionRepository.findByProfileId(profileId);

        return new ProfileRegionResponse(profileRegion.getRegion().getCityName(), profileRegion.getRegion().getDivisionName());
    }

}
