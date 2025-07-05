package liaison.linkit.chat.scheduler;

import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class UnreadMessageNotificationScheduler {

    private final JobLauncher jobLauncher;
    private final Job unreadMessageNotificationJob;

    public UnreadMessageNotificationScheduler(
            JobLauncher jobLauncher, Job unreadMessageNotificationJob) {
        this.jobLauncher = jobLauncher;
        this.unreadMessageNotificationJob = unreadMessageNotificationJob;
    }

    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    public void scheduleUnreadMessageNotificationJob() throws Exception {
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("uuid", UUID.randomUUID().toString())
                        .toJobParameters();

        jobLauncher.run(unreadMessageNotificationJob, params);
    }
}
