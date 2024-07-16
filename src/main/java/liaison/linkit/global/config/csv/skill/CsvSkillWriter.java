package liaison.linkit.global.config.csv.skill;

import liaison.linkit.profile.domain.repository.SkillRepository;
import liaison.linkit.profile.domain.skill.Skill;
import liaison.linkit.profile.dto.csv.SkillCsvData;
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
public class CsvSkillWriter implements ItemWriter<SkillCsvData> {
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends SkillCsvData> chunk) throws Exception {

        Chunk<Skill> skills = new Chunk<>();

        chunk.forEach(skillCsvData -> {
            Skill skill = Skill.of(skillCsvData.getSkillName());
            skills.add(skill);
        });

        skillRepository.saveAll(skills);
    }
}
