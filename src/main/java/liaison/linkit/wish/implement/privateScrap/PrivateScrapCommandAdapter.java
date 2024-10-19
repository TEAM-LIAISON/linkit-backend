package liaison.linkit.wish.implement.privateScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class PrivateScrapCommandAdapter {
    private final PrivateWishRepository privateWishRepository;

    public PrivateWish create(final PrivateWish privateWish) {
        return privateWishRepository.save(privateWish);
    }

    public void deleteByMemberId(final Long memberId) {
        privateWishRepository.deleteByMemberId(memberId);
    }

    public void deleteByProfileId(final Long profileId) {
        privateWishRepository.deleteByProfileId(profileId);
    }

    public void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        privateWishRepository.deleteByMemberIdAndProfileId(memberId, profileId);
    }

}
