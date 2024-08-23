package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;

import java.util.List;

public interface MiniProfileKeywordRepositoryCustom {
    List<MiniProfileKeyword> findAllByMiniProfileId(final Long miniProfileId);
    void deleteAllByMiniProfileId(final Long miniProfileId);
}
