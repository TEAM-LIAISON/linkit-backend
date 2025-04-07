package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import liaison.linkit.global.util.CursorUtils;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
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
            List.of(42L, 58L, 57L, 55L, 73L, 63L);

    private final ProfileRepository profileRepository;
    private final ProfileScrapRepository profileScrapRepository;
    private final ProfileQueryAdapter profileQueryAdapter;
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
            if (!cursorRequest.hasNext()) {
                List<ProfileInformMenu> results =
                        getFirstPageDefaultProfiles(cursorRequest.size(), optionalMemberId);
                String nextCursor =
                        results.isEmpty() ? null : results.get(results.size() - 1).getEmailId();
                return CursorResponse.of(results, nextCursor);
            }

            CursorResponse<Profile> profiles =
                    profileQueryAdapter.findAllExcludingIdsWithCursor(
                            DEFAULT_EXCLUDE_PROFILE_IDS, cursorRequest);

            return CursorUtils.mapCursorResponse(
                    profiles,
                    p -> profileInformMenuAssembler.assembleProfileInformMenu(p, optionalMemberId));
        }

        CursorResponse<Profile> profiles =
                profileQueryAdapter.findAllByFilteringWithCursor(
                        condition.subPosition(),
                        condition.cityName(),
                        condition.profileStateName(),
                        cursorRequest);

        return CursorUtils.mapCursorResponse(
                profiles,
                p -> profileInformMenuAssembler.assembleProfileInformMenu(p, optionalMemberId));
    }

    private List<ProfileInformMenu> getFirstPageDefaultProfiles(
            int size, Optional<Long> optionalMemberId) {
        List<FlatProfileDTO> raw = profileRepository.findFlatProfilesWithoutCursor(size);
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

    private List<ProfileInformMenu> getTopCompletionProfiles(
            int size, Optional<Long> optionalMemberId) {
        List<FlatProfileDTO> raw = profileRepository.findTopCompletionProfiles(size);
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
