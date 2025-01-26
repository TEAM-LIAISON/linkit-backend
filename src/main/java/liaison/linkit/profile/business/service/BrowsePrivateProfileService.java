package liaison.linkit.profile.business.service;

import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.profile.domain.repository.education.ProfileEducationRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.skill.ProfileSkillRepository;
import liaison.linkit.profile.domain.repository.skill.SkillRepository;
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
    private final ProfileEducationRepository profileEducationRepository;
}
