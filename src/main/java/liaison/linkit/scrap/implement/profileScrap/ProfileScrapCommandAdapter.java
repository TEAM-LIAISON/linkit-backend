package liaison.linkit.scrap.implement.profileScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.domain.repository.profileScrap.ProfileScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileScrapCommandAdapter {
    private final ProfileScrapRepository profileScrapRepository;

    public ProfileScrap addProfileScrap(final ProfileScrap profileScrap) {
        return profileScrapRepository.save(profileScrap);
    }

    public void deleteByMemberId(final Long memberId) {
        profileScrapRepository.deleteByMemberId(memberId);
    }

    public void deleteByMemberIdAndEmailId(final Long memberId, final String emailId) {
        profileScrapRepository.deleteByMemberIdAndEmailId(memberId, emailId);
    }

    public void deleteAllByMemberId(final Long memberId) {
        profileScrapRepository.deleteAllByMemberId(memberId);
    }

    public void deleteAllByProfileId(final Long profileId) {
        profileScrapRepository.deleteAllByProfileId(profileId);
    }
}
