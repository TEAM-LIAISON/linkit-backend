package liaison.linkit.matching.dto.response.toPrivateMatching;

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
public class ToPrivateMatchingResponse {

    // 내 이력서에 온 매칭 요청 PK ID
    private final Long privateMatchingId;
    // 발신자 이름
    private final String senderName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 발신자가 누구인지
    private final SenderType senderType;
    // 매칭 요청 타입
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청이 왔는지
    private final boolean isReceivedTeamProfile;

    public static List<ToPrivateMatchingResponse> toPrivateMatchingResponse(final List<PrivateMatching> privateMatchingList) {
        return privateMatchingList.stream()
                .map(privateMatching -> new ToPrivateMatchingResponse(
                        privateMatching.getId(),
                        privateMatching.getMember().getMemberBasicInform().getMemberName(),
                        privateMatching.getRequestMessage(),
                        LocalDate.from(privateMatching.getCreatedAt()),
                        privateMatching.getSenderType(),
                        privateMatching.getMatchingType(),
                        false
                )).collect(Collectors.toList());
    }
}
