package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
     * Optional로 로그인한 회원의 ID가 전달되면 로그인 상태로, 그렇지 않으면 로그아웃 상태로 프로필 목록을 검색합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional)
     * @param majorPosition    포지션 대분류 필터
     * @param skillName        스킬 필터
     * @param cityName         시/도 필터
     * @param profileStateName 프로필 상태 필터
     * @param pageable         페이징 정보
     * @return 검색된 프로필 정보를 ProfileInformMenu DTO로 매핑한 Page
     */
    public Page<ProfileInformMenu> searchProfiles(
        final Optional<Long> optionalMemberId,
        List<String> majorPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> profileStateName,
        Pageable pageable
    ) {
        Page<Profile> profiles = profileQueryAdapter.findAll(majorPosition, skillName, cityName, profileStateName, pageable);

        return profiles.map(profile ->
            profileInformMenuAssembler.assembleProfileInformMenu(profile, optionalMemberId)
        );
    }
}

