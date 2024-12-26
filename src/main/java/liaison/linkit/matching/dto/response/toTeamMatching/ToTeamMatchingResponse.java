package liaison.linkit.matching.dto.response.toTeamMatching;

import java.time.LocalDate;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ToTeamMatchingResponse {

    // 발신자의 miniProfileId / teamMiniProfileId
    private final Long profileId;

    // 팀 소개서에 온 매칭 요청 PK ID
    private final Long teamMatchingId;
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

//    public static List<ToTeamMatchingResponse> toTeamMatchingResponse(final List<TeamMatching> teamMatchingList) {
//        return teamMatchingList.stream()
//                .map(teamMatching -> {
//                    if (teamMatching.getSenderType() == SenderType.PRIVATE) {
//                        // 내 이력서로 나의 팀 소개서에 보낸 경우
//                        return new ToTeamMatchingResponse(
//                                teamMatching.getMember().getProfile().getId(),
//                                teamMatching.getId(),
//                                teamMatching.getMember().getProfile().getMiniProfile().getMiniProfileImg(),
//                                teamMatching.getMember().getMemberBasicInform().getMemberName(),
//                                teamMatching.getRequestMessage(),
//                                LocalDate.from(teamMatching.getCreatedAt()),
//                                teamMatching.getSenderType(),
//                                teamMatching.getReceiverType(),
//                                true,
//                                teamMatching.getIsReceiverCheck()
//                        );
//                    } else {
//                        // 팀 소개서로 내 팀 소개서에 보낸 경우
//                        return new ToTeamMatchingResponse(
//                                teamMatching.getMember().getTeamProfile().getId(),
//                                teamMatching.getId(),
//                                teamMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamLogoImageUrl(),
//                                teamMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamName(),
//                                teamMatching.getRequestMessage(),
//                                LocalDate.from(teamMatching.getCreatedAt()),
//                                teamMatching.getSenderType(),
//                                teamMatching.getReceiverType(),
//                                true,
//                                teamMatching.getIsReceiverCheck()
//                        );
//                    }
//                })
//                .collect(Collectors.toList());
//    }
}
