package liaison.linkit.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),
    NOT_FOUND_AWARDS_ID(1001, "요청한 ID에 해당하는 수상 정보가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_ROLE_ID(1002, "요청한 ID에 해당하는 직무/역할이 존재하지 않습니다."),
    NOT_FOUND_MEMBER_ID(1010, "요청한 ID에 해당하는 멤버가 존재하지 않습니다."),
    FAIL_TO_GENERATE_RANDOM_NICKNAME(1012, "랜덤한 닉네임을 생성하는데 실패하였습니다."),
    NOT_FOUND_MINI_PROFILE_ID(1014, "요청한 ID에 해당하는 미니 프로필이 존재하지 않습니다."),
    NOT_FOUND_MEMBER_INFORM_ID(1016, "요청한 ID에 해당하는 멤버 정보가 존재하지 않습니다."),
    NOT_FOUND_ANTECEDENTS_ID(1018, "요청한 ID에 해당하는 이력 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_ID(1020, "요청한 ID에 해당하는 프로필 정보가 존재하지 않습니다."),
    NOT_FOUND_EDUCATION_ID(1022, "요청한 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_BASIC_INFORM_ID(1025, "요청한 ID에 해당하는 멤버 기본 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_URL_ID(1028, "요청한 ID에 해당하는 첨부 URL 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_FILE_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_SKILL_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_HISTORY_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MINI_PROFILE_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_TEAM_BUILDING_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),

    INVALID_ANTECEDENTS_WITH_MEMBER(1017, "요청한 멤버와 ID에 해당하는 이력 정보가 존재하지 않습니다."),
    INVALID_MINI_PROFILE_WITH_MEMBER(1013, "요청한 멤버와 ID에 해당하는 미니 프로필이 존재하지 않습니다."),
    INVALID_AWARDS_WITH_MEMBER(1011, "요청한 멤버와 ID에 해당하는 수상 정보가 존재하지 않습니다."),
    INVALID_PROFILE_WITH_MEMBER(1019, "요청한 멤버와 ID에 해당하는 프로필 정보가 존재하지 않습니다."),
    INVALID_EDUCATION_WITH_MEMBER(1021, "요청한 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER(1023, "요청한 ID에 해당하는 희망 팀빌딩 정보가 존재하지 않습니다."),
    INVALID_MEMBER_BASIC_INFORM_WITH_MEMBER(1024, "요청한 ID에 해당하는 멤버 기본 정보가 존재하지 않습니다."),
    INVALID_PROFILE_SKILL_WITH_MEMBER(1026, "요청한 ID에 해당하는 보유 기술 정보가 존재하지 않습니다."),
    INVALID_ATTACH_URL_WITH_PROFILE(1027, "요청한 ID에 해당하는 첨부 URL 정보가 존재하지 않습니다."),
    INVALID_ATTACH_FILE_WITH_PROFILE(1028, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_HISTORY_WITH_TEAM_PROFILE(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_TEAM_MINI_PROFILE_WITH_MEMBER(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER(1030, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),


    // -----문단 구분-----
    EXCEED_IMAGE_CAPACITY(5001, "업로드 가능한 이미지 용량을 초과했습니다."),
    NULL_IMAGE(5002, "업로드한 이미지 파일이 NULL입니다."),
    EMPTY_IMAGE(5003, "업로드 가능한 이미지 개수를 초과했습니다."),
    EXCEED_IMAGE_SIZE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    FAIL_IMAGE_NAME_HASH(5102, "이미지 이름을 해싱하는 데 실패했습니다."),
    INVALID_IMAGE(5103, "올바르지 않은 이미지 파일입니다."),
    // -----문단 구분-----
    INVALID_USER_NAME(8001, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(8002, "비밀번호가 일치하지 않습니다."),
    NULL_ADMIN_AUTHORITY(8101, "잘못된 관리자 권한입니다."),
    DUPLICATED_ADMIN_USERNAME(8102, "중복된 사용자 이름입니다."),
    NOT_FOUND_ADMIN_ID(8103, "요청한 ID에 해당하는 관리자를 찾을 수 없습니다."),
    INVALID_CURRENT_PASSWORD(8104, "현재 사용중인 비밀번호가 일치하지 않습니다."),

    INVALID_ADMIN_AUTHORITY(8201, "해당 관리자 기능에 대한 접근 권한이 없습니다."),

    // -----문단 구분-----
    INVALID_AUTHORIZATION_CODE(9001, "유효하지 않은 인증 코드입니다."),
    NOT_SUPPORTED_OAUTH_SERVICE(9002, "해당 OAuth 서비스는 제공하지 않습니다."),
    FAIL_TO_CONVERT_URL_PARAMETER(9003, "Url Parameter 변환 중 오류가 발생했습니다."),
    INVALID_REFRESH_TOKEN(9101, "올바르지 않은 형식의 RefreshToken입니다."),
    INVALID_ACCESS_TOKEN(9102, "올바르지 않은 형식의 AccessToken입니다."),
    EXPIRED_PERIOD_REFRESH_TOKEN(9103, "기한이 만료된 RefreshToken입니다."),
    EXPIRED_PERIOD_ACCESS_TOKEN(9104, "기한이 만료된 AccessToken입니다."),
    FAIL_TO_VALIDATE_TOKEN(9105, "토큰 유효성 검사 중 오류가 발생했습니다."),
    NOT_FOUND_REFRESH_TOKEN(9106, "refresh-token에 해당하는 쿠키 정보가 없습니다."),
    INVALID_AUTHORITY(9201, "해당 요청에 대한 접근 권한이 없습니다."),
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요.");

    private final int code;
    private final String message;
}
