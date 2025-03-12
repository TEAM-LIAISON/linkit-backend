package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.business.service.ProfileSkillService;
import liaison.linkit.profile.presentation.skill.ProfileSkillController;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO.AddProfileSkillItem;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillRequestDTO.AddProfileSkillRequest;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItems;
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

@WebMvcTest(ProfileSkillController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileSkillControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileSkillService profileSkillService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileSkillItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/skill")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateProfileSkill(
            final AddProfileSkillRequest profileSkillRequest) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/skill")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileSkillRequest)));
    }

    @DisplayName("회원이 나의 스킬을 전체 조회할 수 있다.")
    @Test
    void getProfileSkillItems() throws Exception {
        // given
        final ProfileSkillResponseDTO.ProfileSkillItem firstProfileSkillItem =
                new ProfileSkillItem(1L, "Figma", "상");

        final ProfileSkillResponseDTO.ProfileSkillItem secondProfileSkillItem =
                new ProfileSkillItem(2L, "Notion", "중상");

        final ProfileSkillResponseDTO.ProfileSkillItems profileSkillItems =
                new ProfileSkillItems(Arrays.asList(firstProfileSkillItem, secondProfileSkillItem));

        // when
        when(profileSkillService.getProfileSkillItems(anyLong())).thenReturn(profileSkillItems);

        final ResultActions resultActions = performGetProfileSkillItems();

        // then
        final MvcResult mvcResult =
                resultActions
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
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                subsectionWithPath("result.profileSkillItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 스킬 아이템 배열"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].profileSkillId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("스킬 ID"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].skillName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 이름"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].skillLevel")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 숙련도 선택"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileSkillItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileSkillItems>>() {});

        final CommonResponse<ProfileSkillItems> expected =
                CommonResponse.onSuccess(profileSkillItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 스킬 리스트를 수정할 수 있다.")
    @Test
    void updateProfileSkillItems() throws Exception {
        // given
        // request
        final ProfileSkillRequestDTO.AddProfileSkillItem firstProfileSkillItem =
                new AddProfileSkillItem("Figma", "상");

        final ProfileSkillRequestDTO.AddProfileSkillItem secondProfileSkillItem =
                new AddProfileSkillItem("Notion", "중");

        final ProfileSkillRequestDTO.AddProfileSkillRequest addProfileSkillRequest =
                AddProfileSkillRequest.builder()
                        .profileSkillItems(
                                Arrays.asList(firstProfileSkillItem, secondProfileSkillItem))
                        .build();

        // response
        final ProfileSkillResponseDTO.ProfileSkillItem firstProfileSkillItemRequest =
                new ProfileSkillItem(1L, "Figma", "상");

        final ProfileSkillResponseDTO.ProfileSkillItem secondProfileSkillItemRequest =
                new ProfileSkillItem(2L, "Notion", "중");

        final ProfileSkillResponseDTO.ProfileSkillItems profileSkillItems =
                ProfileSkillResponseDTO.ProfileSkillItems.builder()
                        .profileSkillItems(
                                Arrays.asList(
                                        firstProfileSkillItemRequest,
                                        secondProfileSkillItemRequest))
                        .build();

        // when
        when(profileSkillService.updateProfileSkillItems(anyLong(), any()))
                .thenReturn(profileSkillItems);

        final ResultActions resultActions = performUpdateProfileSkill(addProfileSkillRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                // 요청 필드 문서화
                                                fieldWithPath("profileSkillItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 스킬 항목 리스트"),
                                                fieldWithPath("profileSkillItems[].skillName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 이름"),
                                                fieldWithPath("profileSkillItems[].skillLevel")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 레벨")),
                                        responseFields(
                                                // 응답 필드 문서화
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                // 응답 결과 문서화
                                                fieldWithPath("result.profileSkillItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 스킬 목록"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].profileSkillId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 스킬 ID"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].skillName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 이름"),
                                                fieldWithPath(
                                                                "result.profileSkillItems[].skillLevel")
                                                        .type(JsonFieldType.STRING)
                                                        .description("스킬 레벨"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileSkillResponseDTO.ProfileSkillItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileSkillItems>>() {});

        final CommonResponse<ProfileSkillItems> expected =
                CommonResponse.onSuccess(profileSkillItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
