package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.profile.business.ProfileEducationMapper;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileEducationService {

    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileEducationMapper profileEducationMapper;

    @Transactional(readOnly = true)
    public ProfileEducationItems getProfileEducationItems(final Long memberId) {
        log.info("memberId = {}의 내 학력 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileEducation> profileEducations = profileEducationQueryAdapter.getProfileEducations(memberId);
        log.info("profileEducations = {}가 성공적으로 조회되었습니다.", profileEducations);

        return profileEducationMapper.toProfileEducationItems(profileEducations);
    }
}
