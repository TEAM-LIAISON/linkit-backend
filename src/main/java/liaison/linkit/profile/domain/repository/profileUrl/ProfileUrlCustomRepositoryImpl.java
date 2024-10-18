package liaison.linkit.profile.domain.repository.profileUrl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.QProfile;
import liaison.linkit.profile.domain.QProfileUrl;
import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileUrlCustomRepositoryImpl implements ProfileUrlCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProfileUrlResponseDTO.ProfileUrlItems findProfileUrlItems(final Long memberId) {
        QProfile qProfile = QProfile.profile;
        QProfileUrl qProfileUrl = QProfileUrl.profileUrl;

        Profile profile = jpaQueryFactory
                .selectFrom(qProfile)
                .where(qProfile.member.id.eq(memberId))
                .fetchOne();

        if (profile == null) {
            return new ProfileUrlResponseDTO.ProfileUrlItems(Collections.emptyList());
        }

        List<ProfileUrlResponseDTO.ProfileUrlItem> profileUrlItems =
                jpaQueryFactory.select(
                                Projections.constructor(
                                        ProfileUrlResponseDTO.ProfileUrlItem.class,
                                        qProfileUrl.id,
                                        qProfileUrl.urlIconImagePath,
                                        qProfileUrl.urlName,
                                        qProfileUrl.urlPath,
                                        qProfileUrl.profileUrlType
                                ))
                        .from(qProfileUrl)
                        .where(qProfileUrl.profile.id.eq(profile.getId()))
                        .fetch();

        return new ProfileUrlResponseDTO.ProfileUrlItems(profileUrlItems);
    }
}
