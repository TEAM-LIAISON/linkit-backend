package liaison.linkit.global.config.csv.jobRole;

import liaison.linkit.profile.domain.repository.jobRole.JobRoleRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.dto.csv.JobRoleCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvJobReaderWriter implements ItemWriter<JobRoleCsvData> {

    private final JobRoleRepository jobRoleRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends JobRoleCsvData> chunk) throws Exception {

        Chunk<JobRole> jobRoles = new Chunk<>();

        chunk.forEach(jobRoleCsvData -> {
            JobRole jobRole = JobRole.of(jobRoleCsvData.getJobRoleName());
            jobRoles.add(jobRole);
        });

        jobRoleRepository.saveAll(jobRoles);
    }
}
