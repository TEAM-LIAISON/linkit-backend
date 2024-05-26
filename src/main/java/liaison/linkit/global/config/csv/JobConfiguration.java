package liaison.linkit.global.config.csv;

import liaison.linkit.global.config.csv.major.CsvMajorReader;
import liaison.linkit.global.config.csv.major.CsvMajorWriter;
import liaison.linkit.global.config.csv.region.CsvRegionReader;
import liaison.linkit.global.config.csv.region.CsvRegionWriter;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldReader;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldWriter;
import liaison.linkit.global.config.csv.teamScale.CsvTeamScaleReader;
import liaison.linkit.global.config.csv.teamScale.CsvTeamScaleWriter;
import liaison.linkit.global.config.csv.university.CsvUniversityReader;
import liaison.linkit.global.config.csv.university.CsvUniversityWriter;
import liaison.linkit.profile.dto.csv.MajorCsvData;
import liaison.linkit.profile.dto.csv.RegionCsvData;
import liaison.linkit.profile.dto.csv.TeamBuildingFieldCsvData;
import liaison.linkit.profile.dto.csv.UniversityCsvData;
import liaison.linkit.team.dto.csv.TeamScaleCsvData;
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
public class JobConfiguration {

    private final CsvUniversityReader csvUniversityReader;
    private final CsvUniversityWriter csvUniversityWriter;

    private final CsvTeamBuildingFieldReader csvTeamBuildingFieldReader;
    private final CsvTeamBuildingFieldWriter csvTeamBuildingFieldWriter;

    private final CsvMajorReader csvMajorReader;
    private final CsvMajorWriter csvMajorWriter;

    private final CsvRegionReader csvRegionReader;
    private final CsvRegionWriter csvRegionWriter;

    private final CsvTeamScaleReader csvTeamScaleReader;
    private final CsvTeamScaleWriter csvTeamScaleWriter;

    // Step & Job upload
    @Bean
    public Job simpleDataLoadJob(JobRepository jobRepository,
                                 Step universityDataLoadStep,
                                 Step teamBuildingFieldDataLoadStep,
                                 Step majorDataLoadStep,
                                 Step regionDataLoadStep,
                                 Step teamScaleDataLoadStep) {
        return new JobBuilder("linkitInformationLoadJob", jobRepository)
                .start(universityDataLoadStep) // 스텝 실행
                .next(teamBuildingFieldDataLoadStep)
                .next(majorDataLoadStep)
                .next(regionDataLoadStep)
                .next(teamScaleDataLoadStep)
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

    @Bean
    public Step majorDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("majorDataLoadStep", jobRepository)
                .<MajorCsvData, MajorCsvData>chunk(1000, platformTransactionManager)
                .reader(csvMajorReader.csvMajorReader())
                .writer(csvMajorWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step regionDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("regionDataLoadStep", jobRepository)
                .<RegionCsvData, RegionCsvData>chunk(1000, platformTransactionManager)
                .reader(csvRegionReader.csvRegionReader())
                .writer(csvRegionWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step teamScaleDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("teamScaleDataLoadStep", jobRepository)
                .<TeamScaleCsvData, TeamScaleCsvData>chunk(10, platformTransactionManager)
                .reader(csvTeamScaleReader.csvTeamScaleReader())
                .writer(csvTeamScaleWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
