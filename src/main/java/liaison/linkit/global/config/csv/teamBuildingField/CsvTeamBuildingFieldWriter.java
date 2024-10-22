package liaison.linkit.global.config.csv.teamBuildingField;

import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.profile.csv.TeamBuildingFieldCsvData;
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
public class CsvTeamBuildingFieldWriter implements ItemWriter<TeamBuildingFieldCsvData> {

    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends TeamBuildingFieldCsvData> chunk) throws Exception {

        Chunk<TeamBuildingField> teamBuildingFields = new Chunk<>();

        chunk.forEach(teamBuildingFieldCsvData -> {
            TeamBuildingField teamBuildingField = TeamBuildingField.of(teamBuildingFieldCsvData.getTeamBuildingFieldName());
            teamBuildingFields.add(teamBuildingField);
        });

        teamBuildingFieldRepository.saveAll(teamBuildingFields);
    }
}
