//package liaison.linkit.global.config.csv;
//
//import liaison.linkit.profile.domain.education.University;
//import liaison.linkit.profile.domain.repository.education.UniversityRepository;
//import liaison.linkit.profile.dto.csv.UniversityDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class CsvUniversityWriter implements ItemWriter<UniversityDto> {  // Ensure this is the correct interface
//
//    private final UniversityRepository universityRepository;
//
//    @Override
//    public void write(List<? extends UniversityDto> items) throws Exception {
//        List<University> universityList = new ArrayList<>();
//        try {
//            for (UniversityDto dto : items) {
//                University university = dto.toEntity();
//                universityList.add(university);
//                log.info("Converting DTO to entity: {}", university);
//            }
//            universityRepository.saveAll(universityList);
//            log.info("Successfully saved {} universities.", universityList.size());
//        } catch (Exception e) {
//            log.error("Error saving universities: {}", e.getMessage());
//            throw e;  // Properly handle the exception
//        }
//    }
//}
