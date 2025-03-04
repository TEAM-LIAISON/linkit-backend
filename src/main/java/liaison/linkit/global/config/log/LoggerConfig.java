package liaison.linkit.global.config.log;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Configuration
public class LoggerConfig {

    private final ObjectMapper objectMapper;

    public LoggerConfig() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Around("@annotation(liaison.linkit.global.config.log.Logging) && @annotation(logging)")
    public Object aroundLogger(ProceedingJoinPoint joinPoint, Logging logging) throws Throwable {
        RequestLog requestLog = new RequestLog();
        StopWatch stopWatch = new StopWatch();

        // 요청 정보 설정
        setRequestInfo(requestLog);

        // 메소드 정보 설정
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        requestLog.setMethod(methodSignature.getName());
        requestLog.setItem(logging.item());
        requestLog.setAction(logging.action());
        requestLog.setTimestamp(LocalDateTime.now());

        // 파라미터 로깅 설정
        if (logging.includeParams()) {
            setRequestParams(joinPoint, requestLog, logging.excludeParams());
        }

        Object result = null;
        stopWatch.start();

        try {
            result = joinPoint.proceed();
            stopWatch.stop();
            requestLog.setResult("success");

            // 응답 데이터 로깅 설정
            if (logging.includeResult() && result != null) {
                requestLog.setResponseData(result);
            }

        } catch (Throwable t) {
            stopWatch.stop();
            requestLog.setResult("fail");
            requestLog.setExceptionName(t.getClass().getName());
            requestLog.setExceptionMessage(t.getMessage());
            throw t;
        } finally {
            requestLog.setExecutionTimeMs(stopWatch.getTotalTimeMillis());
            logWithAppropriateLevel(logging.level(), requestLog);
        }

        return result;
    }

    private void setRequestInfo(RequestLog requestLog) {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();

            requestLog.setUri(request.getRequestURI());
            requestLog.setHttpMethod(request.getMethod());
            requestLog.setRequestId(MDC.get("request_id"));
            requestLog.setClientIp(MDC.get("client_ip"));
            requestLog.setUserId(MDC.get("user_id"));

        } catch (IllegalStateException e) {
            // 요청 컨텍스트를 가져올 수 없는 경우 (예: 배치 작업)
            log.debug("Cannot get request context", e);
        }
    }

    private void setRequestParams(
            ProceedingJoinPoint joinPoint, RequestLog requestLog, String[] excludeParams) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            // 제외할 파라미터 목록
            Map<String, Object> filteredParams = new LinkedHashMap<>();

            for (int i = 0; i < paramNames.length; i++) {
                if (!Arrays.asList(excludeParams).contains(paramNames[i])) {
                    filteredParams.put(paramNames[i], maskSensitiveData(paramValues[i]));
                }
            }

            requestLog.setParams(filteredParams);
        } catch (Exception e) {
            log.warn("Failed to log request parameters", e);
        }
    }

    private Object maskSensitiveData(Object data) {
        // 민감한 데이터(비밀번호, 토큰 등) 마스킹 로직
        // 예시로 간단한 처리만 구현
        if (data instanceof String) {
            String str = (String) data;
            if (str.length() > 0
                    && (str.toLowerCase().contains("password")
                            || str.toLowerCase().contains("token")
                            || str.toLowerCase().contains("secret"))) {
                return "********";
            }
        }
        return data;
    }

    private void logWithAppropriateLevel(LogLevel level, RequestLog requestLog) {
        try {
            String logMessage = objectMapper.writeValueAsString(requestLog);

            switch (level) {
                case DEBUG:
                    log.debug(logMessage);
                    break;
                case WARN:
                    log.warn(logMessage);
                    break;
                case ERROR:
                    log.error(logMessage);
                    break;
                case INFO:
                default:
                    log.info(logMessage);
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to generate log message", e);
        }
    }
}
