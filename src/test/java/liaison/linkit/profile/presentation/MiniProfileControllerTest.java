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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.miniProfile.MiniProfileController;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO.UpdateMiniProfileRequest;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItems;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfilePositionItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.UpdateMiniProfileResponse;
import liaison.linkit.profile.business.service.MiniProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MiniProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class MiniProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MiniProfileService miniProfileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetMiniProfileDetail() throws Exception {
        return mockMvc.perform(
                get("/api/v1/miniProfile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 나의 미니 프로필을 조회할 수 있다.")
    @Test
    void getMiniProfileDetail() throws Exception {
        // given

        final ProfilePositionItem profilePositionItem
                = new ProfilePositionItem("개발", "백엔드 개발자");

        final ProfileCurrentStateItem firstProfileCurrentStateItem
                = new ProfileCurrentStateItem("팀원 찾는 중");

        final ProfileCurrentStateItem secondProfileCurrentStateItem
                = new ProfileCurrentStateItem("팀 찾는 중");

        final ProfileCurrentStateItems profileCurrentStateItems
                = new ProfileCurrentStateItems(Arrays.asList(firstProfileCurrentStateItem, secondProfileCurrentStateItem));

        final MiniProfileResponseDTO.MiniProfileDetailResponse miniProfileDetailResponse
                = new MiniProfileDetailResponse(1L, "https://image.linkit.im/profile.png", "권동민", profilePositionItem, "서울특별시", "동작구", profileCurrentStateItems, true);

        // when
        when(miniProfileService.getMiniProfileDetail(anyLong())).thenReturn(miniProfileDetailResponse);

        final ResultActions resultActions = performGetMiniProfileDetail();

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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 객체"),
                                        fieldWithPath("result.profileId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 ID"),
                                        fieldWithPath("result.profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 경로"),
                                        fieldWithPath("result.memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("회원 이름"),
                                        fieldWithPath("result.profilePositionItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 포지션 정보"),
                                        fieldWithPath("result.profilePositionItem.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 대분류"),
                                        fieldWithPath("result.profilePositionItem.subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 소분류"),
                                        fieldWithPath("result.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 정보 시/도"),
                                        fieldWithPath("result.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 정보 시/군/구"),
                                        fieldWithPath("result.profileCurrentStateItems")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 현재 상태 정보"),
                                        fieldWithPath("result.profileCurrentStateItems.profileCurrentStates[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 현재 상태 목록"),
                                        fieldWithPath("result.profileCurrentStateItems.profileCurrentStates[].profileStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 상태 이름"),
                                        fieldWithPath("result.isProfilePublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 공개 여부")
                                )
                        )
                ).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MiniProfileDetailResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<MiniProfileDetailResponse>>() {
                }
        );

        final CommonResponse<MiniProfileDetailResponse> expected = CommonResponse.onSuccess(miniProfileDetailResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 미니 프로필을 수정할 수 있다.")
    @Test
    void updateMiniProfile() throws Exception {
        // given
        final UpdateMiniProfileResponse updateMiniProfileResponse
                = new UpdateMiniProfileResponse(1L, LocalDateTime.now(), "대분류 포지션", "소분류 포지션", "활동지역 시/도", "활동지역 시/군/구", Arrays.asList("팀 찾는 중", "아이디어 찾는 중"), true);

        final MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        final UpdateMiniProfileRequest updateMiniProfileRequest
                = new UpdateMiniProfileRequest("대분류 포지션", "소분류 포지션", "시/도", "시/군/구", Arrays.asList("팀 찾는 중", "팀원 찾는 중"), true);

        final MockMultipartFile createRequest = new MockMultipartFile(
                "updateMiniProfileRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(updateMiniProfileRequest).getBytes(StandardCharsets.UTF_8)
        );

        // when
        when(miniProfileService.updateMiniProfile(anyLong(), any(), any())).thenReturn(updateMiniProfileResponse);

        final ResultActions resultActions = mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/miniProfile")
                        .file(profileImage)
                        .file(createRequest)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8")
                        .header(HttpHeaders.AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        requestParts(
                                partWithName("profileImage").description("프로필 본문 이미지"),
                                partWithName("updateMiniProfileRequest").description("미니 프로필 수정 정보 객체")
                        ),
                        responseFields(fieldWithPath("isSuccess")
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
                                fieldWithPath("result.profileId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 ID"),
                                fieldWithPath("result.modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("미니 프로필 수정 시간"),
                                fieldWithPath("result.majorPosition")
                                        .type(JsonFieldType.STRING)
                                        .description("대분류 포지션"),
                                fieldWithPath("result.subPosition")
                                        .type(JsonFieldType.STRING)
                                        .description("소분류 포지션"),
                                fieldWithPath("result.cityName")
                                        .type(JsonFieldType.STRING)
                                        .description("활동지역 시/도"),
                                fieldWithPath("result.divisionName")
                                        .type(JsonFieldType.STRING)
                                        .description("활동지역 시/군/구"),
                                fieldWithPath("result.profileStateNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("현재 상태 배열"),
                                fieldWithPath("result.isProfilePublic")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("프로필 공개 여부")
                        )
                )).andReturn();
    }
}
