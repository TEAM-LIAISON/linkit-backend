package liaison.linkit.profile.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.MiniProfileMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.implement.MiniProfileQueryAdapter;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.currentState.ProfileCurrentStateQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO.UpdateMiniProfileRequest;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfilePositionItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.UpdateMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MiniProfileService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;
    private final MiniProfileQueryAdapter miniProfileQueryAdapter;

    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileCurrentStateQueryAdapter profileCurrentStateQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;

    private final MiniProfileMapper miniProfileMapper;

    private final RegionMapper regionMapper;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;


    // 미니 프로필을 조회한다
    @Transactional(readOnly = true)
    public MiniProfileDetailResponse getMiniProfileDetail(final Long memberId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final String memberName = memberQueryAdapter.findById(memberId).getMemberBasicInform().getMemberName();

        ProfilePositionItem profilePositionItem = new ProfilePositionItem();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
            profilePositionItem = miniProfileMapper.toProfilePositionItem(profilePosition);
        }

        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }

        List<ProfileCurrentStateItem> profileCurrentStateItems = new ArrayList<>();
        if (profileCurrentStateQueryAdapter.existsProfileCurrentStateByProfileId(profile.getId())) {
            List<ProfileCurrentState> profileCurrentStates = profileCurrentStateQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
            profileCurrentStateItems = miniProfileMapper.toProfileCurrentStateItems(profileCurrentStates);
        }

        return miniProfileMapper.toMiniProfileDetailResponse(profile, memberName, profilePositionItem, regionDetail, profileCurrentStateItems);
    }


    // 미니 프로필을 저장한다
    public UpdateMiniProfileResponse updateMiniProfile(
            final Long memberId,
            final Long profileId,
            final MultipartFile profileImage,
            final UpdateMiniProfileRequest updateMiniProfileRequest
    ) {
        String profileImagePath = null;

        // 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 프로필 사진을 업데이트한다
        if (imageValidator.validatingImageUpload(profileImage)) {
            profileImagePath = s3Uploader.uploadProfileImage(new ImageFile(profileImage));
        }

        // 포지션을 업데이트한다

        // 활동 지역을 업데이트한다
        final Region region = regionQueryAdapter.findByCityNameAndDivisionName(updateMiniProfileRequest.getCityName(), updateMiniProfileRequest.getDivisionName());

        // 현재 상태를 업데이트한다

        // 프로필 공개 여부를 업데이트한다
        profile.setIsProfilePublic(updateMiniProfileRequest.getIsProfilePublic());

        return miniProfileMapper.toUpdateProfile(profile);
    }
}
