package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectSkill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectSkillCustomRepositoryImpl implements ProjectSkillCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProjectSkill> getProjectSkills(final Long profilePortfolioId) {
        QProjectSkill qProjectSkill = QProjectSkill.projectSkill;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProjectSkill)
                .join(qProjectSkill.portfolio, qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }

//    @Override
//    public void deleteAll(final List<ProjectSkill> projectSkills) {
//        if (projectSkills == null || projectSkills.isEmpty()) {
//            return; // 삭제할 대상이 없으므로 메서드 종료
//        }
//
//        // ProjectSkill의 ID 리스트 추출
//        List<Long> ids = projectSkills.stream()
//                .map(ProjectSkill::getId)
//                .filter(Objects::nonNull) // null ID 필터링
//                .collect(Collectors.toList());
//
//        if (ids.isEmpty()) {
//            return; // 유효한 ID가 없으므로 삭제할 대상이 없음
//        }
//
//        QProjectSkill qProjectSkill = QProjectSkill.projectSkill;
//
//        // QueryDSL을 사용한 Bulk Delete
//        jpaQueryFactory
//                .delete(qProjectSkill)
//                .where(qProjectSkill.id.in(ids))
//                .execute();
//    }
}
