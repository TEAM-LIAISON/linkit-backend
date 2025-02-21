package liaison.linkit.profile.implement.profile;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.currentState.ProfileCurrentStateRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.exception.profile.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class ProfileQueryAdapter {

    private final ProfileRepository profileRepository;
    private final ProfileCurrentStateRepository profileCurrentStateRepository;

    public Profile findById(final Long profileId) {
        return profileRepository.findById(profileId)
            .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByMemberId(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
            .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByEmailId(final String emailId) {
        return profileRepository.findByEmailId(emailId)
            .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public List<ProfileCurrentState> findProfileCurrentStatesByProfileId(final Long profileId) {
        return profileCurrentStateRepository.findProfileCurrentStatesByProfileId(profileId);
    }

    public Page<Profile> findAll(
        final List<String> subPosition,
        final List<String> skillName,
        final List<String> cityName,
        final List<String> profileStateName,
        final Pageable pageable
    ) {
        return profileRepository.findAll(subPosition, skillName, cityName, profileStateName, pageable);
    }

    @Cacheable(
        value = "topCompletionProfiles",
        key = "'topCompletionProfiles'"  // 상수 키를 사용
    )
    public Page<Profile> findTopCompletionProfiles(
        final Pageable pageable
    ) {
        return profileRepository.findTopCompletionProfiles(pageable);
    }

    public Page<Profile> findAllExcludingIds(
        final List<Long> excludeIds,
        final Pageable pageable
    ) {
        return profileRepository.findAllExcludingIds(excludeIds, pageable);
    }

    @Cacheable(
        value = "homeTopProfiles",
        key = "'homeTopProfiles'"  // 상수 키를 사용
    )
    public List<Profile> findHomeTopProfiles(final int limit) {
        return profileRepository.findHomeTopProfiles(limit);
    }
}
