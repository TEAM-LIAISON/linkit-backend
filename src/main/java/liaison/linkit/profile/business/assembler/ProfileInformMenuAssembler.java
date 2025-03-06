package liaison.linkit.profile.business.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.business.mapper.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.mapper.ProfileMapper;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.state.ProfileCurrentStateQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileInformMenuAssembler {

    // Adapter
    private final RegionQueryAdapter regionQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileCurrentStateQueryAdapter profileCurrentStateQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    // Mapper
    private final RegionMapper regionMapper;
    private final ProfileMapper profileMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamMemberMapper teamMemberMapper;

    /**
     * 1. 지역 정보 조회 메서드
     *
     * @param profile 조회 대상 프로필
     * @return 조회된 RegionDetail, 없으면 기본 인스턴스 반환
     */
    public RegionDetail assembleRegionDetail(final Profile profile) {
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
            ProfileRegion profileRegion =
                    regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        log.info("지역 정보 조회 성공");
        return regionDetail;
    }

    /**
     * 2. 프로필 상태 정보 조회 메서드
     *
     * @param profile 조회 대상 프로필
     * @return 프로필 상태 목록을 ProfileCurrentStateItem 리스트로 변환한 결과
     */
    public List<ProfileCurrentStateItem> assembleProfileCurrentStateItems(final Profile profile) {
        List<ProfileCurrentState> currentStates =
                profileCurrentStateQueryAdapter.findProfileCurrentStatesByProfileId(
                        profile.getId());
        List<ProfileCurrentStateItem> currentStateItems =
                profileCurrentStateMapper.toProfileCurrentStateItems(currentStates);
        log.info("상태 정보 조회 성공");
        return currentStateItems;
    }

    /**
     * 3. 프로필 포지션 정보 조회 메서드
     *
     * @param profile 조회 대상 프로필
     * @return 조회된 ProfilePositionDetail, 없으면 기본 인스턴스 반환
     */
    public ProfilePositionDetail assembleProfilePositionDetail(final Profile profile) {
        ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            profilePositionDetail =
                    profilePositionMapper.toProfilePositionDetail(
                            profilePositionQueryAdapter.findProfilePositionByProfileId(
                                    profile.getId()));
        }
        log.info("대분류 포지션 정보 조회 성공");
        return profilePositionDetail;
    }

    /**
     * 4. 팀 정보 조회 메서드
     *
     * @param profile 조회 대상 프로필
     * @return 조회된 팀 정보를 ProfileTeamInform 리스트로 반환, 없으면 빈 리스트 반환
     */
    public List<ProfileTeamInform> assembleProfileTeamInforms(
            final Profile profile, final Optional<Long> loggedInMemberId) {
        boolean isMyProfile =
                loggedInMemberId.isPresent()
                        && profile.getMember().getId().equals(loggedInMemberId.get());

        List<ProfileTeamInform> targetProfileTeamInforms = new ArrayList<>();
        Long memberIdForTeam = profile.getMember().getId();
        if (teamMemberQueryAdapter.existsTeamByMemberId(memberIdForTeam) && isMyProfile) {
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsByMemberId(memberIdForTeam);
            targetProfileTeamInforms = teamMemberMapper.toProfileTeamInforms(teams);
        } else if (teamMemberQueryAdapter.existsTeamByMemberId(memberIdForTeam) && !isMyProfile) {
            List<Team> teams = teamMemberQueryAdapter.getAllPublicTeamsByMemberId(memberIdForTeam);
            targetProfileTeamInforms = teamMemberMapper.toProfileTeamInforms(teams);
        }
        return targetProfileTeamInforms;
    }

    /**
     * 5. 스크랩 여부 조회 메서드 (로그인 상태인 경우)
     *
     * @param profile 조회 대상 프로필
     * @param loggedInMemberId 로그인한 사용자의 memberId(Optional)
     * @return 스크랩 여부, 로그인 상태가 아니면 기본 false 반환
     */
    public boolean assembleIsProfileScrap(
            final Profile profile, final Optional<Long> loggedInMemberId) {
        boolean isProfileScrap = false;
        if (loggedInMemberId.isPresent()) {
            isProfileScrap =
                    profileScrapQueryAdapter.existsByMemberIdAndEmailId(
                            loggedInMemberId.get(), profile.getMember().getEmailId());
        }
        return isProfileScrap;
    }

    /**
     * 6. 스크랩 수 조회 메서드
     *
     * @param profile 조회 대상 프로필
     * @return 해당 프로필의 총 스크랩 수
     */
    public int assembleProfileScrapCount(final Profile profile) {
        return profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
                profile.getMember().getEmailId());
    }

    /**
     * 7. 최종 ProfileInformMenu DTO 조립 메서드
     *
     * @param profile 조회 대상 프로필
     * @param loggedInMemberId Optional 로그인한 사용자의 memberId. 값이 존재하면 로그인 상태, 없으면 로그아웃 상태로 처리
     * @return 최종 조립된 ProfileInformMenu DTO
     */
    public ProfileInformMenu assembleProfileInformMenu(
            final Profile profile, final Optional<Long> loggedInMemberId) {
        List<ProfileCurrentStateItem> currentStateItems = assembleProfileCurrentStateItems(profile);
        ProfilePositionDetail profilePositionDetail = assembleProfilePositionDetail(profile);
        RegionDetail regionDetail = assembleRegionDetail(profile);
        List<ProfileTeamInform> profileTeamInforms =
                assembleProfileTeamInforms(profile, loggedInMemberId);
        boolean isProfileScrap = assembleIsProfileScrap(profile, loggedInMemberId);
        int profileScrapCount = assembleProfileScrapCount(profile);

        return profileMapper.toProfileInformMenu(
                currentStateItems,
                isProfileScrap,
                profileScrapCount,
                profile,
                profilePositionDetail,
                regionDetail,
                profileTeamInforms);
    }
}
