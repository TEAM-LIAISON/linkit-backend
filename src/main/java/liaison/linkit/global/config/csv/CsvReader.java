package liaison.linkit.global.config.csv;

import liaison.linkit.profile.dto.csv.UniversityCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvReader {
    // Reader 목록
    // University - 학교명(대학)
    // Major - 전공(중복 제거)
    // Degree - 학위(정해진 학위 세트 있음)
    // 희망 팀빌딩 분야
    // 주요 기술

    // 추가 필요
    @Value("${university.csv-path}")
    private String universityCsv;

    @Bean
    public FlatFileItemReader<UniversityCsvData> csvUniversityReader() {
        log.info("어디서 터질까 1");
        // 파일 경로 지정 및 인코딩
        FlatFileItemReader<UniversityCsvData> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource(universityCsv));
        flatFileItemReader.setEncoding("UTF-8");
        log.info("어디서 터질까 2");
        // 데이터 내부에 개행이 있으면 꼭! 추가해주세요
        flatFileItemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        log.info("어디서 터질까 3");
        // 읽어온 파일을 한 줄씩 읽기
        DefaultLineMapper<UniversityCsvData> defaultLineMapper = new DefaultLineMapper<>();
        // 따로 설정하지 않으면 기본값은 ","
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        // "name", "phoneNumber", "comment", "address" 필드 설정
        delimitedLineTokenizer.setNames(UniversityCsvData.getFieldNames().toArray(String[]::new));
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        // 매칭할 class 타입 지정(필드 지정)
        BeanWrapperFieldSetMapper<UniversityCsvData> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(UniversityCsvData.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);
        log.info("어디서 터질까 4");
        log.info("flatFileItemReader={}", flatFileItemReader);

        return flatFileItemReader;
    }
}
