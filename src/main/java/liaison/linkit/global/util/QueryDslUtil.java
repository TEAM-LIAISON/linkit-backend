package liaison.linkit.global.util;

import com.querydsl.core.types.OrderSpecifier;
import java.util.Map;
import java.util.Objects;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.state.QTeamCurrentState;
import liaison.linkit.team.domain.team.QTeam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

@Slf4j
public class QueryDslUtil {

    // 공통 인터페이스 정의
    private interface OrderSpecifierFactory {
        OrderSpecifier<?> create(com.querydsl.core.types.Order order);
    }

    // 정렬 방향 결정 메서드
    private static com.querydsl.core.types.Order getOrder(boolean isAscending) {
        return isAscending ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
    }

    // 공통 OrderSpecifier 생성 로직
    private static OrderSpecifier<?>[] createOrderSpecifiers(
            Sort sort,
            Map<String, OrderSpecifierFactory> propertyFactories) {

        if (sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{};
        }

        return sort.stream()
                .map(order -> {
                    OrderSpecifierFactory factory = propertyFactories.get(order.getProperty());
                    if (factory == null) {
                        throw new IllegalArgumentException(
                                "Unknown sort property: " + order.getProperty());
                    }
                    return factory.create(getOrder(order.isAscending()));
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier<?>[]::new);
    }

    // Announcement 정렬 명세 생성
    public static OrderSpecifier<?>[] getOrderAnnouncementSpecifier(
            Sort sort,
            QTeamMemberAnnouncement qTeamMemberAnnouncement,
            QAnnouncementPosition qAnnouncementPosition,
            QTeamRegion qTeamRegion,
            QTeamScale qTeamScale) {

        Map<String, OrderSpecifierFactory> factories = Map.of(
                "id", order -> new OrderSpecifier<>(order, qTeamMemberAnnouncement.id),
                "subPosition", order -> new OrderSpecifier<>(order, qAnnouncementPosition.position.subPosition),
                "cityName", order -> new OrderSpecifier<>(order, qTeamRegion.region.cityName),
                "scaleName", order -> new OrderSpecifier<>(order, qTeamScale.scale.scaleName)
        );

        return createOrderSpecifiers(sort, factories);
    }

    // Profile 정렬 명세 생성
    public static OrderSpecifier<?>[] getOrderProfileSpecifier(
            Sort sort,
            QProfile qProfile,
            QProfilePosition qProfilePosition,
            QProfileRegion qProfileRegion,
            QProfileCurrentState qProfileCurrentState) {

        Map<String, OrderSpecifierFactory> factories = Map.of(
                "id", order -> new OrderSpecifier<>(order, qProfile.id),
                "memberName", order -> new OrderSpecifier<>(order, qProfile.member.memberBasicInform.memberName),
                "subPosition", order -> new OrderSpecifier<>(order, qProfilePosition.position.subPosition),
                "cityName", order -> new OrderSpecifier<>(order, qProfileRegion.region.cityName),
                "profileStateName", order -> new OrderSpecifier<>(order, qProfileCurrentState.profileState.profileStateName)
        );

        return createOrderSpecifiers(sort, factories);
    }

    // Team 정렬 명세 생성
    public static OrderSpecifier<?>[] getOrderTeamSpecifier(
            Sort sort,
            QTeam qTeam,
            QTeamScale qTeamScale,
            QTeamRegion qTeamRegion,
            QTeamCurrentState qTeamCurrentState) {

        Map<String, OrderSpecifierFactory> factories = Map.of(
                "id", order -> new OrderSpecifier<>(order, qTeam.id),
                "scaleName", order -> new OrderSpecifier<>(order, qTeamScale.scale.scaleName),
                "cityName", order -> new OrderSpecifier<>(order, qTeamRegion.region.cityName),
                "teamStateName", order -> new OrderSpecifier<>(order, qTeamCurrentState.teamState.teamStateName)
        );

        return createOrderSpecifiers(sort, factories);
    }
}
