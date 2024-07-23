package liaison.linkit.matching.dto.response.requestPrivateMatching;

import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MyPrivateMatchingResponse {

    // 내 이력서에 보낸 매칭 요청 PK ID
    private final Long privateMatchingId;
    // 미니 프로필 이미지
    private final String miniProfileImg;
    // 수신자 이름
    private final String receiverName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 발신자가 누구인지
    private final SenderType senderType;
    // 매칭 요청 타입
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;

    // 내가 매칭 요청 보낸 것
    public static List<MyPrivateMatchingResponse> myPrivateMatchingResponseList(
            final List<PrivateMatching> privateMatchingList
    ) {
        return privateMatchingList.stream()
                .map(privateMatching -> new MyPrivateMatchingResponse(
                        privateMatching.getId(),
                        privateMatching.getProfile().getMiniProfile().getMiniProfileImg(),
                        privateMatching.getProfile().getMember().getMemberBasicInform().getMemberName(),
                        privateMatching.getRequestMessage(),
                        LocalDate.from(privateMatching.getCreatedAt()),
                        privateMatching.getSenderType(),
                        privateMatching.getMatchingType(),
                        false
                )).collect(Collectors.toList());
    }
}
