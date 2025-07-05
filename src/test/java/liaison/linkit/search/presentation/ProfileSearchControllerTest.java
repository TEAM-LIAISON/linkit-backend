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
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.search.business.service.ProfileSearchService;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.ProfileListResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

    // 테스트 헬퍼 메서드도 업데이트 필요
    private ResultActions performSearchProfiles(
            List<String> subPositions,
            List<String> cityNames,
            List<String> profileStateNames,
            CursorRequest cursorRequest)
            throws Exception {

        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/profile/search");

        // 커서 페이징 요청이 있을 경우
        if (cursorRequest != null) {
            // size 설정
            requestBuilder.param("size", String.valueOf(cursorRequest.size()));

            // cursor가 존재한다면 cursor 파라미터 추가
            if (cursorRequest.cursor() != null) {
                requestBuilder.param("cursor", cursorRequest.cursor());
            }
        }

        // 파라미터가 비어있지 않다면 해당 파라미터를 추가
        if (subPositions != null && !subPositions.isEmpty()) {
            subPositions.forEach(subPosition -> requestBuilder.param("subPosition", subPosition));
        }
        if (cityNames != null && !cityNames.isEmpty()) {
            cityNames.forEach(cityName -> requestBuilder.param("cityName", cityName));
        }
        if (profileStateNames != null && !profileStateNames.isEmpty()) {
            profileStateNames.forEach(
                    profileStateName -> requestBuilder.param("profileStateName", profileStateName));
        }

        return mockMvc.perform(requestBuilder);
    }

    private ResultActions performFeaturedProfiles() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/profile/search/featured");

        return mockMvc.perform(requestBuilder);
    }

    @DisplayName("회원/비회원이 팀원을 검색할 수 있다. (필터링 가능)")
    @Test
    void searchProfiles() throws Exception {
        // given
        CursorRequest cursorRequest = new CursorRequest("emailId", 20);

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

        // 커서 기반 페이지네이션으로 변경
        CursorResponse<ProfileResponseDTO.ProfileInformMenu> profileCursorResponse =
                CursorResponse.<ProfileResponseDTO.ProfileInformMenu>builder()
                        .content(profiles)
                        .nextCursor("nextProfileId") // 다음 커서 값 설정
                        .hasNext(true) // 다음 페이지가 있음
                        .build();

        CursorResponse<ProfileResponseDTO.ProfileInformMenu> cursorResponse =
                CursorResponse.of(profiles, "nextProfileId"); // CursorResponse로 변경

        when(profileSearchService.searchProfilesWithCursor(any(), any(), any(CursorRequest.class)))
                .thenReturn(cursorResponse);

        // when
        final ResultActions resultActions =
                performSearchProfiles(
                        Arrays.asList("백엔드 개발자", "프론트엔드 개발자"),
                        Arrays.asList("서울특별시", "부산광역시"),
                        Arrays.asList("팀원 찾는 중", "투자 유치 중"),
                        cursorRequest // cursor 값 사용
                        ); // size는 그대로 유지

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
                                                parameterWithName("cityName")
                                                        .optional()
                                                        .description("시/도 이름 (선택적)"),
                                                parameterWithName("profileStateName")
                                                        .optional()
                                                        .description("프로필 상태 이름 (선택적)"),
                                                parameterWithName("cursor")
                                                        .optional()
                                                        .description("마지막으로 조회한 팀의 ID (선택적)"),
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

                                                // ✅ 하단: 나머지 팀원 리스트 (페이지네이션)
                                                fieldWithPath("result.content")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀원 목록"),
                                                fieldWithPath("result.content[].isProfilePublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 공개 여부"),
                                                fieldWithPath(
                                                                "result.content[].profileCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.content[].profileCurrentStates[].profileStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 상태 이름"),
                                                fieldWithPath("result.content[].isProfileScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("프로필 스크랩 여부"),
                                                fieldWithPath("result.content[].profileScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 스크랩 개수"),
                                                fieldWithPath("result.content[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("result.content[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath("result.content[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("이메일 ID"),
                                                fieldWithPath("result.content[].majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath("result.content[].subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 소분류"),
                                                fieldWithPath("result.content[].profileTeamInforms")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("소속 팀 정보 목록"),
                                                fieldWithPath(
                                                                "result.content[].profileTeamInforms[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 이름"),
                                                fieldWithPath(
                                                                "result.content[].profileTeamInforms[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 코드"),
                                                fieldWithPath(
                                                                "result.content[].profileTeamInforms[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("소속 팀 로고 이미지 경로"),
                                                fieldWithPath("result.content[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"),

                                                // ✅ 커서 기반 페이지네이션 정보
                                                fieldWithPath("result.nextCursor")
                                                        .type(JsonFieldType.STRING)
                                                        .description("다음 페이지 조회를 위한 커서 값 (팀 코드)"),
                                                fieldWithPath("result.hasNext")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("다음 페이지 존재 여부"))))
                        .andReturn();
    }

    @DisplayName("회원/비회원이 정적 팀원을 조회할 수 있다. (필터링 불가)")
    @Test
    void getFeaturedProfiles() throws Exception {
        // given
        ProfileListResponseDTO profileListResponseDTO =
                ProfileListResponseDTO.builder()
                        .topCompletionProfiles(
                                Arrays.asList(
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
                                                                        .teamLogoImagePath(
                                                                                "소속 팀 로고 이미지 1")
                                                                        .build(),
                                                                ProfileTeamInform.builder()
                                                                        .teamName("소속 팀 이름 2")
                                                                        .teamCode("소속 팀 코드 2")
                                                                        .teamLogoImagePath(
                                                                                "소속 팀 로고 이미지 2")
                                                                        .build()))
                                                .build(),
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
                                                                        .teamLogoImagePath(
                                                                                "소속 팀 로고 이미지 1")
                                                                        .build(),
                                                                ProfileTeamInform.builder()
                                                                        .teamName("소속 팀 이름 2")
                                                                        .teamCode("소속 팀 코드 2")
                                                                        .teamLogoImagePath(
                                                                                "소속 팀 로고 이미지 2")
                                                                        .build()))
                                                .build()))
                        .build();

        when(profileSearchService.getFeaturedProfiles(any())).thenReturn(profileListResponseDTO);

        // when
        final ResultActions resultActions = performFeaturedProfiles();

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
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

                                                // ✅ 상단: 창업을 위한 팀원을 찾고 있어요 4팀
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
                                                        .description("지역 시/군/구 이름"))))
                        .andReturn();
    }
}
