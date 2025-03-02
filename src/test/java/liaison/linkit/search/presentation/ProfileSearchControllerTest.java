package liaison.linkit.search.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.search.business.service.ProfileSearchService;
import liaison.linkit.search.presentation.dto.profile.ProfileSearchResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ProfileSearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileSearchService profileSearchService;

    private ResultActions performSearchProfiles(
            List<String> subPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> profileStateName,
            int page,
            int size)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/search")
                        .param("subPosition", subPosition.toArray(new String[0]))
                        .param("skillName", skillName.toArray(new String[0]))
                        .param("cityName", cityName.toArray(new String[0]))
                        .param("profileStateName", profileStateName.toArray(new String[0]))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));
    }

    @DisplayName("회원/비회원이 팀원을 검색할 수 있다. (필터링 가능)")
    @Test
    void searchProfiles() throws Exception {
        // given
        ProfileInformMenu profileInformMenu1 =
                ProfileInformMenu.builder()
                        .profileCurrentStates(
                                Arrays.asList(
                                        ProfileCurrentStateItem.builder()
                                                .profileStateName("팀원 찾는 중")
                                                .build(),
                                        ProfileCurrentStateItem.builder()
                                                .profileStateName("팀 찾는 중")
                                                .build()))
                        .isProfileScrap(true)
                        .profileScrapCount(100)
                        .profileImagePath("프로필 이미지 경로 1")
                        .memberName("회원 이름 1")
                        .emailId("이메일 ID 1")
                        .isProfilePublic(true)
                        .majorPosition("포지션 대분류")
                        .subPosition("포지션 소분류")
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("활동지역 시/도")
                                        .divisionName("활동지역 시/군/구")
                                        .build())
                        .profileTeamInforms(
                                Arrays.asList(
                                        ProfileTeamInform.builder()
                                                .teamName("소속 팀 이름 1")
                                                .teamCode("소속 팀 코드 1")
                                                .teamLogoImagePath("소속 팀 로고 이미지 1")
                                                .build(),
                                        ProfileTeamInform.builder()
                                                .teamName("소속 팀 이름 2")
                                                .teamCode("소속 팀 코드 2")
                                                .teamLogoImagePath("소속 팀 로고 이미지 2")
                                                .build()))
                        .build();

        ProfileInformMenu profileInformMenu2 =
                ProfileInformMenu.builder()
                        .profileCurrentStates(
                                Arrays.asList(
                                        ProfileCurrentStateItem.builder()
                                                .profileStateName("팀원 찾는 중")
                                                .build(),
                                        ProfileCurrentStateItem.builder()
                                                .profileStateName("팀 찾는 중")
                                                .build()))
                        .isProfileScrap(false)
                        .profileScrapCount(200)
                        .profileImagePath("프로필 이미지 경로 2")
                        .memberName("회원 이름 2")
                        .emailId("이메일 ID 2")
                        .isProfilePublic(true)
                        .majorPosition("포지션 대분류")
                        .subPosition("포지션 소분류")
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("활동지역 시/도")
                                        .divisionName("활동지역 시/군/구")
                                        .build())
                        .profileTeamInforms(
                                Arrays.asList(
                                        ProfileTeamInform.builder()
                                                .teamName("소속 팀 이름 1")
                                                .teamCode("소속 팀 코드 1")
                                                .teamLogoImagePath("소속 팀 로고 이미지 1")
                                                .build(),
                                        ProfileTeamInform.builder()
                                                .teamName("소속 팀 이름 2")
                                                .teamCode("소속 팀 코드 2")
                                                .teamLogoImagePath("소속 팀 로고 이미지 2")
                                                .build()))
                        .build();

        List<ProfileInformMenu> profiles = Arrays.asList(profileInformMenu1, profileInformMenu2);
        Page<ProfileInformMenu> profilePage =
                new PageImpl<>(profiles, PageRequest.of(0, 20), profiles.size());

        ProfileSearchResponseDTO profileSearchResponseDTO =
                ProfileSearchResponseDTO.builder()
                        .topCompletionProfiles(profiles)
                        .defaultProfiles(profilePage)
                        .build();

        // when
        when(profileSearchService.searchProfiles(
                        any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(profileSearchResponseDTO);

        final ResultActions resultActions =
                performSearchProfiles(
                        Arrays.asList("개발자"),
                        Arrays.asList("Java", "Spring"),
                        Arrays.asList("서울특별시"),
                        Arrays.asList("활동 중"),
                        0,
                        20);

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        queryParameters(
                                                parameterWithName("subPosition")
                                                        .optional()
                                                        .description("포지션 소분류 (선택적)"),
                                                parameterWithName("skillName")
                                                        .optional()
                                                        .description("스킬 이름 (선택적)"),
                                                parameterWithName("cityName")
                                                        .optional()
                                                        .description("시/도 이름 (선택적)"),
                                                parameterWithName("profileStateName")
                                                        .optional()
                                                        .description("프로필 상태 이름 (선택적)"),
                                                parameterWithName("page")
                                                        .optional()
                                                        .description("페이지 번호 (기본값: 0)"),
                                                parameterWithName("size")
                                                        .optional()
                                                        .description("페이지 크기 (기본값: 20)")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부"),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지"),

                                                // ✅ 상단: 프로필 완성도가 높은 팀원 6명
                                                fieldWithPath("result.topCompletionProfiles")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 완성도가 높은 팀원 목록 (최대 6명)"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].isProfilePublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 공개 여부"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileCurrentStates[].profileStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 상태 이름"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].isProfileScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 스크랩 개수"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("이메일 ID"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 소분류"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileTeamInforms")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("소속 팀 정보 목록"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileTeamInforms[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 이름"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileTeamInforms[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 코드"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].profileTeamInforms[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 로고 이미지 경로"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.topCompletionProfiles[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"),

                                                // ✅ 하단: 나머지 팀원 리스트 (페이지네이션)
                                                fieldWithPath("result.defaultProfiles")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("나머지 팀원 목록 (페이지네이션)"),
                                                fieldWithPath("result.defaultProfiles.content")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀원 목록"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].isProfilePublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 공개 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileCurrentStates[].profileStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 상태 이름"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].isProfileScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 스크랩 개수"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("이메일 ID"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 소분류"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileTeamInforms")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("소속 팀 정보 목록"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileTeamInforms[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 이름"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileTeamInforms[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 코드"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].profileTeamInforms[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 로고 이미지 경로"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.content[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"),

                                                // ✅ 페이지네이션 관련 필드 추가 (📢 여기에서 오류가 발생했었음)
                                                fieldWithPath("result.defaultProfiles.pageable")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("페이지네이션 정보"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.pageNumber")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지 번호"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.pageSize")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("페이지 크기"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.offset")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("오프셋"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.paged")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("페이징 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.unpaged")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("페이징 미적용 여부"),

                                                // ✅ `sort`가 `defaultProfiles` 바로 아래에 존재하는 경우 (📢 기존
                                                // pageable.sort가 아닌 구조)
                                                fieldWithPath("result.defaultProfiles.sort")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("정렬 정보"),
                                                fieldWithPath("result.defaultProfiles.sort.sorted")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("정렬 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.sort.unsorted")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("비정렬 여부"),
                                                fieldWithPath("result.defaultProfiles.sort.empty")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("정렬 정보 존재 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.sort.sorted")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("정렬 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.sort.unsorted")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("비정렬 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.pageable.sort.empty")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("정렬 정보 없음 여부"),

                                                // ✅ 전체 페이지네이션 정보 추가 (📢 기존 result.last ->
                                                // result.defaultProfiles.last)
                                                fieldWithPath("result.defaultProfiles.last")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("마지막 페이지 여부"),
                                                fieldWithPath("result.defaultProfiles.totalPages")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("총 페이지 수"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.totalElements")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("총 요소 수"),
                                                fieldWithPath("result.defaultProfiles.size")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("페이지 크기"),
                                                fieldWithPath("result.defaultProfiles.number")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지 번호"),
                                                fieldWithPath("result.defaultProfiles.first")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("첫 페이지 여부"),
                                                fieldWithPath(
                                                                "result.defaultProfiles.numberOfElements")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지의 요소 수"),
                                                fieldWithPath("result.defaultProfiles.empty")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("페이지가 비어있는지 여부"))))
                        .andReturn();
    }
}
