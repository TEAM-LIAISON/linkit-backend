package liaison.linkit.mail.scheduler;

import java.util.UUID;

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
public class AnnouncementAdvertiseScheduler {

    @Autowired private JobLauncher jobLauncher;

    @Autowired private Job announcementAdvertiseJob;

    @Scheduled(cron = "0 0 23 * * ?") // 매일 밤 11시에 실행
    public void scheduleAnnouncementAdvertiseJob() throws Exception {
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("uuid", UUID.randomUUID().toString())
                        .toJobParameters();

        jobLauncher.run(announcementAdvertiseJob, params);
    }
}
