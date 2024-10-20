package liaison.linkit.scrap.domain.repository.privateScrap;

import liaison.linkit.scrap.domain.PrivateScrap;

import java.util.List;

public interface PrivateScrapCustomRepository {
    List<PrivateScrap> findAllByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    void deleteByProfileId(final Long profileId);

    void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId);

    boolean existsByMemberId(final Long memberId);

    boolean existsByProfileId(final Long profileId);

    boolean existsByMemberIdAndProfileId(final Long memberId, final Long profileId);
}
