//package liaison.linkit.global.config.csv;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class FileReaderJobConfig {
//
//    private final PlatformTransactionManager transactionManager;
//    private static final int chunkSize = 2000; //데이터 처리할 row size
//
//    @Bean
//    public Job myJob(JobRepository jobRepository){
//        return new JobBuilder("myJob", jobRepository)
//                .start(myStep(jobRepository))
//                .build();
//    }
//
//    @Bean
//    public Step myStep(JobRepository jobRepository){
//        return new StepBuilder("myStep",jobRepository)
//                .<String, String>chunk(1000, transactionManager)
////                .reader(itemReader())
////                .writer(itemWriter())
//                .build();
//    }
//
////    @Bean
////    public ItemReader<String> itemReader(){
////        return new ItemReader<String>() {
////            @Override
////            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
////                return "Read OK";
////            }
////        };
////    }
////
////    @Bean
////    public ItemWriter<String> itemWriter(){
////        return strList -> {
////            strList.forEach(
////                    str -> log.info("str: {}", str)
////            );
////        };
////    }
//}
