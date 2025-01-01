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
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenus;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.scrap.business.mapper.ProfileScrapMapper;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.exception.profileScrap.BadRequestProfileScrapException;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapRequestDTO.UpdateProfileScrapRequest;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import liaison.linkit.scrap.validation.ScrapValidator;
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

    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;

    private final ProfileScrapMapper profileScrapMapper;

    private final ScrapValidator scrapValidator;
    private final ProfileMapper profileMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberMapper teamMemberMapper;

    // 회원이 프로필 스크랩 버튼을 눌렀을 떄의 메서드
    public ProfileScrapResponseDTO.UpdateProfileScrap updateProfileScrap(
            final Long memberId,
            final String emailId,
            final UpdateProfileScrapRequest updateProfileScrapRequest
    ) {

        boolean shouldAddScrap = updateProfileScrapRequest.isChangeScrapValue();

        scrapValidator.validateSelfProfileScrap(memberId, emailId);     // 자기 자신의 프로필 선택에 대한 예외 처리
        scrapValidator.validateMemberMaxProfileScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId, emailId);

        if (scrapExists) {
            handleExistingScrap(memberId, emailId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, emailId, shouldAddScrap);
        }

        return profileScrapMapper.toUpdateProfileScrap(emailId, shouldAddScrap);
    }

    public ProfileInformMenus getProfileScraps(final Long memberId) {
        final List<ProfileScrap> profileScraps = profileScrapQueryAdapter.getAllProfileScrapByMemberId(memberId);

        final List<Profile> profiles = profileScraps.stream()
                .map(ProfileScrap::getProfile)
                .toList();

        final List<ProfileInformMenu> profileInformMenus = new ArrayList<>();

        for (Profile profile : profiles) {
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
            }
            log.info("지역 정보 조회 성공");

            final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
            final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);
            log.info("상태 정보 조회 성공");

            final boolean isProfileScrap = profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId, profile.getMember().getEmailId());

            ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            log.info("대분류 포지션 정보 조회 성공");

            List<ProfileTeamInform> profileTeamInforms = new ArrayList<>();
            if (teamMemberQueryAdapter.existsTeamByMemberId(memberId)) {
                final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamByMemberId(memberId);
                profileTeamInforms = teamMemberMapper.toProfileTeamInforms(myTeams);
                log.info("팀 정보 조회 성공, 팀 수: {}", profileTeamInforms.size());
            }
            final ProfileInformMenu profileInformMenu = profileMapper.toProfileInformMenu(profileCurrentStateItems, isProfileScrap, profile, profilePositionDetail, regionDetail, profileTeamInforms);
            profileInformMenus.add(profileInformMenu);
        }

        return profileMapper.toProfileInformMenus(profileInformMenus);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, String emailId, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            profileScrapCommandAdapter.deleteByMemberIdAndEmailId(memberId, emailId);
        } else {
            throw BadRequestProfileScrapException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, String emailId, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            Profile profile = profileQueryAdapter.findByEmailId(emailId);
            ProfileScrap profileScrap = new ProfileScrap(null, member, profile);
            profileScrapCommandAdapter.addProfileScrap(profileScrap);
        } else {
            throw BadRequestProfileScrapException.EXCEPTION;
        }
    }

}

