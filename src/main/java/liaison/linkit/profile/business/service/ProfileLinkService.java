package liaison.linkit.profile.business.service;

import java.util.List;

import liaison.linkit.profile.business.mapper.ProfileLinkMapper;
import liaison.linkit.profile.domain.link.ProfileLink;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.link.ProfileLinkCommandAdapter;
import liaison.linkit.profile.implement.link.ProfileLinkQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileLinkService {

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ProfileLinkQueryAdapter profileLinkQueryAdapter;
    private final ProfileLinkCommandAdapter profileLinkCommandAdapter;
    private final ProfileLinkMapper profileLinkMapper;

    @Transactional(readOnly = true)
    public ProfileLinkResponseDTO.ProfileLinkItems getProfileLinkItems(final Long memberId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfileLink> profileLinks =
                profileLinkQueryAdapter.getProfileLinks(profile.getId());

        return profileLinkMapper.toProfileLinkItems(profileLinks);
    }

    public ProfileLinkResponseDTO.ProfileLinkItems updateProfileLinkItems(
            final Long memberId,
            final ProfileLinkRequestDTO.AddProfileLinkRequest addProfileLinkRequest) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 기존에 저장 이력이 존재하는 경우
        if (profileLinkQueryAdapter.existsByProfileId(profile.getId())) {
            // 기존 이력을 모두 삭제한다.
            profileLinkCommandAdapter.removeProfileLinksByProfileId(profile.getId());
            profile.setIsProfileLink(false);
            profile.removeProfileLinkCompletion();
        }

        List<ProfileLink> profileLinks =
                addProfileLinkRequest.getProfileLinkItems().stream()
                        .map(requestItem -> profileLinkMapper.toProfileLink(profile, requestItem))
                        .toList();

        profileLinkCommandAdapter.addProfileLinks(profileLinks);

        if (profileLinkQueryAdapter.existsByProfileId(profile.getId())) {
            profile.setIsProfileLink(true);
            profile.addProfileLinkCompletion();
        }

        return profileLinkMapper.toProfileLinkItems(profileLinks);
    }
}
