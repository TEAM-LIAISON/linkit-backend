package liaison.linkit.member.domain.type;

// 회원의 내 이력서 및 팀 소개서 작성 여부에 따른 타입 설정
public enum MemberProfileType {

    // 내 이력서 및 팀 소개서 모두 작성하지 않은 상태 -> 처음에 회원가입 진행했을 때 상태
    // 모두 작성하지 않은 경우 Error Code -> 사용자가 내 이력서 또는 팀 소개서를 등록하세요 알림을 띄워준다.
    EMPTY_PROFILE,

    // 내 이력서 작성 O / 팀 소개서 작성 X
    // 내 이력서에 있는 Completion 컬럼에 따른 Profile 테이블의 권한 조회
    MY_PROFILE_WRITER,

    // 내 이력서 작성 X / 팀 소개서 작성 O
    // 팀 소개서에 있는 Completion 컬럼에 따른 Team Profile 테이블의 권한 조회
    TEAM_PROFILE_WRITER,

    // 내 이력서 작성 O / 팀 소개서 작성 O
    // 내 이력서 권한을 우선 조회 -> 매칭 요청 권한이 허용된 경우에 -> 내 이력서 및 팀 소개서 모두 요청을 보낼 수 있다.
    // 내 이력서 권한이 허용되지 않았지만 팀 소개서 매칭 요청 %는 상위한 경우 -> 내 이력서에 대한 매칭 요청만 가능하도록 (팀 영입 매칭 요청)
    BOTH_PROFILE_WRITER;

    // 4가지 타입 관리를 통해 매칭 요청 권한 전처리를 거친다.


}
