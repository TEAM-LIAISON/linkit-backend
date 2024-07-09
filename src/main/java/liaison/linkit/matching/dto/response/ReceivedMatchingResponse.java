package liaison.linkit.matching.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ReceivedMatchingResponse {
    // 발신자 이름
    private final String senderName;

    // 매칭 요청 메시지
    private final String requestMessage;

    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
}
