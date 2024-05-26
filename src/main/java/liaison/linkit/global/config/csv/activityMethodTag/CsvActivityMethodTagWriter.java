package liaison.linkit.global.config.csv.activityMethodTag;

import liaison.linkit.team.domain.activity.ActivityMethodTag;
import liaison.linkit.team.domain.repository.activity.ActivityMethodTagRepository;
import liaison.linkit.team.dto.csv.ActivityMethodTagCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvActivityMethodTagWriter implements ItemWriter<ActivityMethodTagCsvData> {

    private final ActivityMethodTagRepository activityMethodTagRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends ActivityMethodTagCsvData> chunk) throws Exception {

        Chunk<ActivityMethodTag> activityMethodTags = new Chunk<>();

        chunk.forEach(activityMethodTagCsvData -> {
            ActivityMethodTag activityMethodTag = ActivityMethodTag.of(activityMethodTagCsvData.getActivityTagName());
            activityMethodTags.add(activityMethodTag);
        });

        activityMethodTagRepository.saveAll(activityMethodTags);
    }
}
