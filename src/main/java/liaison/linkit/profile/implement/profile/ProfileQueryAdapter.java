package liaison.linkit.profile.implement.profile;

import java.util.Collections;
import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.exception.profile.ProfileNotFoundException;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class ProfileQueryAdapter {

    private final ProfileRepository profileRepository;

    public Profile findById(final Long profileId) {
        return profileRepository
                .findById(profileId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByMemberId(final Long memberId) {
        return profileRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public Profile findByEmailId(final String emailId) {
        return profileRepository
                .findByEmailId(emailId)
                .orElseThrow(() -> ProfileNotFoundException.EXCEPTION);
    }

    public CursorResponse<Profile> findAllExcludingIdsWithCursor(
            final List<Long> excludeProfileIds, final CursorRequest cursorRequest) {
        long startTime = System.currentTimeMillis();

        log.info(
                "프로필 찾기 - 기본 조회 요청: excludeProfileIds={}, cursor={}, size={}",
                excludeProfileIds,
                cursorRequest != null ? cursorRequest.getCursor() : null,
                cursorRequest != null ? cursorRequest.getSize() : 0);

        CursorResponse<Profile> result =
                profileRepository.findAllExcludingIdsWithCursor(
                        excludeProfileIds != null ? excludeProfileIds : Collections.emptyList(),
                        cursorRequest);

        long endTime = System.currentTimeMillis();
        log.info("프로필 찾기 - 기본 조회 완료: {} ms 소요", (endTime - startTime));

        return result;
    }

    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {
        long startTime = System.currentTimeMillis();

        log.info(
                "프로필 찾기 - 필터링 조회 요청: subPosition={}, cityName={}, profileStateName={}, cursor={}, size={}",
                subPosition,
                cityName,
                profileStateName,
                cursorRequest != null ? cursorRequest.getCursor() : null,
                cursorRequest != null ? cursorRequest.getSize() : 0);

        CursorResponse<Profile> result =
                profileRepository.findAllByFilteringWithCursor(
                        subPosition, cityName, profileStateName, cursorRequest);

        long endTime = System.currentTimeMillis();
        log.info("프로필 찾기 - 필터링 조회 완료: {} ms 소요", (endTime - startTime));

        return result;
    }

    @Cacheable(value = "topCompletionProfiles", key = "'topCompletionProfiles'")
    public Page<Profile> findTopCompletionProfiles(final Pageable pageable) {
        return profileRepository.findTopCompletionProfiles(pageable);
    }

    @Cacheable(value = "homeTopProfiles", key = "'homeTopProfiles'")
    public List<Profile> findHomeTopProfiles(final int limit) {
        return profileRepository.findHomeTopProfiles(limit);
    }

    public List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition) {
        return profileRepository.findByMarketingConsentAndMajorPosition(majorPosition);
    }
}
