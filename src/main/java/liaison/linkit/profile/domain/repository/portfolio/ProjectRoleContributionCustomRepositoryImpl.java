package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectRoleContribution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectRoleContributionCustomRepositoryImpl implements ProjectRoleContributionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Long, List<String>> getProjectRolesByProfileId(final Long profileId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;
        QProjectRoleContribution qProjectRoleContribution = QProjectRoleContribution.projectRoleContribution;

        log.info("쿼리 조회 시작");

        List<Tuple> results = jpaQueryFactory
                .select(qProfilePortfolio.id, qProjectRoleContribution.projectRole)
                .from(qProfilePortfolio)
                .leftJoin(qProfilePortfolio.projectRoleContributions, qProjectRoleContribution)
                .where(qProfilePortfolio.profile.id.eq(profileId))
                .fetch();

        // Java Stream을 사용한 그룹핑
        Map<Long, List<String>> resultMap = results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(qProfilePortfolio.id),
                        Collectors.mapping(
                                tuple -> tuple.get(qProjectRoleContribution.projectRole),
                                Collectors.filtering(
                                        role -> role != null,
                                        Collectors.toList()
                                )
                        )
                ));

        // projectRole이 없는 경우 빈 리스트를 유지하도록 처리
        for (Tuple tuple : results) {
            Long portfolioId = tuple.get(qProfilePortfolio.id);
            resultMap.putIfAbsent(portfolioId, new ArrayList<>());
        }

        return resultMap;
    }


    @Override
    public List<ProjectRoleContribution> getProjectRoleContributions(final Long profilePortfolioId) {
        QProjectRoleContribution qProjectRoleContribution = QProjectRoleContribution.projectRoleContribution;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProjectRoleContribution)
                .join(qProjectRoleContribution.portfolio, qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }

}
