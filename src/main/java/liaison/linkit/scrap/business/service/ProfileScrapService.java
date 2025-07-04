package liaison.linkit.scrap.business.service;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.mapper.ProfileMapper;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.state.ProfileCurrentStateQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenus;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.scrap.business.mapper.ProfileScrapMapper;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.exception.profileScrap.ProfileScrapBadRequestException;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapRequestDTO.UpdateProfileScrapRequest;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileScrapService {

    // Adapters
    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileCurrentStateQueryAdapter profileCurrentStateQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;
    private final RegionQueryAdapter regionQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    // Mappers
    private final ProfileScrapMapper profileScrapMapper;
    private final ProfileMapper profileMapper;
    private final RegionMapper regionMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamMemberMapper teamMemberMapper;

    // 회원이 프로필 스크랩 버튼을 눌렀을 떄의 메서드
    public ProfileScrapResponseDTO.UpdateProfileScrap updateProfileScrap(
            final Long memberId,
            final String emailId,
            final UpdateProfileScrapRequest updateProfileScrapRequest) {

        boolean shouldAddScrap = updateProfileScrapRequest.isChangeScrapValue();

        //        scrapValidator.validateSelfProfileScrap(memberId, emailId);     // 자기 자신의 프로필 선택에
        // 대한 예외 처리
        //        scrapValidator.validateMemberMaxProfileScrap(memberId);         // 최대 프로필 스크랩 개수에
        // 대한 예외 처리

        boolean scrapExists =
                profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId, emailId);

        if (scrapExists) {
            handleExistingScrap(memberId, emailId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, emailId, shouldAddScrap);
        }

        return profileScrapMapper.toUpdateProfileScrap(emailId, shouldAddScrap);
    }

    public ProfileInformMenus getProfileScraps(final Long memberId) {
        final List<ProfileScrap> profileScraps =
                profileScrapQueryAdapter.getAllProfileScrapByMemberId(memberId);

        final List<Profile> profiles =
                profileScraps.stream().map(ProfileScrap::getProfile).toList();

        final List<ProfileInformMenu> profileInformMenus = new ArrayList<>();

        for (Profile profile : profiles) {
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                final ProfileRegion profileRegion =
                        regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
            }

            final List<ProfileCurrentState> profileCurrentStates =
                    profileCurrentStateQueryAdapter.findProfileCurrentStatesByProfileId(
                            profile.getId());
            final List<ProfileCurrentStateItem> profileCurrentStateItems =
                    profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);

            final boolean isProfileScrap =
                    profileScrapQueryAdapter.existsByMemberIdAndEmailId(
                            memberId, profile.getMember().getEmailId());

            ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                final ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                profilePositionDetail =
                        profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            List<ProfileTeamInform> profileTeamInforms = new ArrayList<>();
            if (teamMemberQueryAdapter.existsTeamByMemberId(profile.getMember().getId())) {
                final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsByMemberId(memberId);
                profileTeamInforms = teamMemberMapper.toProfileTeamInforms(myTeams);
            }

            final int profileScrapCount =
                    profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
                            profile.getMember().getEmailId());

            final ProfileInformMenu profileInformMenu =
                    profileMapper.toProfileInformMenu(
                            profileCurrentStateItems,
                            isProfileScrap,
                            profileScrapCount,
                            profile,
                            profilePositionDetail,
                            regionDetail,
                            profileTeamInforms);
            profileInformMenus.add(profileInformMenu);
        }

        return profileMapper.toProfileInformMenus(profileInformMenus);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(
            final Long memberId, final String emailId, final boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            profileScrapCommandAdapter.deleteByMemberIdAndEmailId(memberId, emailId);
        } else {
            throw ProfileScrapBadRequestException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(
            final Long memberId, final String emailId, final boolean shouldAddScrap) {
        if (shouldAddScrap) {
            final Member member = memberQueryAdapter.findById(memberId);
            final Profile profile = profileQueryAdapter.findByEmailId(emailId);
            final ProfileScrap profileScrap = new ProfileScrap(null, member, profile);
            profileScrapCommandAdapter.addProfileScrap(profileScrap);
        } else {
            throw ProfileScrapBadRequestException.EXCEPTION;
        }
    }
}
