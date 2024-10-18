package liaison.linkit.global.config.csv.degree;

import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.dto.csv.DegreeCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvDegreeWriter implements ItemWriter<DegreeCsvData> {

    private final DegreeRepository degreeRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends DegreeCsvData> chunk) throws Exception {

        Chunk<Degree> degrees = new Chunk<>();

        chunk.forEach(degreeCsvData -> {
            Degree degree = Degree.of(degreeCsvData.getDegreeName());
            degrees.add(degree);
        });

        degreeRepository.saveAll(degrees);
    }
}
