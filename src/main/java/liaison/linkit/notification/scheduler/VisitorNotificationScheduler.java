package liaison.linkit.notification.scheduler;

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
public class VisitorNotificationScheduler {
    private final JobLauncher jobLauncher;
    private final Job visitorNotificationProgressJob;

    public VisitorNotificationScheduler(
            JobLauncher jobLauncher, Job visitorNotificationProgressJob) {
        this.jobLauncher = jobLauncher;
        this.visitorNotificationProgressJob = visitorNotificationProgressJob;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul") // 매일 아침 8시에 실행
    public void scheduleVisitorNotificationJob() throws Exception {
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("uuid", UUID.randomUUID().toString())
                        .toJobParameters();

        jobLauncher.run(visitorNotificationProgressJob, params);
    }
}
