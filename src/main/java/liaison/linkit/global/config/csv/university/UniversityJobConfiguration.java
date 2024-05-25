package liaison.linkit.global.config.csv.university;

import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldReader;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldWriter;
import liaison.linkit.profile.dto.csv.TeamBuildingFieldCsvData;
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
public class UniversityJobConfiguration {

    private final CsvUniversityReader csvUniversityReader;
    private final CsvUniversityWriter csvUniversityWriter;


    private final CsvTeamBuildingFieldReader csvTeamBuildingFieldReader;
    private final CsvTeamBuildingFieldWriter csvTeamBuildingFieldWriter;

    // Step & Job upload
    @Bean
    public Job universityDataLoadJob(JobRepository jobRepository, Step universityDataLoadStep, Step teamBuildingFieldDataLoadStep) {
        return new JobBuilder("universityInformationLoadJob", jobRepository)
                .start(universityDataLoadStep) // 스텝 실행
                .next(teamBuildingFieldDataLoadStep)
                .build();
        // 필요 시 listener 추가 가능
    }

    @Bean
    public Step universityDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager) {
            return new StepBuilder("universityDataLoadStep", jobRepository)
                .<UniversityCsvData, UniversityCsvData>chunk(500, platformTransactionManager)
                .reader(csvUniversityReader.csvUniversityReader())
                .writer(csvUniversityWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step teamBuildingFieldDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("teamBuildingFieldDataLoadStep", jobRepository)
                .<TeamBuildingFieldCsvData, TeamBuildingFieldCsvData>chunk(500, platformTransactionManager)
                .reader(csvTeamBuildingFieldReader.csvTeamBuildingFieldReader())
                .writer(csvTeamBuildingFieldWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
