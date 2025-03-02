package liaison.linkit.search.business.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.search.presentation.dto.profile.ProfileSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    /**
     * 팀원 찾기 화면의 검색 로직
     *
     * <p>- 쿼리 파라미터가 전혀 없으면 기본 검색으로 간주하여: 1. 상단: 프로필 완성도가 높은 팀원 6명(예: completionScore 기준 내림차순) 2.
     * 하단: 상단에 포함된 프로필을 제외한 나머지 팀원들을 최신순(createdAt 내림차순)으로 페이지네이션 - 필터 쿼리 파라미터가 있으면 기존의 필터링 로직대로
     * 검색합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional)
     * @param subPosition 포지션 소분류 필터
     * @param skillName 스킬 필터
     * @param cityName 시/도 필터
     * @param profileStateName 프로필 상태 필터
     * @param pageable 페이징 정보 (예: PageRequest.of(page, 80, Sort.by("createdAt").descending()))
     * @return ProfileSearchResponseDTO: 상단(topProfiles)과 하단(profiles)을 포함한 DTO
     */
    public ProfileSearchResponseDTO searchProfiles(
            final Optional<Long> optionalMemberId,
            List<String> subPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> profileStateName,
            Pageable pageable) {
        // 쿼리 파라미터가 모두 비어있는 경우: 기본 검색
        boolean isDefaultSearch =
                (subPosition == null || subPosition.isEmpty())
                        && (skillName == null || skillName.isEmpty())
                        && (cityName == null || cityName.isEmpty())
                        && (profileStateName == null || profileStateName.isEmpty());

        if (isDefaultSearch) {
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
                            .collect(Collectors.toList());

            // 2. 하단 영역: 상단에 포함된 프로필 제외 후 최신순(createdAt 내림차순) 페이지네이션
            List<Long> excludeIds =
                    topProfiles.stream().map(Profile::getId).collect(Collectors.toList());

            Page<Profile> remainingProfiles =
                    profileQueryAdapter.findAllExcludingIds(excludeIds, pageable);
            Page<ProfileInformMenu> remainingProfileDTOs =
                    remainingProfiles.map(
                            profile ->
                                    profileInformMenuAssembler.assembleProfileInformMenu(
                                            profile, optionalMemberId));

            return ProfileSearchResponseDTO.builder()
                    .topCompletionProfiles(topProfileDTOs)
                    .defaultProfiles(remainingProfileDTOs)
                    .build();
        } else {
            // 필터 쿼리 파라미터가 존재하는 경우: 기존 검색 로직 적용
            Page<Profile> profiles =
                    profileQueryAdapter.findAll(subPosition, cityName, profileStateName, pageable);
            Page<ProfileInformMenu> profileDTOs =
                    profiles.map(
                            profile ->
                                    profileInformMenuAssembler.assembleProfileInformMenu(
                                            profile, optionalMemberId));
            return ProfileSearchResponseDTO.builder()
                    .topCompletionProfiles(Collections.emptyList())
                    .defaultProfiles(profileDTOs)
                    .build();
        }
    }
}
