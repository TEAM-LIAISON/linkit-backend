package liaison.linkit.profile.domain.repository.profile;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.FlatProfileDTO;

public interface ProfileCustomRepository {

    Optional<Profile> findByMemberId(final Long memberId);

    Optional<Profile> findByEmailId(final String emailId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest);

    // 홈화면
    List<FlatProfileDTO> findHomeTopProfiles(int size);

    // 프로필 완성도가 가장 높은 팀원이에요
    List<FlatProfileDTO> findTopCompletionProfiles(int size);

    // cursor 없는 초기 조회
    List<FlatProfileDTO> findFlatProfilesWithoutCursor(List<Long> excludeProfileIds, int size);

    // cursor 값만 있는 스크롤 조회
    List<FlatProfileDTO> findAllProfilesWithoutFilter(
            List<Long> excludeProfileIds, CursorRequest cursorRequest);

    List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition);

    List<FlatProfileDTO> findFilteredFlatProfilesWithCursor(
            List<String> subPosition,
            List<String> cityName,
            List<String> profileStateName,
            CursorRequest cursorRequest);
}
