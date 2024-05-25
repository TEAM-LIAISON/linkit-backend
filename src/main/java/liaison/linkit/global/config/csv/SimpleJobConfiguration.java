package liaison.linkit.global.config.csv;

import liaison.linkit.profile.dto.csv.UniversityCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {

    private final CsvReader csvReader;
    private final CsvUniversityWriter csvUniversityWriter;

    // Step & Job upload
    @Bean
    public Job universityDataLoadJob(JobRepository jobRepository, Step universityDataLoadStep) {
        return new JobBuilder("universityInformationLoadJob", jobRepository)
                .start(universityDataLoadStep) // 스텝 실행
                .build();
        // 필요 시 listener 추가 가능
    }

    @Bean
    public Step universityDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager) {
            return new StepBuilder("universityDataLoadStep", jobRepository)
                .<UniversityCsvData, UniversityCsvData>chunk(500, platformTransactionManager)
                .reader(csvReader.csvUniversityReader())
                .writer(csvUniversityWriter)
                .allowStartIfComplete(true)
//			.listener(csvFileDeleteListener)
                .build();
    }

}
