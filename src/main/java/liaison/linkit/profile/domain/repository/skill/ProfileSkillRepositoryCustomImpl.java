package liaison.linkit.profile.domain.repository.skill;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.domain.QProfile;
import liaison.linkit.profile.domain.QProfileSkill;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileSkillRepositoryCustomImpl implements ProfileSkillRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public ProfileSkillResponseDTO.ProfileSkillItems findProfileSkillItemsDTO(final Long memberId) {
        QProfile qProfile = QProfile.profile;
        Profile profile = jpaQueryFactory
                .selectFrom(qProfile)
                .where(qProfile.member.id.eq(memberId))
                .fetchOne();

        QProfileSkill qProfileSkill = QProfileSkill.profileSkill;

        if (profile == null) {
            return new ProfileSkillResponseDTO.ProfileSkillItems(Collections.emptyList());
        }

        List<ProfileSkillResponseDTO.ProfileSkillItem> profileSkillItems =
                jpaQueryFactory.select(
                                Projections.constructor(
                                        ProfileSkillResponseDTO.ProfileSkillItem.class,
                                        qProfileSkill.id,
                                        qProfileSkill.skillIconImagePath,
                                        qProfileSkill.skillName,
                                        qProfileSkill.skillLevel
                                ))
                        .from(qProfileSkill)
                        .where(qProfileSkill.profile.id.eq(profile.getId()))
                        .fetch();

        return new ProfileSkillResponseDTO.ProfileSkillItems(profileSkillItems);
    }

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ProfileSkill> getProfileSkills(final Long memberId) {
        QProfileSkill qProfileSkill = QProfileSkill.profileSkill;

        return jpaQueryFactory
                .selectFrom(qProfileSkill)
                .where(qProfileSkill.profile.member.id.eq(memberId))
                .fetch();
    }

//    @Override
//    public Optional<ProfileSkill> findByProfileId(Long profileId) {
//        QProfileSkill profileSkill = QProfileSkill.profileSkill;
//
//        ProfileSkill result = jpaQueryFactory
//                .selectFrom(profileSkill)
//                .where(profileSkill.profile.id.eq(profileId))
//                .fetchOne();
//
//        return Optional.ofNullable(result);
//    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        jpaQueryFactory
                .delete(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .execute();
    }

}
