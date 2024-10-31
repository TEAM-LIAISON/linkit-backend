package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.profile.business.ProfileActivityMapper;
import liaison.linkit.profile.domain.ProfileActivity;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileActivityService {

    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfileActivityMapper profileActivityMapper;

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityItems getProfileActivityItems(final Long memberId) {
        log.info("memberId = {}의 내 이력 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileActivity> profileActivities = profileActivityQueryAdapter.getProfileActivities(memberId);
        log.info("profileActivities = {}가 성공적으로 조회되었습니다.", profileActivities);

        return profileActivityMapper.toProfileActivityItems(profileActivities);
    }

    @Transactional(readOnly = true)
    public ProfileActivityResponseDTO.ProfileActivityItem getProfileActivityItem(final Long memberId, final Long profileActivityId) {
        log.info("memberId = {}의 내 로그 DTO 조회 요청 발생했습니다.", memberId);

        final ProfileActivity profileActivity = profileActivityQueryAdapter.getProfileActivity(profileActivityId);
        log.info("profileLog = {}가 성공적으로 조회되었습니다.", profileActivity);

        return profileActivityMapper.toProfileActivityItem(profileActivity);
    }
}
