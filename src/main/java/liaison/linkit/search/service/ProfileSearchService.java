package liaison.linkit.search.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.business.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.ProfileMapper;
import liaison.linkit.profile.business.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.team.business.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
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
    private final RegionQueryAdapter regionQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    private final ProfileMapper profileMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final RegionMapper regionMapper;


    public Page<ProfileInformMenu> searchProfiles(
            List<String> majorPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> profileStateName,
            Pageable pageable
    ) {
        log.info("검색 조건 - MajorPosition: {}, SkillName: {}, CityName: {}, ProfileStateName: {}",
                majorPosition, skillName, cityName, profileStateName);
        Page<Profile> profiles = profileQueryAdapter.findAll(majorPosition, skillName, cityName, profileStateName, pageable);
        log.info("검색 결과 - 총 팀원 수: {}", profiles.getTotalElements());

        return profiles.map(this::toSearchProfileInformMenu);
    }

    private ProfileInformMenu toSearchProfileInformMenu(
            final Profile profile
    ) {
        RegionDetail regionDetail = new RegionDetail();

        if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        log.info("지역 정보 조회 성공");

        final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
        final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);
        log.info("상태 정보 조회 성공");

        ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();

        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
            profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        log.info("대분류 포지션 정보 조회 성공");

        List<ProfileTeamInform> profileTeamInforms = new ArrayList<>();
        if (teamMemberQueryAdapter.existsTeamByMemberId(profile.getMember().getId())) {
            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamByMemberId(profile.getMember().getId());
            profileTeamInforms = teamMemberMapper.toProfileTeamInforms(myTeams);
            log.info("팀 정보 조회 성공, 팀 수: {}", profileTeamInforms.size());
        }

        return profileMapper.toProfileInformMenu(profileCurrentStateItems, profile, profilePositionDetail, regionDetail, profileTeamInforms);
    }
}
