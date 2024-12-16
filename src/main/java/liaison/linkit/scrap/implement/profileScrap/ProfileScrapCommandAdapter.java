package liaison.linkit.scrap.implement.profileScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.domain.repository.profileScrap.ProfileScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileScrapCommandAdapter {
    private final ProfileScrapRepository privateScrapRepository;

    public ProfileScrap addProfileScrap(final ProfileScrap profileScrap) {
        return privateScrapRepository.save(profileScrap);
    }

    public void deleteByMemberId(final Long memberId) {
        privateScrapRepository.deleteByMemberId(memberId);
    }

    public void deleteByMemberIdAndEmailId(final Long memberId, final String emailId) {
        privateScrapRepository.deleteByMemberIdAndEmailId(memberId, emailId);
    }

}
