package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberTokensAndOnBoardingStepInform {
    private final String accessToken;
    private final String refreshToken;

    private final String email;

    // 일단 기본 정보 기입 여부 전달 필요
    private final boolean isMemberBasicInform;

    // 내 이력서 또는 팀 소개서 하나라도 완료되었는지 여부
    private final boolean isProfile;
}
