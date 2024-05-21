package liaison.linkit.team.domain.repository.Activity;

import liaison.linkit.team.domain.activity.ActivityMethodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityMethodTagRepository extends JpaRepository<ActivityMethodTag, Long> {

    @Query("SELECT amt FROM ActivityMethodTag amt WHERE amt.activityTagName = :activityTagName")
    ActivityMethodTag findActivityRegionByActivityTagName(@Param("activityTagName") String activityTagName);
}
