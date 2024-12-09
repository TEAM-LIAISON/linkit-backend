package liaison.linkit.profile.domain.repository.profile;

import java.util.Optional;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;

public interface ProfileCustomRepository {

    Optional<Profile> findByMemberId(final Long memberId);

    Optional<Profile> findByEmailId(final String emailId);

    MiniProfileDetailResponse findMiniProfileDetail(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);


}
