package liaison.linkit.global.config.csv.university;

import liaison.linkit.profile.domain.education.University;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.dto.csv.UniversityCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

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
            University university = University.of(universityCsvData.getUniversityName());
            universities.add(university);
        });

        universityRepository.saveAll(universities);
    }
}
