package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.QMemberBasicInform;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.QProfile;
import liaison.linkit.profile.domain.QProfileCurrentState;
import liaison.linkit.profile.domain.QProfilePosition;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileCustomRepositoryImpl implements ProfileCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Profile result = jpaQueryFactory
                .selectFrom(profile)
                .where(profile.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }


    // 수정 및 보완 필요
    @Override
    public MiniProfileResponseDTO.MiniProfileDetail findMiniProfileDetail(final Long memberId) {
        QProfile qProfile = QProfile.profile;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform; // 멤버 기본 정보에 대한 Querydsl 메타 모델
        QRegion qRegion = QRegion.region;

        List<MiniProfileResponseDTO.ProfilePositionItem> profilePositionItems =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        MiniProfileResponseDTO.ProfilePositionItem.class,
                                        qProfilePosition.position.majorPosition,
                                        qProfilePosition.position.subPosition
                                ))
                        .from(qProfilePosition)
                        .leftJoin(qPosition)
                        .on(qProfilePosition.position.id.eq(qPosition.id))
                        .where(qProfilePosition.profile.member.id.eq(memberId))
                        .fetch();
        
        List<MiniProfileResponseDTO.ProfileCurrentStateItem> profileCurrentStateItems =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        MiniProfileResponseDTO.ProfileCurrentStateItem.class,
                                        qProfileCurrentState.profileState.profileStateName
                                ))
                        .from(qProfileCurrentState)
                        .leftJoin(qProfileCurrentState)
                        .on(qProfileCurrentState.profileState.id.eq(qProfileCurrentState.id))
                        .where(qProfileCurrentState.profile.member.id.eq(memberId))
                        .fetch();

        return null;
    }


    @Override
    public boolean existsByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profile)
                .where(profile.member.id.eq(memberId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        jpaQueryFactory
                .update(profile)
                .set(profile.status, StatusType.DELETED)
                .where(profile.member.id.eq(memberId))
                .execute();
    }
}
