package liaison.linkit.matching.domain.repository.profileMatching;

import liaison.linkit.matching.domain.ProfileMatching;

import java.util.List;

public interface ProfileMatchingRepositoryCustom {

    List<ProfileMatching> findByProfileIdAndMatchingStatus(final Long profileId);

    List<ProfileMatching> findByMemberIdAndMatchingStatus(final Long memberId);

    List<ProfileMatching> findSuccessReceivedMatching(final Long profileId);

    List<ProfileMatching> findSuccessRequestMatching(final Long memberId);

    boolean existsByProfileId(final Long profileId);

    boolean existsByMemberId(final Long memberId);

    boolean existsNonCheckByMemberId(final Long memberId, final Long profileId);

    void deleteByMemberId(final Long memberId);

    void deleteByProfileId(final Long profileId);

}

