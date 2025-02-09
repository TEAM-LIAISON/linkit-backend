package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSearchService {

    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileInformMenuAssembler profileInformMenuAssembler;


    public Page<ProfileInformMenu> searchProfilesInLoginState(
        final Long memberId,
        List<String> majorPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> profileStateName,
        Pageable pageable
    ) {
        Page<Profile> profiles = profileQueryAdapter.findAll(majorPosition, skillName, cityName, profileStateName, pageable);
        return profiles.map(
            profile -> toSearchProfileInformMenuInLoginState(memberId, profile)
        );
    }

    public Page<ProfileInformMenu> searchProfilesInLogoutState(
        List<String> majorPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> profileStateName,
        Pageable pageable
    ) {
        Page<Profile> profiles = profileQueryAdapter.findAll(majorPosition, skillName, cityName, profileStateName, pageable);
        return profiles.map(this::toSearchProfileInformMenuInLogoutState);
    }

    private ProfileInformMenu toSearchProfileInformMenuInLoginState(
        final Long memberId,
        final Profile targetProfile
    ) {
        return profileInformMenuAssembler.assembleProfileInformMenu(targetProfile, Optional.ofNullable(memberId));
    }

    private ProfileInformMenu toSearchProfileInformMenuInLogoutState(
        final Profile targetProfile
    ) {
        return profileInformMenuAssembler.assembleProfileInformMenu(targetProfile, Optional.empty());
    }

}
