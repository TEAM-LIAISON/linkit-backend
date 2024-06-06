package liaison.linkit.global.config.csv.region;

import liaison.linkit.profile.dto.csv.RegionCsvData;
import liaison.linkit.region.domain.Region;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
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
public class CsvRegionWriter implements ItemWriter<RegionCsvData> {

    private final RegionRepository regionRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends RegionCsvData> chunk) throws Exception {

        Chunk<Region> regions = new Chunk<>();

        chunk.forEach(regionCsvData -> {
            Region region = Region.of(regionCsvData.getCityName(), regionCsvData.getDivisionName());
            regions.add(region);
        });

        regionRepository.saveAll(regions);
    }
}
