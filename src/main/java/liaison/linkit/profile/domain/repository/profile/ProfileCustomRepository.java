package liaison.linkit.profile.domain.repository.profile;

import java.util.Optional;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;

public interface ProfileCustomRepository {

    Optional<Profile> findByMemberId(final Long memberId);

    MiniProfileResponseDTO.MiniProfileDetail findMiniProfileDTO(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);
}
