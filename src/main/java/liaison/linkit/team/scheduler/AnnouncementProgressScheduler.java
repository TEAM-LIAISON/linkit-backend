package liaison.linkit.team.scheduler;

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
public class AnnouncementProgressScheduler {

    @Autowired private JobLauncher jobLauncher;

    @Autowired private Job announcementProgressJob;

    @Scheduled(cron = "0 1 0 * * ?", zone = "Asia/Seoul") // 매일 자정 1분 후(00시 01분)에 실행
    public void scheduleAnnouncementProgressJob() throws Exception {
        JobParameters params =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addString("uuid", UUID.randomUUID().toString())
                        .toJobParameters();

        jobLauncher.run(announcementProgressJob, params);
    }
}
