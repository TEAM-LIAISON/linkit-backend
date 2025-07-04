package liaison.linkit.scrap.domain.repository.profileScrap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import liaison.linkit.scrap.domain.ProfileScrap;

public interface ProfileScrapCustomRepository {
    List<ProfileScrap> getAllProfileScrapByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    void deleteByMemberIdAndEmailId(final Long memberId, final String emailId);

    void deleteAllByMemberId(final Long memberId);

    void deleteAllByProfileId(final Long profileId);

    boolean existsByMemberId(final Long memberId);

    boolean existsByProfileId(final Long profileId);

    boolean existsByMemberIdAndEmailId(final Long memberId, final String emailId);

    int countTotalProfileScrapByEmailId(final String emailId);

    Set<Long> findScrappedProfileIdsByMember(Long memberId, List<Long> profileIds);

    Map<Long, Integer> countScrapsGroupedByProfile(List<Long> profileIds);
}
