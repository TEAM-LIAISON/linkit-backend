package liaison.linkit.global.config.csv;

import liaison.linkit.profile.domain.education.University;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.dto.csv.UniversityCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvUniversityWriter implements ItemWriter<UniversityCsvData> {
    private final UniversityRepository universityRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends UniversityCsvData> chunk) throws Exception {
        log.info("write 실행 여부 1");
        Chunk<University> universities = new Chunk<>();

        chunk.forEach(universityCsvData -> {
            University university = University.of(universityCsvData.getUniversityName());
            universities.add(university);
        });

        log.info("universities={}", universities);
        universityRepository.saveAll(universities);
    }
}
