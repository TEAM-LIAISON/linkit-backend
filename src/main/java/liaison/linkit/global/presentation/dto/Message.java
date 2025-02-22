package liaison.linkit.global.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private StatusEnum status; // 응답 상태 (OK, ERROR, NOT_FOUND 등)
    private String message; // 응답 메시지
    private Object data; // 응답 데이터
}
