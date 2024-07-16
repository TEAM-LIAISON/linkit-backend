package liaison.linkit.global.config.csv.memberRole;

import liaison.linkit.member.dto.csv.MemberRoleCsvData;
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
public class CsvMemberRoleReader {
    @Value("${memberRole.csv-path}")
    private String memberRoleCsv;

    @Bean(name = "memberRoleCsvReader")
    public FlatFileItemReader<MemberRoleCsvData> csvMemberRoleReader() {

        FlatFileItemReader<MemberRoleCsvData> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource(memberRoleCsv));
        flatFileItemReader.setEncoding("UTF-8");

        // 데이터 내부에 개행이 있으면 꼭! 추가해주세요
        flatFileItemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        // 읽어온 파일을 한 줄씩 읽기
        DefaultLineMapper<MemberRoleCsvData> defaultLineMapper = new DefaultLineMapper<>();
        // 따로 설정하지 않으면 기본값은 ","
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setNames(MemberRoleCsvData.getFieldNames().toArray(String[]::new));
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        // 매칭할 class 타입 지정(필드 지정)
        BeanWrapperFieldSetMapper<MemberRoleCsvData> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(MemberRoleCsvData.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);
        return flatFileItemReader;
    }
}
