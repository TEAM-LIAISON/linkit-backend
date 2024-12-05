package liaison.linkit.global.config.csv.teamState;

import liaison.linkit.team.domain.TeamState;
import liaison.linkit.team.domain.repository.currentState.TeamStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvTeamStateWriter implements ItemWriter<TeamStateCsvData> {
    private final TeamStateRepository teamStateRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends TeamStateCsvData> chunk) throws Exception {

        Chunk<TeamState> teamStates = new Chunk<>();

        chunk.forEach(teamStateCsvData -> {
            TeamState teamState = TeamState.of(teamStateCsvData.getTeamStateName());
            teamStates.add(teamState);
        });

        teamStateRepository.saveAll(teamStates);
    }
}
