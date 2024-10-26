package liaison.linkit.profile.service;

import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.profile.domain.repository.antecedents.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.skill.SkillRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrowsePrivateProfileService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;
    private final AntecedentsRepository antecedentsRepository;
    private final EducationRepository educationRepository;
}
