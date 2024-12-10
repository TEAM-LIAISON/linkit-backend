package liaison.linkit.global.util;

import com.querydsl.core.types.OrderSpecifier;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;


@Slf4j
public class QueryDslUtil {

    public static OrderSpecifier<?>[] getOrderSpecifier(Sort sort, QProfile qProfile, QProfilePosition qProfilePosition,
                                                        QProfileRegion qProfileRegion, QProfileCurrentState qProfileCurrentState, QProfileSkill qProfileSkill) {

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
}
