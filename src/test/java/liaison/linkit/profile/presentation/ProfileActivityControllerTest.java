package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.activity.ProfileActivityController;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.AddProfileActivityRequest;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItems;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityResponse;
import liaison.linkit.profile.service.ProfileActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileActivityController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileActivityControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileActivityService profileActivityService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileActivityItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/activity")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performAddProfileActivity(final AddProfileActivityRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/activity")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    @DisplayName("회원이 나의 이력을 전체 조회할 수 있다.")
    @Test
    void getProfileActivityItems() throws Exception {
        // given
        final ProfileActivityResponseDTO.ProfileActivityItem firstProfileActivityItem
                = new ProfileActivityItem(1L, "리에종", "PO", "2022.06", "2026.06");

        final ProfileActivityResponseDTO.ProfileActivityItem secondProfileActivityItem
                = new ProfileActivityItem(2L, "리에종", "디자이너", "2024.10", "2024.12");

        final ProfileActivityResponseDTO.ProfileActivityItems profileActivityItems
                = new ProfileActivityItems(Arrays.asList(firstProfileActivityItem, secondProfileActivityItem));

        // when
        when(profileActivityService.getProfileActivityItems(anyLong())).thenReturn(profileActivityItems);

        final ResultActions resultActions = performGetProfileActivityItems();

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
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
                                        subsectionWithPath("result.profileActivityItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 이력 아이템 배열"),
                                        fieldWithPath("result.profileActivityItems[].profileActivityId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 이력 ID"),
                                        fieldWithPath("result.profileActivityItems[].activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("result.profileActivityItems[].activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("result.profileActivityItems[].activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("result.profileActivityItems[].activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileActivityItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileActivityItems>>() {
                }
        );

        final CommonResponse<ProfileActivityItems> expected = CommonResponse.onSuccess(profileActivityItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력을 생성할 수 있다.")
    @Test
    void addProfileActivity() throws Exception {
        // given

        final ProfileActivityRequestDTO.AddProfileActivityRequest addProfileActivityRequest
                = new AddProfileActivityRequest("리에종", "PO", "2022.06", "2026.06", false, "이력 설명");

        final ProfileActivityResponseDTO.ProfileActivityResponse profileActivityResponse
                = new ProfileActivityResponse("리에종", "PO", "2022.06", "2026.06", false, "이력 설명");

        // when
        when(profileActivityService.addProfileActivity(anyLong(), any())).thenReturn(profileActivityResponse);

        final ResultActions resultActions = performAddProfileActivity(addProfileActivityRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("isActivityInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 진행 여부"),
                                        fieldWithPath("activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명")
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
                                        fieldWithPath("result.activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("result.activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("result.activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("result.activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("result.isActivityInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 진행 여부"),
                                        fieldWithPath("result.activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileActivityResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileActivityResponse>>() {
                }
        );

        final CommonResponse<ProfileActivityResponse> expected = CommonResponse.onSuccess(profileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
