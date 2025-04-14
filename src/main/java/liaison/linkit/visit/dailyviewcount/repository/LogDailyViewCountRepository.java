package liaison.linkit.visit.dailyviewcount.repository;

import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogDailyViewCountRepository extends JpaRepository<LogDailyViewCount, Long> {}
