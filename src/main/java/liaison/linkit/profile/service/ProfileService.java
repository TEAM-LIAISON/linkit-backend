package liaison.linkit.profile.service;

import liaison.linkit.profile.domain.repository.antecedents.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.skill.SkillRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    // 프로필 리포지토리 -> 자기소개, 회원 정보 담당
    private final ProfileRepository profileRepository;
    // 미니 프로필 정보 담당

    // 보유 기술 정보 담당
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;
    // 희망 팀빌딩 분야 정보 담당
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    // 이력 정보 담당
    private final AntecedentsRepository antecedentsRepository;

    // 학력 정보 담당
    private final EducationRepository educationRepository;

}
