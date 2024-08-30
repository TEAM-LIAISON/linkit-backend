package liaison.linkit.matching.domain.repository.privateMatching;

import liaison.linkit.matching.domain.PrivateMatching;

import java.util.List;

public interface PrivateMatchingRepositoryCustom {

    List<PrivateMatching> findByProfileIdAndMatchingStatus( final Long profileId);
    List<PrivateMatching> findByMemberIdAndMatchingStatus(final Long memberId);
    List<PrivateMatching> findSuccessReceivedMatching(final Long profileId);
    List<PrivateMatching> findSuccessRequestMatching(final Long memberId);

    boolean existsByProfileId(final Long profileId);
    boolean existsByMemberId(final Long memberId);
    boolean existsNonCheckByMemberId(final Long memberId, final Long profileId);

    void deleteByMemberId(final Long memberId);
    void deleteByProfileId(final Long profileId);

}

