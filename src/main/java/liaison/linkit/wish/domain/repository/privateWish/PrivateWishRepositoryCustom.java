package liaison.linkit.wish.domain.repository.privateWish;

import liaison.linkit.wish.domain.PrivateWish;

import java.util.List;

public interface PrivateWishRepositoryCustom {
    List<PrivateWish> findAllByMemberId(final Long memberId);
    void deleteByMemberId(final Long memberId);
    void deleteByProfileId(final Long profileId);
    void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId);
    boolean existsByMemberId(final Long memberId);
    boolean existsByProfileId(final Long profileId);
    boolean findByMemberIdAndProfileId(final Long memberId, final Long profileId);

}
