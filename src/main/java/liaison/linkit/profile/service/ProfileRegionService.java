package liaison.linkit.profile.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileRegionService {

    final ProfileRegionRepository profileRegionRepository;
    final ProfileRepository profileRepository;
    final RegionRepository regionRepository;

    public void save(
            final Long memberId,
            final ProfileRegionCreateRequest profileRegionCreateRequest
    ) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Region region = regionRepository
                .findRegionByCityNameAndDivisionName(profileRegionCreateRequest.getCityName(), profileRegionCreateRequest.getDivisionName());

        ProfileRegion profileRegion = new ProfileRegion((null), profile, region);

        profileRegionRepository.save(profileRegion);
    }

    @Transactional(readOnly = true)
    public ProfileRegionResponse getProfileRegion(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        ProfileRegion profileRegion = profileRegionRepository.findByProfileId(profileId);

        return new ProfileRegionResponse(profileRegion.getRegion().getCityName(), profileRegion.getRegion().getDivisionName());
    }

}
