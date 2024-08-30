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

    // 발신자의 miniProfileId / teamMiniProfileId
    private final Long profileId;
    // 내 이력서에 온 매칭 요청 PK ID
    private final Long privateMatchingId;
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
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청이 왔는지
    private final boolean isReceivedTeamProfile;
    // 내가 열람한 매칭 요청인지 여부
    private final Boolean isReceiverCheck;

    public static List<ToPrivateMatchingResponse> toPrivateMatchingResponse(
            final List<PrivateMatching> privateMatchingList
    ) {
        return privateMatchingList.stream()
                .map(privateMatching -> {
                    // 내 이력서로 보낸 사람
                    // 나의 내 이력서로 매칭 요청을 받음
                    if (privateMatching.getSenderType() == SenderType.PRIVATE) {
                        return new ToPrivateMatchingResponse(
                                privateMatching.getMember().getProfile().getMiniProfile().getId(),
                                privateMatching.getId(),
                                privateMatching.getMember().getProfile().getMiniProfile().getMiniProfileImg(),
                                privateMatching.getMember().getMemberBasicInform().getMemberName(),
                                privateMatching.getRequestMessage(),
                                LocalDate.from(privateMatching.getCreatedAt()),
                                privateMatching.getSenderType(),    // PRIVATE
                                privateMatching.getMatchingType(),
                                false,
                                privateMatching.getIsReceiverCheck()
                        );
                    } else {
                        // 팀 소개서로 보낸 사람
                        // 나의 내 이력서로 매칭 요청을 받음
                        // SenderType -> TEAM
                        return new ToPrivateMatchingResponse(
                                privateMatching.getMember().getTeamProfile().getTeamMiniProfile().getId(),
                                privateMatching.getId(),
                                privateMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamLogoImageUrl(),
                                privateMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamName(),
                                privateMatching.getRequestMessage(),
                                LocalDate.from(privateMatching.getCreatedAt()),
                                privateMatching.getSenderType(),    // TEAM
                                privateMatching.getMatchingType(),
                                true,
                                privateMatching.getIsReceiverCheck()
                        );
                    }
                })
                .collect(Collectors.toList());
    }

}
