package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.ProfileListResponseDTO;
import liaison.linkit.team.business.assembler.AnnouncementInformMenuAssembler;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileSearchService {

    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileInformMenuAssembler profileInformMenuAssembler;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;

    public ProfileListResponseDTO getFeaturedProfiles(final Optional<Long> optionalMemberId) {
        // 1. 상단 영역: 프로필 완성도가 높은 팀원 6명 (completionScore 내림차순)
        Pageable topPageable = PageRequest.of(0, 6);
        List<Profile> topProfiles =
                profileQueryAdapter.findTopCompletionProfiles(topPageable).getContent();

        List<ProfileInformMenu> topProfileDTOs =
                topProfiles.stream()
                        .map(
                                profile ->
                                        profileInformMenuAssembler.assembleProfileInformMenu(
                                                profile, optionalMemberId))
                        .toList();

        return ProfileListResponseDTO.of(topProfileDTOs);
    }

    /**
     * 팀원 찾기 화면의 검색 로직
     *
     * <p>- 쿼리 파라미터가 전혀 없으면 기본 검색으로 간주하여: 1. 상단: 프로필 완성도가 높은 팀원 6명(예: completionScore 기준 내림차순) 2.
     * 하단: 상단에 포함된 프로필을 제외한 나머지 팀원들을 최신순(createdAt 내림차순)으로 페이지네이션 - 필터 쿼리 파라미터가 있으면 기존의 필터링 로직대로
     * 검색합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional)
     * @param subPosition 포지션 소분류 필터
     * @param cityName 시/도 필터
     * @param profileStateName 프로필 상태 필터
     * @return ProfileInformMenu: 하단(profiles) DTO
     */
    public CursorResponse<ProfileInformMenu> searchProfilesWithCursor(
            final Optional<Long> optionalMemberId,
            List<String> subPosition,
            List<String> cityName,
            List<String> profileStateName,
            CursorRequest cursorRequest) {
        if (isDefaultSearch(subPosition, cityName, profileStateName)) {
            List<Long> excludeProfileIds = getExcludeProfileIds();

            CursorResponse<Profile> profiles =
                    profileQueryAdapter.findAllExcludingIdsWithCursor(
                            excludeProfileIds, cursorRequest);

            return convertProfilesToDTOs(profiles, optionalMemberId);
        } else {
            CursorResponse<Profile> profiles =
                    profileQueryAdapter.findAllByFilteringWithCursor(
                            subPosition, cityName, profileStateName, cursorRequest);

            return convertProfilesToDTOs(profiles, optionalMemberId);
        }
    }

    /** 기본 검색 여부를 판단합니다. */
    private boolean isDefaultSearch(
            List<String> subPosition, List<String> cityName, List<String> profileStateName) {
        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (profileStateName == null || profileStateName.isEmpty());
    }

    /** 제외할 팀 ID 목록 가져오기 (강제 지정) */
    public List<Long> getExcludeProfileIds() {
        // 박주혜 42L
        // 최민호 58L
        // 김태범 57L
        // 최윤수 55L
        // 박현진 73L
        // 김시원 63L
        return List.of(42L, 58L, 57L, 55L, 73L, 63L);
    }

    /** 팀 엔티티를 DTO로 변환하고 커서 응답으로 래핑 */
    private CursorResponse<ProfileResponseDTO.ProfileInformMenu> convertProfilesToDTOs(
            CursorResponse<Profile> profiles, Optional<Long> optionalMemberId) {
        List<ProfileResponseDTO.ProfileInformMenu> profileDTOs =
                profiles.getContent().stream()
                        .map(
                                profile ->
                                        profileInformMenuAssembler.assembleProfileInformMenu(
                                                profile, optionalMemberId))
                        .toList();

        return CursorResponse.of(profileDTOs, profiles.getNextCursor());
    }
}
