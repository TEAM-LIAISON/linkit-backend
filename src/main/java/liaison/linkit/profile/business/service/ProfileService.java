package liaison.linkit.profile.business.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.business.assembler.ProfileDetailAssembler;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.business.mapper.ProfileMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import liaison.linkit.visit.event.ProfileVisitedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    private final ProfileInformMenuAssembler profileInformMenuAssembler;
    private final ProfileMapper profileMapper;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileDetailAssembler profileDetailAssembler;

    private final ApplicationEventPublisher applicationEventPublisher;

    // 수정창에서 내 프로필 왼쪽 메뉴 조회
    public ProfileLeftMenu getProfileLeftMenu(final Long memberId) {
        final Profile targetProfile = profileQueryAdapter.findByMemberId(memberId);

        return profileMapper.toProfileLeftMenu(
                profileMapper.toProfileCompletionMenu(targetProfile),
                profileMapper.toProfileBooleanMenu(targetProfile));
    }

    // 로그인한 사용자가 프로필을 조회한다.
    public ProfileResponseDTO.ProfileDetail getLoggedInProfileDetail(
            final Optional<Long> optionalMemberId, final String emailId) {
        final Profile targetProfile = profileQueryAdapter.findByEmailId(emailId);
        final Member targetMember = targetProfile.getMember();

        boolean isMyProfile = Objects.equals(targetMember.getId(), optionalMemberId.orElse(null));

        final ProfileCompletionMenu profileCompletionMenu =
                profileMapper.toProfileCompletionMenu(targetProfile);
        final ProfileInformMenu profileInformMenu =
                profileInformMenuAssembler.assembleProfileInformMenu(
                        targetProfile, optionalMemberId);

        // 로그인한 사용자의 경우 방문자 저장 이벤트 실행

        if (!isMyProfile) {
            final Profile visitorProfile =
                    profileQueryAdapter.findByMemberId(optionalMemberId.get());

            applicationEventPublisher.publishEvent(
                    new ProfileVisitedEvent(
                            targetProfile.getId(),
                            visitorProfile.getId(),
                            optionalMemberId,
                            "profileVisit"));
        }

        return profileDetailAssembler.assembleProfileDetail(
                targetProfile, isMyProfile, profileCompletionMenu, profileInformMenu);
    }

    // 로그인하지 않은 사용자가 프로필을 조회한다.
    public ProfileResponseDTO.ProfileDetail getLoggedOutProfileDetail(final String emailId) {
        final Profile targetProfile = profileQueryAdapter.findByEmailId(emailId);

        final ProfileCompletionMenu profileCompletionMenu =
                profileMapper.toProfileCompletionMenu(targetProfile);
        final ProfileInformMenu profileInformMenu =
                profileInformMenuAssembler.assembleProfileInformMenu(
                        targetProfile, Optional.empty());

        return profileDetailAssembler.assembleProfileDetail(
                targetProfile, false, profileCompletionMenu, profileInformMenu);
    }

    // 홈화면에서 로그인 상태에서 팀원 정보를 조회한다.
    public ProfileResponseDTO.ProfileInformMenus getHomeProfileInformMenusInLoginState(
            final Long memberId) {
        List<Profile> profiles = profileQueryAdapter.findHomeTopProfiles(6);

        // Profiles -> ProfileInformMenus 변환
        List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus =
                profiles.stream()
                        .map(profile -> toHomeProfileInformMenuInLoginState(profile, memberId))
                        .toList();

        return profileMapper.toProfileInformMenus(profileInformMenus);
    }

    // 홈화면에서 로그아웃 상태에서 팀원 정보를 조회한다.
    public ProfileResponseDTO.ProfileInformMenus getHomeProfileInformMenusInLogoutState() {
        List<Profile> profiles = profileQueryAdapter.findHomeTopProfiles(6);

        // Profiles -> ProfileInformMenus 변환
        List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus =
                profiles.stream().map(this::toHomeProfileInformMenuInLogoutState).toList();

        return profileMapper.toProfileInformMenus(profileInformMenus);
    }

    // 프로필 요약 정보를 조회한다.
    public ProfileResponseDTO.ProfileSummaryInform getProfileSummaryInform(final String emailId) {
        final Profile targetProfile = profileQueryAdapter.findByEmailId(emailId);

        return profileInformMenuAssembler.assembleProfileSummaryInform(targetProfile);
    }

    private ProfileInformMenu toHomeProfileInformMenuInLoginState(
            final Profile targetProfile, final Long memberId) {
        return profileInformMenuAssembler.assembleProfileInformMenu(
                targetProfile, Optional.of(memberId));
    }

    private ProfileInformMenu toHomeProfileInformMenuInLogoutState(final Profile targetProfile) {
        return profileInformMenuAssembler.assembleProfileInformMenu(
                targetProfile, Optional.empty());
    }
}
