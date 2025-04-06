package liaison.linkit.profile.implement.profile;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.exception.profile.ProfileNotFoundException;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.FlatProfileWithPositionDTO;
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
    private final ProfileInformMenuAssembler profileInformMenuAssembler;

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
        log.debug(
                "커서 기반 팀 조회 요청: excludeProfileIds={}, cursor={}, size={}",
                excludeProfileIds,
                cursorRequest.getCursor(),
                cursorRequest.getSize());
        return profileRepository.findAllExcludingIdsWithCursor(excludeProfileIds, cursorRequest);
    }

    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {
        log.debug(
                "필터링된 커서 기반 팀 조회 요청: subPosition={}, cityName={}, profileStateName={}, cursor={}, size={}",
                subPosition,
                cityName,
                profileStateName,
                cursorRequest.getCursor(),
                cursorRequest.getSize());
        return profileRepository.findAllByFilteringWithCursor(
                subPosition, cityName, profileStateName, cursorRequest);
    }

    @Cacheable(
            value = "topCompletionProfiles",
            key = "'topCompletionProfiles'" // 상수 키를 사용
            )
    public Page<Profile> findTopCompletionProfiles(final Pageable pageable) {
        return profileRepository.findTopCompletionProfiles(pageable);
    }

    @Cacheable(
            value = "homeTopProfiles",
            key = "'homeTopProfiles'" // 상수 키를 사용
            )
    public List<Profile> findHomeTopProfiles(final int limit) {
        return profileRepository.findHomeTopProfiles(limit);
    }

    public List<ProfileResponseDTO.ProfileInformMenu> getFirstPageDefaultProfiles(int size) {
        List<FlatProfileWithPositionDTO> raw =
                profileRepository.findFlatProfilesWithoutCursor(size);
        return profileInformMenuAssembler.assembleProfileInformMenus(raw);
    }

    public List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition) {
        return profileRepository.findByMarketingConsentAndMajorPosition(majorPosition);
    }
}
