package liaison.linkit.search.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.search.business.service.ProfileSearchService;
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

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileSearchService profileSearchService;

    private ResultActions performSearchProfiles(
            List<String> majorPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> profileStateName,
            int page,
            int size
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/search")
                        .param("majorPosition", majorPosition.toArray(new String[0]))
                        .param("skillName", skillName.toArray(new String[0]))
                        .param("cityName", cityName.toArray(new String[0]))
                        .param("profileStateName", profileStateName.toArray(new String[0]))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    @DisplayName("회원/비회원이 팀원을 검색할 수 있다. (필터링 가능)")
    @Test
    void searchProfiles() throws Exception {
        // given
        ProfileInformMenu profileInformMenu1 = ProfileInformMenu.builder()
                .profileCurrentStates(Arrays.asList(
                                ProfileCurrentStateItem.builder()
                                        .profileStateName("팀원 찾는 중")
                                        .build(),
                                ProfileCurrentStateItem.builder()
                                        .profileStateName("팀 찾는 중")
                                        .build()
                        )
                )
                .isProfileScrap(true)
                .profileScrapCount(100)
                .profileImagePath("프로필 이미지 경로 1")
                .memberName("회원 이름 1")
                .emailId("이메일 ID 1")
                .isProfilePublic(true)
                .majorPosition("포지션 대분류")
                .regionDetail(
                        RegionDetail.builder()
                                .cityName("활동지역 시/도")
                                .divisionName("활동지역 시/군/구")
                                .build()
                )
                .profileTeamInforms(
                        Arrays.asList(
                                ProfileTeamInform.builder()
                                        .teamName("소속 팀 이름 1")
                                        .teamLogoImagePath("소속 팀 로고 이미지 1")
                                        .build(),
                                ProfileTeamInform.builder()
                                        .teamName("소속 팀 이름 2")
                                        .teamLogoImagePath("소속 팀 로고 이미지 2")
                                        .build()
                        )
                )
                .build();

        ProfileInformMenu profileInformMenu2 = ProfileInformMenu.builder()
                .profileCurrentStates(Arrays.asList(
                                ProfileCurrentStateItem.builder()
                                        .profileStateName("팀원 찾는 중")
                                        .build(),
                                ProfileCurrentStateItem.builder()
                                        .profileStateName("팀 찾는 중")
                                        .build()
                        )
                )
                .isProfileScrap(false)
                .profileScrapCount(200)
                .profileImagePath("프로필 이미지 경로 2")
                .memberName("회원 이름 2")
                .emailId("이메일 ID 2")
                .isProfilePublic(true)
                .majorPosition("포지션 대분류")
                .regionDetail(
                        RegionDetail.builder()
                                .cityName("활동지역 시/도")
                                .divisionName("활동지역 시/군/구")
                                .build()
                )
                .profileTeamInforms(
                        Arrays.asList(
                                ProfileTeamInform.builder()
                                        .teamName("소속 팀 이름 1")
                                        .teamLogoImagePath("소속 팀 로고 이미지 1")
                                        .build(),
                                ProfileTeamInform.builder()
                                        .teamName("소속 팀 이름 2")
                                        .teamLogoImagePath("소속 팀 로고 이미지 2")
                                        .build()
                        )
                )
                .build();

        List<ProfileInformMenu> profiles = Arrays.asList(profileInformMenu1, profileInformMenu2);
        Page<ProfileInformMenu> profilePage = new PageImpl<>(profiles, PageRequest.of(0, 20), profiles.size());

        // when
        when(profileSearchService.searchProfilesInLogoutState(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(profilePage);

        final ResultActions resultActions = performSearchProfiles(
                Arrays.asList("개발자"),
                Arrays.asList("Java", "Spring"),
                Arrays.asList("서울특별시"),
                Arrays.asList("활동 중"),
                0,
                20
        );

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("majorPosition")
                                                .optional()
                                                .description("포지션 대분류 (선택적)"),
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
                                                .description("페이지 크기 (기본값: 20)")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.content")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 정보 메뉴 목록"),
                                        fieldWithPath("result.content[].profileCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 현재 상태 목록"),
                                        fieldWithPath("result.content[].profileCurrentStates[].profileStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 상태 이름"),
                                        fieldWithPath("result.content[].isProfileScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 스크랩 여부 (로그인, 로그아웃 상태 반영 & 스크랩 여부 반영)"),
                                        fieldWithPath("result.content[].profileScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 스크랩 전체 개수"),
                                        fieldWithPath("result.content[].profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 경로"),
                                        fieldWithPath("result.content[].memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("회원 이름"),
                                        fieldWithPath("result.content[].emailId")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일 ID"),
                                        fieldWithPath("result.content[].isProfilePublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 공개 여부"),
                                        fieldWithPath("result.content[].majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 대분류"),
                                        fieldWithPath("result.content[].profileTeamInforms")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 팀 정보 목록"),
                                        fieldWithPath("result.content[].profileTeamInforms[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("소속 팀 이름"),
                                        fieldWithPath("result.content[].profileTeamInforms[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("소속 팀 로고 이미지 경로"),
                                        fieldWithPath("result.content[].regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.content[].regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/도 이름"),
                                        fieldWithPath("result.content[].regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("시/군/구 이름"),
                                        fieldWithPath("result.pageable")
                                                .type(JsonFieldType.OBJECT)
                                                .description("페이지 정보"),
                                        fieldWithPath("result.pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 여부"),
                                        fieldWithPath("result.pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("비정렬 여부"),
                                        fieldWithPath("result.pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보 존재 여부"),
                                        fieldWithPath("result.pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("데이터 오프셋"),
                                        fieldWithPath("result.pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이징 여부"),
                                        fieldWithPath("result.pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이징 미적용 여부"),
                                        fieldWithPath("result.totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 페이지 수"),
                                        fieldWithPath("result.totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 요소 수"),
                                        fieldWithPath("result.last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("마지막 페이지 여부"),
                                        fieldWithPath("result.size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.sort")
                                                .type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("result.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 여부"),
                                        fieldWithPath("result.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("비정렬 여부"),
                                        fieldWithPath("result.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보 존재 여부"),
                                        fieldWithPath("result.first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("첫 페이지 여부"),
                                        fieldWithPath("result.numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 요소 수"),
                                        fieldWithPath("result.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요소 존재 여부")
                                )
                        )
                ).andReturn();
    }
}
