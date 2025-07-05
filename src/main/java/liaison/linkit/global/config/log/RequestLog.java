package liaison.linkit.global.config.log;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestLog {
    private String requestId;
    private String clientIp;
    private String userId;
    private String item;
    private String action;
    private String result;
    private String uri;
    private String method;
    private String httpMethod;
    private LocalDateTime timestamp;
    private long executionTimeMs;
    private Object params;
    private Object responseData;
    private String exceptionName;
    private String exceptionMessage;
}
