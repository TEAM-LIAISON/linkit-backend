package liaison.linkit.global.util;

import com.querydsl.core.types.OrderSpecifier;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import liaison.linkit.team.domain.QTeam;
import liaison.linkit.team.domain.QTeamCurrentState;
import liaison.linkit.team.domain.QTeamRegion;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
import liaison.linkit.team.domain.announcement.QAnnouncementSkill;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import liaison.linkit.team.domain.scale.QTeamScale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;


@Slf4j
public class QueryDslUtil {

    public static OrderSpecifier<?>[] getOrderAnnouncementSpecifier(
            Sort sort,
            QTeamMemberAnnouncement qTeamMemberAnnouncement,
            QAnnouncementPosition qAnnouncementPosition,
            QAnnouncementSkill qAnnouncementSkill,
            QTeamRegion qTeamRegion,
            QTeamScale qTeamScale
    ) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{};
        }

        // OrderSpecifier를 저장할 리스트
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            OrderSpecifier<?> orderSpecifier = null;

            switch (order.getProperty()) {
                case "id":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamMemberAnnouncement.id
                    );
                    break;
                case "majorPosition":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qAnnouncementPosition.position.majorPosition
                    );
                    break;
                case "skillName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qAnnouncementSkill.skill.skillName
                    );
                    break;
                case "cityName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamRegion.region.cityName
                    );
                    break;
                case "scaleName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamScale.scale.scaleName
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sort property: " + order.getProperty());
            }

            if (orderSpecifier != null) {
                orders.add(orderSpecifier);
            }
        }

        return orders.toArray(new OrderSpecifier<?>[0]);
    }

    public static OrderSpecifier<?>[] getOrderProfileSpecifier(
            Sort sort,
            QProfile qProfile,
            QProfilePosition qProfilePosition,
            QProfileRegion qProfileRegion,
            QProfileCurrentState qProfileCurrentState,
            QProfileSkill qProfileSkill
    ) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{};
        }

        // OrderSpecifier를 저장할 리스트
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            OrderSpecifier<?> orderSpecifier = null;

            switch (order.getProperty()) {
                case "id":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfile.id
                    );
                    break;
                case "memberName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfile.member.memberBasicInform.memberName
                    );
                    break;
                case "majorPosition":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfilePosition.position.majorPosition
                    );
                    break;
                case "skillName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfileSkill.skill.skillName
                    );
                    break;
                case "cityName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfileRegion.region.cityName
                    );
                    break;
                case "profileStateName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qProfileCurrentState.profileState.profileStateName
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sort property: " + order.getProperty());
            }

            if (orderSpecifier != null) {
                orders.add(orderSpecifier);
            }
        }

        return orders.toArray(new OrderSpecifier<?>[0]);
    }

    public static OrderSpecifier<?>[] getOrderTeamSpecifier(
            Sort sort,
            QTeam qTeam,
            QTeamScale qTeamScale,
            QTeamRegion qTeamRegion,
            QTeamCurrentState qTeamCurrentState
    ) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{};
        }

        // OrderSpecifier를 저장할 리스트
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            OrderSpecifier<?> orderSpecifier = null;

            switch (order.getProperty()) {
                case "id":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeam.id
                    );
                    break;
                case "scaleName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamScale.scale.scaleName
                    );
                    break;
                case "cityName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamRegion.region.cityName
                    );
                    break;
                case "teamStateName":
                    orderSpecifier = new OrderSpecifier<>(
                            order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                            qTeamCurrentState.teamState.teamStateName
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sort property: " + order.getProperty());
            }

            if (orderSpecifier != null) {
                orders.add(orderSpecifier);
            }
        }

        return orders.toArray(new OrderSpecifier<?>[0]);
    }
}
