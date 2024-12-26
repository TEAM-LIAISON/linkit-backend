package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class ReceivedMatchingResponse {

    // 발신자의 miniProfileId / teamMiniProfileId
    private final Long profileId;

    // 내 이력서 또는 팀 소개서에 매칭 요청 온 PK ID
    private final Long receivedMatchingId;
    // 프로필 이미지 src
    private final String miniProfileImg;
    // 발신자 이름
    private final String senderName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 발신자가 누구인지
    private final SenderType senderType;
    // 매칭 요청 타입
    private final ReceiverType receiverType;
    // 어떤 이력/소개서에 매칭 요청이 왔는지
    private final boolean isReceivedTeamProfile;

    // 내가 열람한 매칭 요청인지 여부
    private final Boolean isReceiverCheck;

    public static List<ReceivedMatchingResponse> toReceivedMatchingResponse(
            final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList,
            final List<ToTeamMatchingResponse> toTeamMatchingResponseList
    ) {
        // 스트림 API를 사용하여 두 리스트의 요소를 하나의 스트림으로 결합
        return Stream.concat(
                toPrivateMatchingResponseList.stream().map(pmr -> new ReceivedMatchingResponse(
                        pmr.getProfileId(),
                        pmr.getPrivateMatchingId(),
                        pmr.getMiniProfileImg(),
                        pmr.getSenderName(),
                        pmr.getRequestMessage(),
                        pmr.getRequestOccurTime(),
                        pmr.getSenderType(),
                        pmr.getReceiverType(),
                        pmr.isReceivedTeamProfile(),
                        pmr.getIsReceiverCheck())),
                toTeamMatchingResponseList.stream().map(tmr -> new ReceivedMatchingResponse(
                        tmr.getProfileId(),
                        tmr.getTeamMatchingId(),
                        tmr.getMiniProfileImg(),
                        tmr.getSenderName(),
                        tmr.getRequestMessage(),
                        tmr.getRequestOccurTime(),
                        tmr.getSenderType(),
                        tmr.getReceiverType(),
                        tmr.isReceivedTeamProfile(),
                        tmr.getIsReceiverCheck()))
        ).collect(Collectors.toList());
    }
}
