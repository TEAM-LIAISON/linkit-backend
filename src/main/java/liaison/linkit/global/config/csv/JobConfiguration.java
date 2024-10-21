package liaison.linkit.global.config.csv;

import liaison.linkit.global.config.csv.activityMethodTag.CsvActivityMethodTagReader;
import liaison.linkit.global.config.csv.activityMethodTag.CsvActivityMethodTagWriter;
import liaison.linkit.global.config.csv.industrySector.CsvIndustrySectorReader;
import liaison.linkit.global.config.csv.industrySector.CsvIndustrySectorWriter;
import liaison.linkit.global.config.csv.jobRole.CsvJobRoleReader;
import liaison.linkit.global.config.csv.jobRole.CsvJobRoleWriter;
import liaison.linkit.global.config.csv.region.CsvRegionReader;
import liaison.linkit.global.config.csv.region.CsvRegionWriter;
import liaison.linkit.global.config.csv.skill.CsvSkillReader;
import liaison.linkit.global.config.csv.skill.CsvSkillWriter;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldReader;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldWriter;
import liaison.linkit.global.config.csv.teamScale.CsvTeamScaleReader;
import liaison.linkit.global.config.csv.teamScale.CsvTeamScaleWriter;
import liaison.linkit.profile.dto.csv.*;
import liaison.linkit.team.dto.csv.ActivityMethodTagCsvData;
import liaison.linkit.team.dto.csv.IndustrySectorCsvData;
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


    private final CsvTeamBuildingFieldReader csvTeamBuildingFieldReader;
    private final CsvTeamBuildingFieldWriter csvTeamBuildingFieldWriter;

    private final CsvRegionReader csvRegionReader;
    private final CsvRegionWriter csvRegionWriter;

    private final CsvTeamScaleReader csvTeamScaleReader;
    private final CsvTeamScaleWriter csvTeamScaleWriter;

    private final CsvIndustrySectorReader csvIndustrySectorReader;
    private final CsvIndustrySectorWriter csvIndustrySectorWriter;

    private final CsvActivityMethodTagReader csvActivityMethodTagReader;
    private final CsvActivityMethodTagWriter csvActivityMethodTagWriter;

    private final CsvJobRoleReader csvJobRoleReader;
    private final CsvJobRoleWriter csvJobRoleWriter;

    private final CsvSkillReader csvSkillReader;
    private final CsvSkillWriter csvSkillWriter;

    // Step & Job upload
    @Bean
    public Job simpleDataLoadJob(JobRepository jobRepository,
                                 Step teamBuildingFieldDataLoadStep,
                                 Step regionDataLoadStep,
                                 Step teamScaleDataLoadStep,
                                 Step industrySectorDataLoadStep,
                                 Step activityMethodTagDataLoadStep,
                                 Step jobRoleDataLoadStep,
                                 Step skillDataLoadStep) {
        return new JobBuilder("linkitInformationLoadJob", jobRepository)
                .start(teamBuildingFieldDataLoadStep)
                .next(regionDataLoadStep)
                .next(teamScaleDataLoadStep)
                .next(industrySectorDataLoadStep)
                .next(activityMethodTagDataLoadStep)
                .next(jobRoleDataLoadStep)
                .next(skillDataLoadStep)
                .build();
        // 필요 시 listener 추가 가능
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

    @Bean
    public Step industrySectorDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("industrySectorDataLoadStep", jobRepository)
                .<IndustrySectorCsvData, IndustrySectorCsvData>chunk(10, platformTransactionManager)
                .reader(csvIndustrySectorReader.csvIndustrySectorReader())
                .writer(csvIndustrySectorWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step activityMethodTagDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("activityMethodTagDataLoadStep", jobRepository)
                .<ActivityMethodTagCsvData, ActivityMethodTagCsvData>chunk(10, platformTransactionManager)
                .reader(csvActivityMethodTagReader.csvActivityMethodTagReader())
                .writer(csvActivityMethodTagWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step jobRoleDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("jobRoleDataLoadStep", jobRepository)
                .<JobRoleCsvData, JobRoleCsvData>chunk(10, platformTransactionManager)
                .reader(csvJobRoleReader.csvJobRoleReader())
                .writer(csvJobRoleWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step skillDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("skillDataLoadStep", jobRepository)
                .<SkillCsvData, SkillCsvData>chunk(10, platformTransactionManager)
                .reader(csvSkillReader.csvRegionReader())
                .writer(csvSkillWriter)
                .allowStartIfComplete(true)
                .build();
    }

}
