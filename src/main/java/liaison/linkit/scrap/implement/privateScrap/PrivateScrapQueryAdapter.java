package liaison.linkit.scrap.implement.privateScrap;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.PrivateScrap;
import liaison.linkit.scrap.domain.repository.privateScrap.PrivateScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class PrivateScrapQueryAdapter {
    private final PrivateScrapRepository privateScrapRepository;

    public List<PrivateScrap> findAllPrivateWish(final Long memberId) {
        return privateScrapRepository.findAllByMemberId(memberId);
    }

    public boolean existsByMemberId(final Long memberId) {
        return privateScrapRepository.existsByMemberId(memberId);
    }

    public boolean existsByProfileId(final Long profileId) {
        return privateScrapRepository.existsByProfileId(profileId);
    }

    public boolean existsByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        return privateScrapRepository.existsByMemberIdAndProfileId(memberId, profileId);
    }
}
