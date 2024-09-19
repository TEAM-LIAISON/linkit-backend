package liaison.linkit.common.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class CommonResponse<T> {

    @JsonProperty("isSuccess")
    private Boolean isSuccess;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    // 실제 응답 데이터
    // 제네릭 타입을 통해 다양한 유형의 응답 데이터를 담음
    @JsonProperty("result")
    private T result;

    // 정적 팩토리 메서드
    public static <T> CommonResponse<T> onSuccess(T data) {
        return new CommonResponse<>(true, "1000", "요청에 성공하였습니다.", data);
    }

    public static <T> CommonResponse<T> onFailure(String code, String message, T data) {
        return new CommonResponse<>(false, code, message, data);
    }
}
