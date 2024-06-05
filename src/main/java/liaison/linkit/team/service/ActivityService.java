//package liaison.linkit.team.service;
//
//import liaison.linkit.team.domain.TeamProfile;
//import liaison.linkit.team.domain.activity.ActivityMethod;
//import liaison.linkit.team.domain.activity.ActivityMethodTag;
//import liaison.linkit.team.domain.activity.ActivityRegion;
//import liaison.linkit.team.domain.activity.Region;
//import liaison.linkit.team.domain.repository.TeamProfileRepository;
//import liaison.linkit.team.domain.repository.activity.ActivityMethodRepository;
//import liaison.linkit.team.domain.repository.activity.ActivityMethodTagRepository;
//import liaison.linkit.team.domain.repository.activity.ActivityRegionRepository;
//import liaison.linkit.team.domain.repository.activity.RegionRepository;
//import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
//import liaison.linkit.team.dto.response.activity.ActivityMethodResponse;
//import liaison.linkit.team.dto.response.activity.ActivityRegionResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ActivityService {
//
//    final TeamProfileRepository teamProfileRepository;
//
//    final ActivityMethodRepository activityMethodRepository;
//    final ActivityMethodTagRepository activityMethodTagRepository;
//
//    final ActivityRegionRepository activityRegionRepository;
//    final RegionRepository regionRepository;
//
//
//
//    // 활동 방식 저장
//    public void saveActivityMethod(
//            final Long memberId,
//            final ActivityCreateRequest activityCreateRequest
//    ) {
//        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
//        if (activityMethodRepository.existsByTeamProfileId(teamProfile.getId())) {
//            activityMethodRepository.deleteAllByTeamProfileId(teamProfile.getId());
//        }
//
//        // 활동 방식 태그 이름 구현 로직
//        final List<ActivityMethodTag> activityMethodTags = activityMethodTagRepository
//                .findActivityMethodTagByActivityTagNames(activityCreateRequest.getActivityTagNames());
//
//        final List<ActivityMethod> activityMethods = activityMethodTags.stream()
//                .map(activityMethodTag -> new ActivityMethod(null, teamProfile, activityMethodTag))
//                .toList();
//
//        activityMethodRepository.saveAll(activityMethods);
//
//        teamProfile.updateIsActivityMethod(true);
//    }
//
//    // 활동 지역 저장
//    public void saveActivityRegion(
//            final Long memberId,
//            final ActivityCreateRequest activityCreateRequest
//    ) {
//        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
//
//        if (activityRegionRepository.existsByTeamProfileId(teamProfile.getId())) {
//            activityRegionRepository.deleteAllByTeamProfileId(teamProfile.getId());
//        }
//
//        // 지역 정보 구현 로직
//        final Region region = regionRepository
//                .findRegionByCityNameAndDivisionName(activityCreateRequest.getCityName(), activityCreateRequest.getDivisionName());
//
//        ActivityRegion activityRegion = new ActivityRegion(null, teamProfile, region);
//
//        activityRegionRepository.save(activityRegion);
//
//        teamProfile.updateIsActivityRegion(true);
//    }
//
//    // 활동 방식 전체 조회
//    @Transactional(readOnly = true)
//    public ActivityMethodResponse getAllActivityMethods(final Long memberId) {
//
//        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
//
//        List<ActivityMethod> activityMethods = activityMethodRepository.findAllByTeamProfileId(teamProfileId);
//
//        List<String> activityTagNames = activityMethods.stream()
//                .map(activityMethod -> activityMethodTagRepository.findById(activityMethod.getActivityMethodTag().getId()))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .map(ActivityMethodTag::getActivityTagName)
//                .toList();
//
//        return ActivityMethodResponse.of(activityTagNames);
//    }
//
//    @Transactional(readOnly = true)
//    public ActivityRegionResponse getActivityRegion(final Long memberId) {
//        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
//        ActivityRegion activityRegion = activityRegionRepository.findByTeamProfileId(teamProfileId);
//
//        return new ActivityRegionResponse(activityRegion.getRegion().getCityName(), activityRegion.getRegion().getDivisionName());
//    }
//}
