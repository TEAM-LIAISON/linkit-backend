package liaison.linkit.scrap.implement.privateScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.PrivateScrap;
import liaison.linkit.scrap.domain.repository.privateScrap.PrivateScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class PrivateScrapCommandAdapter {
    private final PrivateScrapRepository privateScrapRepository;

    public PrivateScrap create(final PrivateScrap privateScrap) {
        return privateScrapRepository.save(privateScrap);
    }

    public void deleteByMemberId(final Long memberId) {
        privateScrapRepository.deleteByMemberId(memberId);
    }

    public void deleteByProfileId(final Long profileId) {
        privateScrapRepository.deleteByProfileId(profileId);
    }

    public void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        privateScrapRepository.deleteByMemberIdAndProfileId(memberId, profileId);
    }

}
