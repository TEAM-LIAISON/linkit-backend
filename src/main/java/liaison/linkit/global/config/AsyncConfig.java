package liaison.linkit.global.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** 비동기 작업을 위한 Spring 설정 클래스 @EnableAsync 어노테이션을 통해 비동기 기능 활성화 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 비동기 작업을 실행할 ThreadPool 설정
     *
     * @return ThreadPoolTaskExecutor 인스턴스
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 기본 실행 대기하는 Thread 수
        executor.setCorePoolSize(5);
        // 동시 동작하는 최대 Thread 수
        executor.setMaxPoolSize(10);
        // MaxPoolSize가 초과하여 Thread 생성 요청 시, 해당 요청을 Queue에 저장하는 용량
        executor.setQueueCapacity(25);
        // 생성되는 Thread 접두사
        executor.setThreadNamePrefix("MatchingAsync-");
        executor.initialize();
        return executor;
    }

    /**
     * 공고 알림 등 특정 비동기 작업에 사용할 전용 ThreadPool 설정 Bean 이름을 지정해두고, @Async(\"announcementTaskExecutor\")로
     * 사용 가능
     */
    @Bean(name = "announcementTaskExecutor")
    public Executor announcementTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 공고 알림의 특성에 맞게 풀 사이즈 등을 설정
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AnnouncementAsync-");
        executor.initialize();
        return executor;
    }

    /**
     * 비동기 작업 중 발생한 예외를 처리하는 핸들러 설정
     *
     * @return SimpleAsyncUncaughtExceptionHandler 인스턴스
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
