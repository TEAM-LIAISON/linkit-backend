package liaison.linkit.team.service;

import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.activity.ActivityMethodTag;
import liaison.linkit.team.domain.activity.ActivityRegion;
import liaison.linkit.team.domain.repository.Activity.ActivityMethodRepository;
import liaison.linkit.team.domain.repository.Activity.ActivityMethodTagRepository;
import liaison.linkit.team.domain.repository.Activity.ActivityRegionRepository;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.request.activity.BothActivityMethodCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityMethodService {

    final TeamProfileRepository teamProfileRepository;
    final ActivityMethodRepository activityMethodRepository;

    final ActivityRegionRepository activityRegionRepository;
    final ActivityMethodTagRepository activityMethodTagRepository;


    public void save(
            final Long memberId,
            final BothActivityMethodCreateRequest bothActivityMethodCreateRequest
    ) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

        // 1. ActivityRegion 엔티티에서 매칭되는 항목 찾기
        final ActivityRegion activityRegion = activityRegionRepository
                .findActivityRegionByRegionName(bothActivityMethodCreateRequest.getRegionName());
        // 2. ActivityMethodTag 엔티티에서 매칭되는 항목 찾기
        final ActivityMethodTag activityMethodTag = activityMethodTagRepository
                .findActivityRegionByActivityTagName(bothActivityMethodCreateRequest.getActivityTagName());

//        // 활동 방식에 저장 필요
//        final ActivityMethod activityMethod = new ActivityMethod(
//
//        )
    }
}
