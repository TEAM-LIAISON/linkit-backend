package liaison.linkit.global.config.csv;

import liaison.linkit.global.config.csv.jobRole.CsvJobRoleReader;
import liaison.linkit.global.config.csv.jobRole.CsvJobRoleWriter;
import liaison.linkit.global.config.csv.position.CsvPositionReader;
import liaison.linkit.global.config.csv.position.CsvPositionWriter;
import liaison.linkit.global.config.csv.position.PositionCsvData;
import liaison.linkit.global.config.csv.profileState.CsvProfileStateReader;
import liaison.linkit.global.config.csv.profileState.CsvProfileStateWriter;
import liaison.linkit.global.config.csv.profileState.ProfileStateCsvData;
import liaison.linkit.global.config.csv.region.CsvRegionReader;
import liaison.linkit.global.config.csv.region.CsvRegionWriter;
import liaison.linkit.global.config.csv.skill.CsvSkillReader;
import liaison.linkit.global.config.csv.skill.CsvSkillWriter;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldReader;
import liaison.linkit.global.config.csv.teamBuildingField.CsvTeamBuildingFieldWriter;
import liaison.linkit.global.config.csv.scale.CsvScaleReader;
import liaison.linkit.global.config.csv.scale.CsvScaleWriter;
import liaison.linkit.global.config.csv.scale.ScaleCsvData;
import liaison.linkit.global.config.csv.teamState.CsvTeamStateReader;
import liaison.linkit.global.config.csv.teamState.CsvTeamStateWriter;
import liaison.linkit.global.config.csv.teamState.TeamStateCsvData;
import liaison.linkit.global.config.csv.university.CsvUniversityReader;
import liaison.linkit.global.config.csv.university.CsvUniversityWriter;
import liaison.linkit.global.config.csv.university.UniversityCsvData;
import liaison.linkit.profile.csv.JobRoleCsvData;
import liaison.linkit.profile.csv.RegionCsvData;
import liaison.linkit.profile.csv.SkillCsvData;
import liaison.linkit.profile.csv.TeamBuildingFieldCsvData;
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

    private final CsvScaleReader csvScaleReader;
    private final CsvScaleWriter csvScaleWriter;

    private final CsvRegionReader csvRegionReader;
    private final CsvRegionWriter csvRegionWriter;

    private final CsvJobRoleReader csvJobRoleReader;
    private final CsvJobRoleWriter csvJobRoleWriter;

    private final CsvSkillReader csvSkillReader;
    private final CsvSkillWriter csvSkillWriter;

    private final CsvPositionReader csvPositionReader;
    private final CsvPositionWriter csvPositionWriter;

    private final CsvProfileStateReader csvProfileStateReader;
    private final CsvProfileStateWriter csvProfileStateWriter;

    private final CsvTeamStateReader csvTeamStateReader;
    private final CsvTeamStateWriter csvTeamStateWriter;

    private final CsvUniversityReader csvUniversityReader;
    private final CsvUniversityWriter csvUniversityWriter;

    // Step & Job upload
    @Bean
    public Job simpleDataLoadJob(JobRepository jobRepository,
                                 Step teamScaleDataLoadStep,
                                 Step teamBuildingFieldDataLoadStep,
                                 Step regionDataLoadStep,
                                 Step jobRoleDataLoadStep,
                                 Step positionDataLoadStep,
                                 Step profileStateDataLoadStep,
                                 Step teamStateDataLoadStep,
                                 Step skillDataLoadStep,
                                 Step universityDataLoadStep) {
        return new JobBuilder("linkitInformationLoadJob", jobRepository)
                .start(teamBuildingFieldDataLoadStep)
                .next(teamScaleDataLoadStep)
                .next(regionDataLoadStep)
                .next(jobRoleDataLoadStep)
                .next(positionDataLoadStep)
                .next(profileStateDataLoadStep)
                .next(teamStateDataLoadStep)
                .next(skillDataLoadStep)
                .next(universityDataLoadStep)
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
    public Step teamScaleDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("teamScaleDataLoadStep", jobRepository)
                .<ScaleCsvData, ScaleCsvData>chunk(5, platformTransactionManager)
                .reader(csvScaleReader.csvScaleReader())
                .writer(csvScaleWriter)
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
    public Step positionDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("positionDataLoadStep", jobRepository)
                .<PositionCsvData, PositionCsvData>chunk(10, platformTransactionManager)
                .reader(csvPositionReader.csvPositionReader())
                .writer(csvPositionWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step profileStateDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("profileStateDataLoadStep", jobRepository)
                .<ProfileStateCsvData, ProfileStateCsvData>chunk(10, platformTransactionManager)
                .reader(csvProfileStateReader.csvProfileStateReader())
                .writer(csvProfileStateWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step teamStateDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("teamStateDataLoadStep", jobRepository)
                .<TeamStateCsvData, TeamStateCsvData>chunk(10, platformTransactionManager)
                .reader(csvTeamStateReader.csvTeamStateReader())
                .writer(csvTeamStateWriter)
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

    @Bean
    public Step universityDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("universityDataLoadStep", jobRepository)
                .<UniversityCsvData, UniversityCsvData>chunk(10, platformTransactionManager)
                .reader(csvUniversityReader.csvUniversityFieldReader())
                .writer(csvUniversityWriter)
                .allowStartIfComplete(true)
                .build();
    }

}
