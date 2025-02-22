package liaison.linkit.profile.business.service;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.domain.Position;
import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.MiniProfileMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.implement.position.PositionQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionCommandAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.region.ProfileRegionCommandAdapter;
import liaison.linkit.profile.implement.region.ProfileRegionQueryAdapter;
import liaison.linkit.profile.implement.state.ProfileCurrentStateCommandAdapter;
import liaison.linkit.profile.implement.state.ProfileCurrentStateQueryAdapter;
import liaison.linkit.profile.implement.state.ProfileStateQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO.UpdateMiniProfileRequest;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItems;
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

    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionCommandAdapter profilePositionCommandAdapter;
    private final PositionQueryAdapter positionQueryAdapter;

    private final ProfileCurrentStateQueryAdapter profileCurrentStateQueryAdapter;
    private final ProfileCurrentStateCommandAdapter profileCurrentStateCommandAdapter;
    private final ProfileStateQueryAdapter profileStateQueryAdapter;

    private final ProfileRegionQueryAdapter profileRegionQueryAdapter;
    private final ProfileRegionCommandAdapter profileRegionCommandAdapter;

    private final RegionQueryAdapter regionQueryAdapter;

    private final MiniProfileMapper miniProfileMapper;

    private final RegionMapper regionMapper;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;

    // 미니 프로필을 조회한다
    @Transactional(readOnly = true)
    public MiniProfileDetailResponse getMiniProfileDetail(final Long memberId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        final String memberName =
                memberQueryAdapter.findById(memberId).getMemberBasicInform().getMemberName();

        ProfilePositionItem profilePositionItem = new ProfilePositionItem();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            ProfilePosition profilePosition =
                    profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
            profilePositionItem = miniProfileMapper.toProfilePositionItem(profilePosition);
        }

        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
            final ProfileRegion profileRegion =
                    regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }

        ProfileCurrentStateItems profileCurrentStateItems = new ProfileCurrentStateItems();
        if (profileCurrentStateQueryAdapter.existsProfileCurrentStateByProfileId(profile.getId())) {
            List<ProfileCurrentState> profileCurrentStates =
                    profileCurrentStateQueryAdapter.findProfileCurrentStatesByProfileId(
                            profile.getId());
            profileCurrentStateItems =
                    miniProfileMapper.toProfileCurrentStateItems(profileCurrentStates);
        }

        return miniProfileMapper.toMiniProfileDetailResponse(
                profile, memberName, profilePositionItem, regionDetail, profileCurrentStateItems);
    }

    // 미니 프로필을 저장한다
    public UpdateMiniProfileResponse updateMiniProfile(
            final Long memberId,
            final MultipartFile profileImage,
            final UpdateMiniProfileRequest updateMiniProfileRequest) {
        String profileImagePath = null;

        // 프로필 조회
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 프로필 사진을 업데이트한다
        if (imageValidator.validatingImageUpload(profileImage)) {
            profileImagePath = s3Uploader.uploadProfileMainImage(new ImageFile(profileImage));
            profile.updateProfileImagePath(profileImagePath);
        }

        log.info("미니 프로필 사진 업데이트 완료");

        // 포지션을 업데이트한다
        final Position position =
                positionQueryAdapter.findByMajorPositionAndSubPosition(
                        updateMiniProfileRequest.getMajorPosition(),
                        updateMiniProfileRequest.getSubPosition());
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            profilePositionCommandAdapter.deleteAllByProfileId(profile.getId());
        }

        ProfilePosition profilePosition = new ProfilePosition(null, profile, position);
        profilePositionCommandAdapter.save(profilePosition);

        log.info("미니 프로필 포지션 업데이트 완료");

        // 활동 지역을 업데이트한다
        final Region region =
                regionQueryAdapter.findByCityNameAndDivisionName(
                        updateMiniProfileRequest.getCityName(),
                        updateMiniProfileRequest.getDivisionName());
        if (profileRegionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
            profileRegionCommandAdapter.deleteByProfileId(profile.getId());
        }

        ProfileRegion profileRegion = new ProfileRegion(null, profile, region);
        profileRegionCommandAdapter.save(profileRegion);

        log.info("미니 프로필 지역 업데이트 완료");

        // 현재 상태를 업데이트한다
        if (profileCurrentStateQueryAdapter.existsProfileCurrentStateByProfileId(profile.getId())) {
            profileCurrentStateCommandAdapter.deleteAllByProfileId(profile.getId());
        }

        // 새로운 프로필 현재 상태 저장
        List<String> profileStateNames = updateMiniProfileRequest.getProfileStateNames();
        List<ProfileCurrentState> profileCurrentStates = new ArrayList<>();

        for (String stateName : profileStateNames) {
            // ProfileState 엔티티 조회
            ProfileState profileState = profileStateQueryAdapter.findByStateName(stateName);

            // ProfileCurrentState 엔티티 생성
            ProfileCurrentState profileCurrentState =
                    new ProfileCurrentState(null, profile, profileState);
            profileCurrentStates.add(profileCurrentState);
        }

        // ProfileCurrentState 저장
        profileCurrentStateCommandAdapter.saveAll(profileCurrentStates);

        log.info("미니 프로필 현재 상태 업데이트 완료");

        // 프로필 공개 여부를 업데이트한다
        profile.setIsProfilePublic(updateMiniProfileRequest.getIsProfilePublic());

        if (!profile.isProfileMiniProfile()) {
            profile.setIsProfileMiniProfile(true);
        }

        return miniProfileMapper.toUpdateProfile(profile);
    }
}
