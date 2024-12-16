package liaison.linkit.scrap.domain.repository.profileScrap;

import liaison.linkit.scrap.domain.ProfileScrap;

import java.util.List;

public interface ProfileScrapCustomRepository {
    List<ProfileScrap> findAllByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    void deleteByMemberIdAndEmailId(final Long memberId, final String emailId);

    boolean existsByMemberId(final Long memberId);

    boolean existsByProfileId(final Long profileId);

    boolean existsByMemberIdAndEmailId(final Long memberId, final String emailId);
}
