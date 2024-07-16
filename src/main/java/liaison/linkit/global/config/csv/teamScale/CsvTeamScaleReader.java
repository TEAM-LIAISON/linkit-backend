package liaison.linkit.global.config.csv.teamScale;

import liaison.linkit.team.dto.csv.TeamScaleCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class CsvTeamScaleReader {
    @Value("${teamScale.csv-path}")
    private String teamScaleCsv;

    @Bean(name = "teamScaleCsvReader")
    public FlatFileItemReader<TeamScaleCsvData> csvTeamScaleReader() {
        // 파일 경로 지정 및 인코딩
        FlatFileItemReader<TeamScaleCsvData> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource(teamScaleCsv));
        flatFileItemReader.setEncoding("UTF-8");

        // 데이터 내부에 개행이 있으면 꼭! 추가해주세요
        flatFileItemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        // 읽어온 파일을 한 줄씩 읽기
        DefaultLineMapper<TeamScaleCsvData> defaultLineMapper = new DefaultLineMapper<>();
        // 따로 설정하지 않으면 기본값은 ","
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        // "name", "phoneNumber", "comment", "address" 필드 설정
        delimitedLineTokenizer.setNames(TeamScaleCsvData.getFieldNames().toArray(String[]::new));
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        // 매칭할 class 타입 지정(필드 지정)
        BeanWrapperFieldSetMapper<TeamScaleCsvData> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TeamScaleCsvData.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;
    }
}
