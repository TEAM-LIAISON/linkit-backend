package liaison.linkit.global.config.csv.profileState;

import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.profile.domain.repository.currentState.ProfileStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvProfileStateWriter implements ItemWriter<ProfileStateCsvData> {
    private final ProfileStateRepository profileStateRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends ProfileStateCsvData> chunk) throws Exception {

        Chunk<ProfileState> profileStates = new Chunk<>();

        chunk.forEach(
                profileStateCsvData -> {
                    ProfileState profileState =
                            ProfileState.of(profileStateCsvData.getProfileStateName());
                    profileStates.add(profileState);
                });

        profileStateRepository.saveAll(profileStates);
    }
}
