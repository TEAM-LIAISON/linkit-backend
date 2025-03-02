package liaison.linkit.chat.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class UnreadMessageNotificationScheduler {

    @Autowired private JobLauncher jobLauncher;

    @Autowired private Job unreadMessageNotificationJob;

    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    public void scheduleUnreadMessageNotificationJob() throws Exception {
        // Spring Batch 5.0+ 스타일의 JobParameters 생성
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

        jobLauncher.run(unreadMessageNotificationJob, params);
    }
}
