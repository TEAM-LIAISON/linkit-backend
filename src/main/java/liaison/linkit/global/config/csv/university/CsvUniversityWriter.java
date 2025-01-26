package liaison.linkit.global.config.csv.university;

import liaison.linkit.common.domain.University;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
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

        Chunk<University> universities = new Chunk<>();

        chunk.forEach(universityCsvData -> {
            University university = University.builder().universityName(universityCsvData.getUniversityName()).build();
            universities.add(university);
        });

        universityRepository.saveAll(universities);
    }
}
