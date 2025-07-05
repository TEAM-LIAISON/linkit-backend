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
import liaison.linkit.profile.business.service.ProfileLinkService;
import liaison.linkit.profile.presentation.link.ProfileLinkController;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO.AddProfileLinkRequest;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItems;
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

@WebMvcTest(ProfileLinkController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileLinkControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileLinkService profileLinkService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileLinkItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/link")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateProfileLink(final AddProfileLinkRequest profileLinkRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/link")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileLinkRequest)));
    }

    @DisplayName("회원이 나의 링크을 전체 조회할 수 있다.")
    @Test
    void getProfileLinkItems() throws Exception {
        // given
        final ProfileLinkResponseDTO.ProfileLinkItem firstProfileLinkItem =
                new ProfileLinkItem(1L, "링크 이름", "링크 경로");

        final ProfileLinkResponseDTO.ProfileLinkItem secondProfileLinkItem =
                new ProfileLinkItem(2L, "링크 이름", "링크 경로");

        final ProfileLinkResponseDTO.ProfileLinkItems profileLinkItems =
                new ProfileLinkResponseDTO.ProfileLinkItems(
                        Arrays.asList(firstProfileLinkItem, secondProfileLinkItem));

        // when
        when(profileLinkService.getProfileLinkItems(anyLong())).thenReturn(profileLinkItems);

        final ResultActions resultActions = performGetProfileLinkItems();

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
                                                subsectionWithPath("result.profileLinkItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 링크 아이템 배열"),
                                                fieldWithPath(
                                                                "result.profileLinkItems[].profileLinkId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("링크 ID"),
                                                fieldWithPath("result.profileLinkItems[].linkName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 이름"),
                                                fieldWithPath("result.profileLinkItems[].linkPath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 경로"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLinkItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileLinkItems>>() {});

        final CommonResponse<ProfileLinkItems> expected =
                CommonResponse.onSuccess(profileLinkItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 링크 리스트를 수정할 수 있다.")
    @Test
    void updateProfileLinkItems() throws Exception {
        // given
        // request
        final ProfileLinkRequestDTO.AddProfileLinkItem firstProfileLinkItem =
                new ProfileLinkRequestDTO.AddProfileLinkItem("링크 이름", "링크 경로");

        final ProfileLinkRequestDTO.AddProfileLinkItem secondProfileLinkItem =
                new ProfileLinkRequestDTO.AddProfileLinkItem("링크 이름", "링크 경로");

        final ProfileLinkRequestDTO.AddProfileLinkRequest addProfileLinkRequest =
                AddProfileLinkRequest.builder()
                        .profileLinkItems(
                                Arrays.asList(firstProfileLinkItem, secondProfileLinkItem))
                        .build();

        // response
        final ProfileLinkResponseDTO.ProfileLinkItem firstProfileLinkItemRequest =
                new ProfileLinkItem(1L, "링크 이름", "링크 경로");

        final ProfileLinkResponseDTO.ProfileLinkItem secondProfileLinkItemRequest =
                new ProfileLinkItem(2L, "링크 이름", "링크 경로");

        final ProfileLinkResponseDTO.ProfileLinkItems profileLinkItems =
                ProfileLinkResponseDTO.ProfileLinkItems.builder()
                        .profileLinkItems(
                                Arrays.asList(
                                        firstProfileLinkItemRequest, secondProfileLinkItemRequest))
                        .build();

        // when
        when(profileLinkService.updateProfileLinkItems(anyLong(), any()))
                .thenReturn(profileLinkItems);

        final ResultActions resultActions = performUpdateProfileLink(addProfileLinkRequest);

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
                                                fieldWithPath("profileLinkItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 링크 항목 리스트"),
                                                fieldWithPath("profileLinkItems[].linkName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 이름"),
                                                fieldWithPath("profileLinkItems[].linkPath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 경로")),
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
                                                fieldWithPath("result.profileLinkItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 링크 목록"),
                                                fieldWithPath(
                                                                "result.profileLinkItems[].profileLinkId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 링크 ID"),
                                                fieldWithPath("result.profileLinkItems[].linkName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 이름"),
                                                fieldWithPath("result.profileLinkItems[].linkPath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("링크 경로"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLinkResponseDTO.ProfileLinkItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileLinkItems>>() {});

        final CommonResponse<ProfileLinkItems> expected =
                CommonResponse.onSuccess(profileLinkItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
