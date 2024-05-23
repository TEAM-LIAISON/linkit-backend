//package liaison.linkit.global.config.csv;
//
//import liaison.linkit.profile.dto.csv.UniversityDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class CsvReader {
//    /**
//     * 대학 정보 파일 읽기
//     */
//
//    @Bean
//    public FlatFileItemReader<UniversityDto> csvUniversityReader(){
//        /* 파일읽기 */
//        FlatFileItemReader<UniversityDto> flatFileItemReader = new FlatFileItemReader<>();
//        flatFileItemReader.setResource(new ClassPathResource("/csv/university.csv")); //읽을 파일 경로 지정
//        flatFileItemReader.setEncoding("UTF-8"); //인토딩 설정
//
//        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
//        DefaultLineMapper<UniversityDto> defaultLineMapper = new DefaultLineMapper<>();
//        log.info("csv 읽기 실행 여부");
//        /* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
//        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); //csv 파일에서 구분자
//        delimitedLineTokenizer.setNames("universityName"); //행으로 읽은 데이터 매칭할 데이터 각 이름
//        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); //lineTokenizer 설정
//
//        /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
//        BeanWrapperFieldSetMapper<UniversityDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
//        beanWrapperFieldSetMapper.setTargetType(UniversityDto.class);
//
//        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); //fieldSetMapper 지정
//
//        flatFileItemReader.setLineMapper(defaultLineMapper); //lineMapper 지정
//
//        return flatFileItemReader;
//
//    }
//}
