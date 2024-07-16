package liaison.linkit.global.config.csv.major;

import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.repository.education.MajorRepository;
import liaison.linkit.profile.dto.csv.MajorCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvMajorWriter implements ItemWriter<MajorCsvData> {

    private final MajorRepository majorRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends MajorCsvData> chunk) throws Exception {

        Chunk<Major> majors = new Chunk<>();

        chunk.forEach(majorCsvData -> {
            Major major = Major.of(majorCsvData.getMajorName());
            majors.add(major);
        });

        majorRepository.saveAll(majors);
    }
}
