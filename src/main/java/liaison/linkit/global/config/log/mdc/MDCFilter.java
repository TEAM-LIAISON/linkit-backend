package liaison.linkit.global.config.log.mdc;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    private static final String REQUEST_ID = "request_id";
    private static final String CLIENT_IP = "client_ip";
    private static final String USER_ID = "user_id";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        // 요청 및 응답 래핑 (필요시 body 접근용)
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // MDC에 정보 설정
            MDC.put(REQUEST_ID, UUID.randomUUID().toString());
            MDC.put(CLIENT_IP, getClientIp(request));

            // 인증된 사용자 ID 가져오기 (Spring Security 사용 가정)
            String userId = extractUserIdFromRequest(request);
            if (userId != null) {
                MDC.put(USER_ID, userId);
            }

            // 필터 체인 실행
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 반드시 responseWrapper의 content 복사 필요
            responseWrapper.copyBodyToResponse();
            // MDC 정리
            MDC.clear();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            clientIp = xForwardedFor.split(",")[0].trim();
        }

        return clientIp;
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        // Spring Security 사용 시 SecurityContextHolder에서 Authentication 객체 가져와 사용자 정보 추출
        // 간략한 예시로 헤더에서 가져옴 (실제로는 SecurityContextHolder 사용 권장)
        return request.getHeader("X-User-ID");
    }
}
