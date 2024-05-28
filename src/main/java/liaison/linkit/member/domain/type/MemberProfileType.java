package liaison.linkit.member.domain.type;

// 회원의 내 이력서 및 팀 소개서 작성 여부에 따른 타입 설정
public enum MemberProfileType {

    // 내 이력서 및 팀 소개서 모두 작성하지 않은 상태 -> 처음에 회원가입 진행했을 때 상태
    EMPTY_PROFILE,

    // 내 이력서 작성 O / 팀 소개서 작성 X
    MY_PROFILE_WRITER,

    // 내 이력서 작성 X / 팀 소개서 작성 O
    TEAM_PROFILE_WRITER,

    // 내 이력서 작성 O / 팀 소개서 작성 O
    BOTH_PROFILE_WRITER;

}
