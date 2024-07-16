package liaison.linkit.global.config.csv.degree;

import liaison.linkit.profile.dto.csv.DegreeCsvData;
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
public class CsvDegreeReader {

    @Value("${degree.csv-path}")
    private String degreeCsv;

    @Bean(name = "degreeCsvReader")
    public FlatFileItemReader<DegreeCsvData> csvIndustrySectorReader() {
        // 파일 경로 지정 및 인코딩
        FlatFileItemReader<DegreeCsvData> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource(degreeCsv));
        flatFileItemReader.setEncoding("UTF-8");

        // 데이터 내부에 개행이 있으면 꼭! 추가해주세요
        flatFileItemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        // 읽어온 파일을 한 줄씩 읽기
        DefaultLineMapper<DegreeCsvData> defaultLineMapper = new DefaultLineMapper<>();
        // 따로 설정하지 않으면 기본값은 ","
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        // "name", "phoneNumber", "comment", "address" 필드 설정
        delimitedLineTokenizer.setNames(DegreeCsvData.getFieldNames().toArray(String[]::new));
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        // 매칭할 class 타입 지정(필드 지정)
        BeanWrapperFieldSetMapper<DegreeCsvData> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(DegreeCsvData.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }
}
