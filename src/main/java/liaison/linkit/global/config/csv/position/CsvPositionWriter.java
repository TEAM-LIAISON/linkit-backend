package liaison.linkit.global.config.csv.position;

import liaison.linkit.common.domain.Position;
import liaison.linkit.profile.domain.repository.position.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvPositionWriter implements ItemWriter<PositionCsvData> {

    private final PositionRepository positionRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends PositionCsvData> chunk) throws Exception {

        Chunk<Position> positions = new Chunk<>();

        chunk.forEach(positionCsvData -> {
            Position position = Position.of(positionCsvData.getMajorPosition(), positionCsvData.getSubPosition());
            positions.add(position);
        });

        positionRepository.saveAll(positions);
    }
}
