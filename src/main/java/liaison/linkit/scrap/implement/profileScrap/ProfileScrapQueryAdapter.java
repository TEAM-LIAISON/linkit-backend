package liaison.linkit.scrap.implement.profileScrap;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.domain.repository.profileScrap.ProfileScrapRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileScrapQueryAdapter {
    private final ProfileScrapRepository privateScrapRepository;

    public List<ProfileScrap> getAllProfileScrapByMemberId(final Long memberId) {
        return privateScrapRepository.getAllProfileScrapByMemberId(memberId);
    }

    public boolean existsByMemberId(final Long memberId) {
        return privateScrapRepository.existsByMemberId(memberId);
    }

    public boolean existsByProfileId(final Long profileId) {
        return privateScrapRepository.existsByProfileId(profileId);
    }

    public boolean existsByMemberIdAndEmailId(final Long memberId, final String emailId) {
        return privateScrapRepository.existsByMemberIdAndEmailId(memberId, emailId);
    }
}
