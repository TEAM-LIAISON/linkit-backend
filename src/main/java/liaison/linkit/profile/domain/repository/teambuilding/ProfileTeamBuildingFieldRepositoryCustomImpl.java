package liaison.linkit.profile.domain.repository.teambuilding;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.QProfileTeamBuildingField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ProfileTeamBuildingFieldRepositoryCustomImpl implements ProfileTeamBuildingFieldRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileTeamBuildingField profileTeamBuildingField = QProfileTeamBuildingField.profileTeamBuildingField;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileTeamBuildingField)
                .where(profileTeamBuildingField.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ProfileTeamBuildingField> findAllByProfileId(Long profileId) {
        QProfileTeamBuildingField profileTeamBuildingField = QProfileTeamBuildingField.profileTeamBuildingField;

        return jpaQueryFactory
                .selectFrom(profileTeamBuildingField)
                .where(profileTeamBuildingField.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QProfileTeamBuildingField profileTeamBuildingField = QProfileTeamBuildingField.profileTeamBuildingField;

        jpaQueryFactory
                .delete(profileTeamBuildingField)
                .where(profileTeamBuildingField.profile.id.eq(profileId))
                .execute();
    }
}
