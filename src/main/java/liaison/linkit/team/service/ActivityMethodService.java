package liaison.linkit.team.service;

import liaison.linkit.team.domain.repository.Activity.ActivityMethodRepository;
import liaison.linkit.team.domain.repository.Activity.ActivityMethodTagRepository;
import liaison.linkit.team.domain.repository.Activity.RegionRepository;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityMethodService {

    final TeamProfileRepository teamProfileRepository;
    final ActivityMethodRepository activityMethodRepository;

    final RegionRepository regionRepository;
    final ActivityMethodTagRepository activityMethodTagRepository;

}
