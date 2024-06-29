package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query("""
           SELECT s
           FROM Skill s
           WHERE s.skillName in :skillNames
            """)
    List<Skill> findSkillNamesBySkillNames(@Param("skillNames") List<String> skillNames);

    @Query("SELECT s FROM Skill s WHERE s.skillName = :skillName")
    Skill findBySkillName(
            @Param("skillName") final String skillName
    );
}
