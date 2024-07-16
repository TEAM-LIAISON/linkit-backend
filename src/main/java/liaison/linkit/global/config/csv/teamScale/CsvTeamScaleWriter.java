package liaison.linkit.global.config.csv.teamScale;

import liaison.linkit.team.domain.miniprofile.TeamScale;
import liaison.linkit.team.domain.repository.miniprofile.TeamScaleRepository;
import liaison.linkit.team.dto.csv.TeamScaleCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

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
            TeamScale teamScale = TeamScale.of(teamScaleCsvData.getSizeType());
            teamScales.add(teamScale);
        });

        teamScaleRepository.saveAll(teamScales);
    }
}
