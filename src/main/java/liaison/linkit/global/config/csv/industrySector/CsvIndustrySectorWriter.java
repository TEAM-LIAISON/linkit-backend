package liaison.linkit.global.config.csv.industrySector;

import liaison.linkit.team.domain.miniprofile.IndustrySector;
import liaison.linkit.team.domain.repository.miniprofile.IndustrySectorRepository;
import liaison.linkit.team.dto.csv.IndustrySectorCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvIndustrySectorWriter implements ItemWriter<IndustrySectorCsvData> {
    private final IndustrySectorRepository industrySectorRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends IndustrySectorCsvData> chunk) throws Exception {

        Chunk<IndustrySector> industrySectors = new Chunk<>();

        chunk.forEach(industrySectorCsvData -> {
            IndustrySector industrySector = IndustrySector.of(industrySectorCsvData.getSectorName());
            industrySectors.add(industrySector);
        });

        industrySectorRepository.saveAll(industrySectors);
    }
}
