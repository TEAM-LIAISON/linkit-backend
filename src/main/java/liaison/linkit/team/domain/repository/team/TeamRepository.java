package liaison.linkit.team.domain.repository.team;

import java.util.List;

import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {
    /**
     * ID 목록으로 팀을 조회할 때 관련 엔터티를 함께 로드합니다. EntityGraph를 사용하여 N+1 문제를 방지합니다.
     *
     * @param ids 조회할 팀 ID 목록
     * @return 조회된 팀 엔터티 목록
     */
    @EntityGraph(attributePaths = {"teamScales", "teamRegions", "teamCurrentStates"})
    List<Team> findAllByIdIn(List<Long> ids);
}
