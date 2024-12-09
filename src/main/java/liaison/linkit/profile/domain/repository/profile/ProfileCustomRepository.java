package liaison.linkit.profile.domain.repository.profile;

import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileCustomRepository {

    Optional<Profile> findByMemberId(final Long memberId);

    Optional<Profile> findByEmailId(final String emailId);

    MiniProfileDetailResponse findMiniProfileDetail(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    Page<Profile> findAll(
            final List<String> majorPosition,
            final List<String> skillName,
            final List<String> cityName,
            final List<String> profileStateName,
            final Pageable pageable
    );
}
