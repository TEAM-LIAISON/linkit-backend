package liaison.linkit.team.domain.repository.activity;

import liaison.linkit.team.domain.activity.ActivityMethodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityMethodTagRepository extends JpaRepository<ActivityMethodTag, Long> {

    @Query("""
           SELECT a
           FROM ActivityMethodTag  a
           WHERE a.activityTagName in :activityTagNames
           """)
    List<ActivityMethodTag> findActivityMethodTagByActivityTagNames(@Param("activityTagNames") final List<String> activityTagNames);
}
