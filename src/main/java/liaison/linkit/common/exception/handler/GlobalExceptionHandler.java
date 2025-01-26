package liaison.linkit.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liaison.linkit.common.exception.BaseCodeException;
import liaison.linkit.common.exception.BaseDynamicException;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import liaison.linkit.common.exception.GlobalErrorCode;
import liaison.linkit.common.presentation.ErrorResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        HttpServletRequest servletRequest = servletWebRequest.getRequest();

        // HttpServletRequest를 사용하여 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(servletRequest.getRequestURL().toString())
                .query(servletRequest.getQueryString()) // 쿼리 문자열 추가
                .build()
                .toUriString();

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                HttpStatus.valueOf(status.value()).name(),
                ex.getMessage(),
                url
        );

        return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
    }


    @SneakyThrows
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        HttpServletRequest servletRequest = servletWebRequest.getRequest();

        // HttpServletRequest를 사용하여 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl(servletRequest.getRequestURL().toString())
                .query(servletRequest.getQueryString()) // 쿼리 문자열 추가
                .build()
                .toUriString();

        Map<String, Object> fieldAndErrorMessages = errors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        String errorsToJsonString = new ObjectMapper().writeValueAsString(fieldAndErrorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                HttpStatus.valueOf(status.value()).name(),
                errorsToJsonString,
                url
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(
            ConstraintViolationException e, HttpServletRequest request) {
        Map<String, Object> bindingErrors = new HashMap<>();
        e.getConstraintViolations()
                .forEach(
                        constraintViolation -> {
                            List<String> propertyPath =
                                    List.of(
                                            constraintViolation
                                                    .getPropertyPath()
                                                    .toString()
                                                    .split("\\."));
                            String path =
                                    propertyPath.stream()
                                            .skip(propertyPath.size() - 1L)
                                            .findFirst()
                                            .orElse(null);
                            bindingErrors.put(path, constraintViolation.getMessage());
                        });

        ErrorReason errorReason =
                ErrorReason.builder()
                        .code("BAD_REQUEST")
                        .status(400)
                        .reason(bindingErrors.toString())
                        .build();
        ErrorResponse errorResponse =
                new ErrorResponse(errorReason, request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.valueOf(errorReason.getStatus()))
                .body(errorResponse);
    }

    @ExceptionHandler(BaseCodeException.class)
    public ResponseEntity<ErrorResponse> baseCodeExceptionHandler(
            BaseCodeException e, HttpServletRequest request) {
        BaseErrorCode code = e.getErrorCode();

        ErrorReason errorReason = code.getErrorReason();
        ErrorResponse errorResponse =
                new ErrorResponse(errorReason, request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.valueOf(errorReason.getStatus()))
                .body(errorResponse);
    }

    @ExceptionHandler(BaseDynamicException.class)
    public ResponseEntity<ErrorResponse> baseDynamicExceptionHandler(
            BaseDynamicException e, HttpServletRequest request) {
        ErrorResponse errorResponse =
                new ErrorResponse(
                        e.getStatus(),
                        e.getCode(),
                        e.getReason(),
                        request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request)
            throws IOException {
        // HttpServletRequest에서 직접 URL을 생성
        String url = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
                .query(request.getQueryString())  // 쿼리 문자열 포함
                .build()
                .toUriString();

        GlobalErrorCode internalServerError = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse =
                new ErrorResponse(
                        internalServerError.getStatus(),
                        internalServerError.getCode(),
                        internalServerError.getReason(),
                        url);

        return ResponseEntity.status(HttpStatus.valueOf(internalServerError.getStatus()))
                .body(errorResponse);
    }

//    @ExceptionHandler(BadCredentialsException.class)
//    protected ResponseEntity<ErrorResponse> handleBadCredentialsException(
//            BadCredentialsException e, HttpServletRequest request) throws IOException {
//        String url =
//                UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request))
//                        .build()
//                        .toUriString();
//
//        AdminErrorCode unauthorizedError = AdminErrorCode.ADMIN_BAD_CREDENTIALS_EXCEPTION;
//
//        ErrorResponse errorResponse =
//                new ErrorResponse(
//                        unauthorizedError.getStatus(),
//                        unauthorizedError.getCode(),
//                        unauthorizedError.getReason(),
//                        url);
//
//        return ResponseEntity.status(HttpStatus.valueOf(unauthorizedError.getStatus()))
//                .body(errorResponse);
//    }
}
