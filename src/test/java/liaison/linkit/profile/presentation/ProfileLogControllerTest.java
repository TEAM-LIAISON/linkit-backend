package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static liaison.linkit.global.type.ProfileLogType.GENERAL_LOG;
import static liaison.linkit.global.type.ProfileLogType.REPRESENTATIVE_LOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.log.ProfileLogController;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import liaison.linkit.profile.service.ProfileLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileLogController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileLogControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileLogService profileLogService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileLogItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/log")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 나의 로그를 전체 조회할 수 있다.")
    @Test
    void getProfileLogItems() throws Exception {
        // given
        final ProfileLogResponseDTO.ProfileLogItem firstProfileLogItem
                = new ProfileLogItem(1L, true, REPRESENTATIVE_LOG, LocalDateTime.now(), "로그 제목", "로그 내용");

        final ProfileLogResponseDTO.ProfileLogItem secondProfileLogItem
                = new ProfileLogItem(2L, true, GENERAL_LOG, LocalDateTime.now(), "로그 제목", "로그 내용");

        final ProfileLogResponseDTO.ProfileLogItems profileLogItems
                = new ProfileLogItems(Arrays.asList(firstProfileLogItem, secondProfileLogItem));

        // when
        when(profileLogService.getProfileLogItems(anyLong())).thenReturn(profileLogItems);

        final ResultActions resultActions = performGetProfileLogItems();

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
                                        subsectionWithPath("result.profileLogItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 로그 아이템 배열"),
                                        fieldWithPath("result.profileLogItems[].profileLogId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 로그 ID"),
                                        fieldWithPath("result.profileLogItems[].isLogPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("로그 공개 여부"),
                                        fieldWithPath("result.profileLogItems[].profileLogType")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 유형 (대표글 여부)"),
                                        fieldWithPath("result.profileLogItems[].modifiedAt")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 수정 시간"),
                                        fieldWithPath("result.profileLogItems[].logTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 제목"),
                                        fieldWithPath("result.profileLogItems[].logContent")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 내용")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.ProfileLogItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileLogItems>>() {
                }
        );

        final CommonResponse<ProfileLogItems> expected = CommonResponse.onSuccess(profileLogItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
