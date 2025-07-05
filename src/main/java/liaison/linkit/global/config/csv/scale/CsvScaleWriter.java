package liaison.linkit.global.config.csv.scale;

import liaison.linkit.team.domain.repository.teamScale.ScaleRepository;
import liaison.linkit.team.domain.scale.Scale;
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
public class CsvScaleWriter implements ItemWriter<ScaleCsvData> {

    private final ScaleRepository scaleRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends ScaleCsvData> chunk) throws Exception {

        Chunk<Scale> scales = new Chunk<>();

        chunk.forEach(
                scaleCsvData -> {
                    Scale scale = Scale.builder().scaleName(scaleCsvData.getScaleName()).build();
                    scales.add(scale);
                });

        scaleRepository.saveAll(scales);
    }
}
