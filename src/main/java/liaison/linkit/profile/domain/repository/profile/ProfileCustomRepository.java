package liaison.linkit.profile.domain.repository.profile;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileCustomRepository {

    Optional<Profile> findByMemberId(final Long memberId);

    Optional<Profile> findByEmailId(final String emailId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    CursorResponse<Profile> findAllExcludingIdsWithCursor(
            final List<Long> excludeIds, final CursorRequest cursorRequest);

    CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest);

    List<Profile> findHomeTopProfiles(final int limit);

    Page<Profile> findTopCompletionProfiles(final Pageable pageable);
}
