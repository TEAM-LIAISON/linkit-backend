package liaison.linkit.global.config.csv.teamScale;

import liaison.linkit.team.domain.repository.teamScale.TeamScaleRepository;
import liaison.linkit.team.domain.scale.TeamScale;
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
public class CsvTeamScaleWriter implements ItemWriter<TeamScaleCsvData> {

    private final TeamScaleRepository teamScaleRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends TeamScaleCsvData> chunk) throws Exception {

        Chunk<TeamScale> teamScales = new Chunk<>();

        chunk.forEach(teamScaleCsvData -> {
            TeamScale teamScale = TeamScale.builder().scaleName(teamScaleCsvData.getScaleName()).build();
            teamScales.add(teamScale);
        });

        teamScaleRepository.saveAll(teamScales);
    }
}
