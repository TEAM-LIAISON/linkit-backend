package liaison.linkit.team.service;

import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.activity.ActivityMethod;
import liaison.linkit.team.domain.activity.ActivityMethodTag;
import liaison.linkit.team.domain.activity.ActivityRegion;
import liaison.linkit.team.domain.activity.Region;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.activity.ActivityMethodRepository;
import liaison.linkit.team.domain.repository.activity.ActivityMethodTagRepository;
import liaison.linkit.team.domain.repository.activity.ActivityRegionRepository;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    final TeamProfileRepository teamProfileRepository;

    final ActivityMethodRepository activityMethodRepository;
    final ActivityMethodTagRepository activityMethodTagRepository;

    final ActivityRegionRepository activityRegionRepository;
    final RegionRepository regionRepository;

    public void save(
            final Long memberId,
            final ActivityCreateRequest activityCreateRequest
    ) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

        // 활동 방식 태그 이름 구현 로직
        final List<ActivityMethodTag> activityMethodTags = activityMethodTagRepository
                .findActivityMethodTagByActivityTagNames(activityCreateRequest.getActivityTagNames());



        final List<ActivityMethod> activityMethods = activityMethodTags.stream()
                .map(activityMethodTag -> new ActivityMethod(null, teamProfile, activityMethodTag))
                .toList();

        // activityMethodRepository 저장


        // Request DTO -> 각 문자열을 ActivityMethodTag 테이블에서 찾아서 가져옴
    }

    // 활동 방식 저장
    public void saveActivityMethod(
            final Long memberId,
            final ActivityCreateRequest activityCreateRequest
    ) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

        // 활동 방식 태그 이름 구현 로직
        final List<ActivityMethodTag> activityMethodTags = activityMethodTagRepository
                .findActivityMethodTagByActivityTagNames(activityCreateRequest.getActivityTagNames());

        final List<ActivityMethod> activityMethods = activityMethodTags.stream()
                .map(activityMethodTag -> new ActivityMethod(null, teamProfile, activityMethodTag))
                .toList();

        activityMethodRepository.saveAll(activityMethods);
    }

    // 활동 지역 저장
    public void saveActivityRegion(
            final Long memberId,
            final ActivityCreateRequest activityCreateRequest
    ) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

        // 지역 정보 구현 로직
        final Region region = regionRepository
                .findRegionByCityNameAndDivisionName(activityCreateRequest.getCityName(), activityCreateRequest.getDivisionName());

        ActivityRegion activityRegion = new ActivityRegion(null, teamProfile, region);

        activityRegionRepository.save(activityRegion);
    }
}
