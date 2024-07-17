package liaison.linkit.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    FAIL_TO_GENERATE_MEMBER(1001, "회원을 생성하는데 실패하였습니다."),

    NOT_FOUND_MEMBER_ID(1002, "요청한 ID에 해당하는 멤버(회원)가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_ROLE_ID(1003, "요청한 ID에 해당하는 직무/역할이 존재하지 않습니다."),
    NOT_FOUND_AWARDS_ID(1004, "요청한 ID에 해당하는 수상 정보가 존재하지 않습니다."),


    NOT_FOUND_ANTECEDENTS_ID(1018, "요청한 ID에 해당하는 이력 정보가 존재하지 않습니다."),

    NOT_FOUND_EDUCATION_ID(1022, "요청한 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_BASIC_INFORM_ID(1025, "요청한 ID에 해당하는 멤버 기본 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_URL_ID(1028, "요청한 ID에 해당하는 첨부 URL 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_FILE_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_SKILL_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_HISTORY_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MINI_PROFILE_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_TEAM_BUILDING_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_PROFILE_ID(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),


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
    INVALID_TEAM_ATTACH_URL_WITH_PROFILE(1029, "요청한 ID에 해당하는 팀 첨부 URL 정보가 존재하지 않습니다."),
    INVALID_TEAM_ATTACH_FILE_WITH_PROFILE(1030, "요청한 ID에 해당하는 팀 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_HISTORY_WITH_TEAM_PROFILE(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_TEAM_MINI_PROFILE_WITH_MEMBER(1029, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER(1030, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),
    INVALID_PROFILE_REGION_WITH_MEMBER(1031, "요청한 ID에 해당하는 첨부 파일 정보가 존재하지 않습니다."),

    INVALID_TEAM_INTRODUCTION_WITH_TEAM_PROFILE(1032, "요청한 ID에 해당하는 팀원 소개 정보가 존재하지 않습니다."),
    INVALID_TEAM_MEMBER_ANNOUNCEMENT_WITH_PROFILE(1033, "요청한 ID에 해당하는 팀원 공고 정보가 존재하지 않습니다."),
    // -----문단 구분-----
    EXCEED_IMAGE_CAPACITY(5001, "업로드 가능한 이미지 용량을 초과했습니다."),
    NULL_IMAGE(5002, "업로드한 이미지 파일이 NULL입니다."),
    NULL_ATTACH_FILE(5003, "업로드한 첨부 파일이 NULL입니다."),
    EMPTY_IMAGE(5003, "업로드 가능한 이미지 개수를 초과했습니다."),
    EMPTY_ATTACH_FILE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    EMPTY_TEAM_ATTACH_FILE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    EXCEED_IMAGE_SIZE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    INVALID_FILE_URL(5005, "요청한 파일의 URL의 형식이 잘못되었습니다."),
    INVALID_FILE_PATH(5102, "첨부 파일을 저장할 경로가 올바르지 않습니다."),

    FAIL_IMAGE_NAME_HASH(5102, "이미지 이름을 해싱하는 데 실패했습니다."),
    FAIL_FILE_NAME_HASH(5103, "첨부 파일 이름을 해싱하는 데 실패했습니다"),
    INVALID_IMAGE(5103, "올바르지 않은 이미지 파일입니다."),
    INVALID_FILE(5104, "올바르지 않은 첨부 파일입니다."),
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
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요."),


    // 수정 완료 코드

    // 1. antecedents 2. attach (URL/FILE) 3. awards
    INVALID_TEAM_MINI_PROFILE(6004, "해당 팀 미니 프로필이 정보가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_BY_MEMBER_ID(1004, "사용자의 ID에 해당하는 회원 정보가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID(1004, "사용자의 ID에 해당하는 회원 기본 정보가 존재하지 않습니다."),

    NOT_FOUND_PROFILE_BY_MEMBER_ID(1004, "사용자의 ID에 해당하는 내 이력서 정보가 존재하지 않습니다."),

    NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 내 이력서용 미니 프로필 정보가 존재하지 않습니다."),

    NOT_FOUND_PROFILE_REGION_BY_MEMBER_ID(1006, "사용자의 ID에 해당하는 내 이력서용 미니 프로필 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_REGION_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 내 이력서용 미니 프로필 정보가 존재하지 않습니다."),

    NOT_FOUND_PROFILE_SKILL_BY_ID(1004, "해당 보유 기술 및 역할 ID에 해당하는 보유 기술 및 역할 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_SKILLS_BY_PROFILE_ID(1004, "사용자의 내 이력서 ID에 해당하는 보유 기술 및 역할 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_ANTECEDENTS_BY_ID(1007, "해당 이력 ID에 해당하는 이력 정보가 존재하지 않습니다."),
    NOT_FOUND_ANTECEDENTS_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 이력 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_EDUCATION_BY_ID(1007, "해당 학력 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_EDUCATIONS_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 학력 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_AWARDS_BY_ID(1007, "해당 학력 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_AWARDS_LIST_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 학력 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_ATTACH_URL_BY_ID(1007, "해당 학력 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_URLS_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 학력 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_ATTACH_FILE_BY_ID(1007, "해당 학력 ID에 해당하는 학력 정보가 존재하지 않습니다."),
    NOT_FOUND_ATTACH_FILES_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 학력 리스트 정보가 존재하지 않습니다."),

    NOT_FOUND_MINI_PROFILE_BY_MEMBER_ID(1006, "사용자의 ID에 해당하는 내 이력서용 미니 프로필 정보가 존재하지 않습니다."),
    NOT_FOUND_MINI_PROFILE_BY_PROFILE_ID(1007, "사용자의 내 이력서 ID에 해당하는 내 이력서용 미니 프로필 정보가 존재하지 않습니다."),
    COMMIT_REPAIR(1000, "커밋 확인용"),

    NOT_FOUND_UNIVERSITY_NAME(1009, "입력하신 대학 정보와 일치하는 대학명을 수 없습니다."),
    NOT_FOUND_DEGREE_NAME(1010, "입력하신 재학 여부를 판단할 수 없습니다."),
    NOT_FOUND_MAJOR_NAME(1011, "입력하신 전공 정보와 일치하는 전공명을 찾을 수 없습니다."),

    CANNOT_CREATE_PRIVATE_WISH_BECAUSE_OF_MAX_COUNT(8000, "내 이력서 찜하기 최대 개수에 도달했습니다. 더 이상 저장할 수 없어요."),
    CANNOT_CREATE_TEAM_WISH_BECAUSE_OF_MAX_COUNT(8001, "팀 소개서 찜하기 최대 개수에 도달했습니다. 더 이상 저장할 수 없어요."),

    // 팀 소개서 관련 항목
    NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID(1005, "사용자의 ID에 해당하는 팀 소개서 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE(1005, "사용자의 ID에 해당하는 팀 소개서 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_SKILL(1005, "사용자의 ID에 해당하는 팀 소개서 정보가 존재하지 않습니다."),
    NOT_FOUND_ACTIVITY_METHOD_BY_TEAM_PROFILE_ID(1009, "사용자의 팀 소개서 ID에 해당하는 활동 방식 정보가 존재하지 않습니다."),
    NOT_FOUND_ACTIVITY_REGION_BY_TEAM_PROFILE_ID(1010, "사용자의 팀 소개서 ID에 해당하는 활동 지역 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID(1007, "사용자의 팀 소개서 ID에 해당하는 팀 소개서 미니 프로필 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MEMBER_INTRODUCTION_ID(1008, "해당 팀원 소개 ID에 해당하는 팀원 소개 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID(1008, "해당 팀원 공고 ID에 해당하는 팀원 공고 정보가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_BY_ID(1008, "해당 팀원 공고 ID에 해당하는 팀원 공고 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_PROFILE_BY_ID(1008, "해당 ID에 해당하는 팀 소개서 정보가 존재하지 않습니다."),
    NOT_FOUND_MATCHING_BY_PROFILE_ID(1010, "해당 내 이력서 ID에 해당하는 매칭 정보가 존재하지 않습니다."),
    NOT_FOUND_MINI_PROFILE_BY_ID(1008, "해당 팀원 공고 ID에 해당하는 팀원 공고 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_ATTACH_URL_ID(1008, "해당 팀 첨부 URL ID에 해당하는 팀 첨부 URL 정보가 존재하지 않습니다."),
    NOT_FOUND_TEAM_MINI_PROFILE_BY_MEMBER_ID(5000, "팀 소개서 온보딩에서 팀 이름, 규모, 분야를 먼저 선택해주세요"),
    NOT_FOUND_TEAM_WISH_BY_ID(5001, "팀 소개서 온보딩에서 팀 이름, 규모, 분야를 먼저 선택해주세요");
    private final int code;
    private final String message;
}
