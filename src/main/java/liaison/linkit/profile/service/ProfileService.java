package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.business.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.ProfileMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileMapper profileMapper;

    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;

    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;

    private final ProfileCurrentStateMapper profileCurrentStateMapper;

    public ProfileLeftMenu getProfileLeftMenu(final Long memberId) {
        log.info("memberId = {}의 프로필 왼쪽 메뉴 DTO 조회 요청 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        log.info("profile = {}가 성공적으로 조회되었습니다.", profile);

        RegionDetail regionDetail = new RegionDetail();

        if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }

        final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
        final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);

        final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());

        final ProfileCompletionMenu profileCompletionMenu = profileMapper.toProfileCompletionMenu(profile);
        final ProfileInformMenu profileInformMenu = profileMapper.toProfileInformMenu(profileCurrentStateItems, profile, profilePosition, regionDetail);
        final ProfileBooleanMenu profileBooleanMenu = profileMapper.toProfileBooleanMenu(profile);

        return profileMapper.toProfileLeftMenu(profileCompletionMenu, profileInformMenu, profileBooleanMenu);
    }
}
