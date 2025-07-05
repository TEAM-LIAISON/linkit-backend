package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.scrap.domain.repository.profileScrap.ProfileScrapRepository;
import liaison.linkit.search.business.model.ProfileSearchCondition;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.FlatProfileDTO;
import liaison.linkit.search.presentation.dto.profile.ProfileListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSearchService {

    private static final List<Long> DEFAULT_EXCLUDE_PROFILE_IDS =
            List.of(171L, 160L, 168L, 154L, 167L, 108L);

    private final ProfileRepository profileRepository;
    private final ProfileScrapRepository profileScrapRepository;
    private final ProfileInformMenuAssembler profileInformMenuAssembler;

    public ProfileListResponseDTO getFeaturedProfiles(final Optional<Long> optionalMemberId) {
        List<ProfileInformMenu> topProfileDTOs = getTopCompletionProfiles(6, optionalMemberId);
        return ProfileListResponseDTO.of(topProfileDTOs);
    }

    public CursorResponse<ProfileInformMenu> searchProfilesWithCursor(
            final Optional<Long> optionalMemberId,
            ProfileSearchCondition condition,
            CursorRequest cursorRequest) {

        if (condition.isDefault()) {
            // /api/v1/profile/search?size=20
            if (!cursorRequest.hasNext()) {
                List<ProfileInformMenu> results =
                        getFirstPageDefaultProfiles(cursorRequest.size(), optionalMemberId);
                String nextCursor =
                        results.isEmpty() ? null : results.get(results.size() - 1).getEmailId();
                return CursorResponse.of(results, nextCursor);
            }

            // /api/v1/profile/search?cursor={emailId}&size=20
            List<ProfileInformMenu> results =
                    getAllProfilesWithoutFilter(
                            cursorRequest.size(), optionalMemberId, cursorRequest);
            String nextCursor =
                    results.isEmpty() ? null : results.get(results.size() - 1).getEmailId();
            return CursorResponse.of(results, nextCursor);
        }

        // 필터가 포함된 경우
        List<ProfileInformMenu> results =
                getAllProfilesWithFilter(
                        cursorRequest.size(), optionalMemberId, condition, cursorRequest);
        String nextCursor = results.isEmpty() ? null : results.get(results.size() - 1).getEmailId();
        return CursorResponse.of(results, nextCursor);
    }

    private List<ProfileInformMenu> getFirstPageDefaultProfiles(
            int size, Optional<Long> optionalMemberId) {
        List<FlatProfileDTO> raw =
                profileRepository.findFlatProfilesWithoutCursor(DEFAULT_EXCLUDE_PROFILE_IDS, size);
        return getProfileInformMenus(size, optionalMemberId, raw);
    }

    private List<ProfileInformMenu> getTopCompletionProfiles(
            int size, Optional<Long> optionalMemberId) {
        List<FlatProfileDTO> raw = profileRepository.findTopCompletionProfiles(size);
        return getProfileInformMenus(size, optionalMemberId, raw);
    }

    private List<ProfileInformMenu> getAllProfilesWithoutFilter(
            int size, Optional<Long> optionalMemberId, CursorRequest cursorRequest) {
        List<FlatProfileDTO> raw =
                profileRepository.findAllProfilesWithoutFilter(
                        DEFAULT_EXCLUDE_PROFILE_IDS, cursorRequest);
        return getProfileInformMenus(size, optionalMemberId, raw);
    }

    private List<ProfileInformMenu> getAllProfilesWithFilter(
            int size,
            Optional<Long> optionalMemberId,
            ProfileSearchCondition condition,
            CursorRequest cursorRequest) {
        List<FlatProfileDTO> raw =
                profileRepository.findFilteredFlatProfilesWithCursor(
                        condition.subPosition(),
                        condition.cityName(),
                        condition.profileStateName(),
                        cursorRequest);
        return getProfileInformMenus(size, optionalMemberId, raw);
    }

    @NotNull
    private List<ProfileInformMenu> getProfileInformMenus(
            int size, Optional<Long> optionalMemberId, List<FlatProfileDTO> raw) {
        List<Long> profileIds = raw.stream().map(FlatProfileDTO::getProfileId).distinct().toList();

        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        profileScrapRepository.findScrappedProfileIdsByMember(
                                                memberId, profileIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts =
                profileScrapRepository.countScrapsGroupedByProfile(profileIds);

        Map<Long, List<ProfileTeamInform>> teamMap = Map.of();

        List<ProfileInformMenu> menus =
                profileInformMenuAssembler.assembleProfileInformMenus(
                        raw, scraps, scrapCounts, teamMap);

        return menus.size() > size ? menus.subList(0, size) : menus;
    }
}
