package liaison.linkit.wish.implement.privateWish;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class PrivateWishQueryAdapter {
    private final PrivateWishRepository privateWishRepository;

    public List<PrivateWish> findAllPrivateWish(final Long memberId) {
        return privateWishRepository.findAllByMemberId(memberId);
    }

    public boolean existsByMemberId(final Long memberId) {
        return privateWishRepository.existsByMemberId(memberId);
    }

    public boolean existsByProfileId(final Long profileId) {
        return privateWishRepository.existsByProfileId(profileId);
    }

    public boolean existsByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        return privateWishRepository.existsByMemberIdAndProfileId(memberId, profileId);
    }
}
