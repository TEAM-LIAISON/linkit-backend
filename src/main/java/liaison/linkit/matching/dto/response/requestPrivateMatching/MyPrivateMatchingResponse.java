package liaison.linkit.matching.dto.response.requestPrivateMatching;

import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.domain.type.MatchingType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MyPrivateMatchingResponse {
    // 수신자 이름
    private final String receiverName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 매칭 요청 타입
    private final MatchingType matchingType;

    public static List<MyPrivateMatchingResponse> myPrivateMatchingResponseList(
            final List<PrivateMatching> privateMatchingList
    ) {
        return privateMatchingList.stream()
                .map(privateMatching -> new MyPrivateMatchingResponse(
                        privateMatching.getProfile().getMember().getMemberBasicInform().getMemberName(),
                        privateMatching.getRequestMessage(),
                        LocalDate.from(privateMatching.getCreatedAt()),
                        privateMatching.getMatchingType()
                )).collect(Collectors.toList());
    }
}
