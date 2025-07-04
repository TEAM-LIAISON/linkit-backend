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
public class CertificationNotificationScheduler {
    private final JobLauncher jobLauncher;
    private final Job certificationNotificationProgressJob;

    public CertificationNotificationScheduler(
            JobLauncher jobLauncher, Job certificationNotificationProgressJob) {
        this.jobLauncher = jobLauncher;
        this.certificationNotificationProgressJob = certificationNotificationProgressJob;
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul") // 1분마다 실행
    public void scheduleCertificationNotificationJob() throws Exception {
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("uuid", UUID.randomUUID().toString())
                        .toJobParameters();

        jobLauncher.run(certificationNotificationProgressJob, params);
    }
}
