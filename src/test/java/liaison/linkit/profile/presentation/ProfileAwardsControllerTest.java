package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.awards.ProfileAwardsController;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.AddProfileAwardsRequest;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.AddProfileAwardsResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsCertificationResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsDetail;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItems;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.RemoveProfileAwardsResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.UpdateProfileAwardsResponse;
import liaison.linkit.profile.service.ProfileAwardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileAwardsController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class ProfileAwardsControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileAwardsService profileAwardsService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileAwardsItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/awards")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetProfileAwardsDetail(final Long profileAwardsId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/awards/{profileAwardsId}", profileAwardsId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performAddProfileAwards(final AddProfileAwardsRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/awards")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateProfileAwards(final Long profileAwardsId, final UpdateProfileAwardsRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/awards/{profileAwardsId}", profileAwardsId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveProfileAwards(final Long profileAwardsId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/awards/{profileAwardsId}", profileAwardsId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performRemoveAwardsCertification(final Long profileAwardsId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/awards/certification/{profileAwardsId}", profileAwardsId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 나의 수상을 전체 조회할 수 있다.")
    @Test
    void getProfileAwardsItems() throws Exception {
        // given
        final ProfileAwardsResponseDTO.ProfileAwardsItem firstProfileAwardsItem
                = new ProfileAwardsItem(1L, "수상 이름 1", "훈격 1", "수상 날짜 1", true);

        final ProfileAwardsResponseDTO.ProfileAwardsItem secondProfileAwardsItem
                = new ProfileAwardsItem(2L, "수상 이름 2", "훈격 2", "수상 날짜 2", true);

        final ProfileAwardsResponseDTO.ProfileAwardsItems profileAwardsItems
                = new ProfileAwardsItems(Arrays.asList(firstProfileAwardsItem, secondProfileAwardsItem));

        // when
        when(profileAwardsService.getProfileAwardsItems(anyLong())).thenReturn(profileAwardsItems);

        final ResultActions resultActions = performGetProfileAwardsItems();

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
                                        subsectionWithPath("result.profileAwardsItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 수상 아이템 배열"),
                                        fieldWithPath("result.profileAwardsItems[].profileAwardsId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 수상 ID"),
                                        fieldWithPath("result.profileAwardsItems[].awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 이름"),
                                        fieldWithPath("result.profileAwardsItems[].awardsRanking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 훈격"),
                                        fieldWithPath("result.profileAwardsItems[].awardsDate")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 시기"),
                                        fieldWithPath("result.profileAwardsItems[].isAwardsVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("수상 증명서 인증 완료 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileAwardsItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileAwardsItems>>() {
                }
        );

        final CommonResponse<ProfileAwardsItems> expected = CommonResponse.onSuccess(profileAwardsItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상을 상세 조회할 수 있다.")
    @Test
    void getProfileAwardsDetail() throws Exception {
        // given
        final ProfileAwardsResponseDTO.ProfileAwardsDetail profileAwardsDetail
                = new ProfileAwardsDetail(1L, "수상 이름", "수상 훈격", "수상 시기", "수상 주최/주관", "수상 설명", true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        // when
        when(profileAwardsService.getProfileAwardsDetail(anyLong(), anyLong())).thenReturn(profileAwardsDetail);

        final ResultActions resultActions = performGetProfileAwardsDetail(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("profileAwardsId")
                                                .description("프로필 수상 ID")
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
                                        fieldWithPath("result.profileAwardsId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 수상 ID"),
                                        fieldWithPath("result.awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 이름"),
                                        fieldWithPath("result.awardsRanking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 훈격"),
                                        fieldWithPath("result.awardsDate")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 시기"),

                                        fieldWithPath("result.awardsOrganizer")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 주최 및 주관"),
                                        fieldWithPath("result.awardsDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 설명"),

                                        fieldWithPath("result.isAwardsCertified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("수상 증명서 존재 여부"),
                                        fieldWithPath("result.isAwardsVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("수상 증명서 인증 여부"),
                                        fieldWithPath("result.awardsCertificationAttachFileName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 증명서 파일 이름"),
                                        fieldWithPath("result.awardsCertificationAttachFilePath")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 증명서 파일 경로")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileAwardsDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileAwardsDetail>>() {
                }
        );

        final CommonResponse<ProfileAwardsDetail> expected = CommonResponse.onSuccess(profileAwardsDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상을 생성할 수 있다.")
    @Test
    void addProfileAwards() throws Exception {
        // given
        final ProfileAwardsRequestDTO.AddProfileAwardsRequest addProfileAwardsRequest
                = new AddProfileAwardsRequest("수상 이름", "수상 훈격", "수상 시기", "수상 주최 및 주관", "수상 설명");

        final AddProfileAwardsResponse addProfileAwardsResponse
                = new AddProfileAwardsResponse(1L, "수상 이름", "수상 훈격", "수상 시기", "수상 주최 및 주관", "수상 설명");

        // when
        when(profileAwardsService.addProfileAwards(anyLong(), any())).thenReturn(addProfileAwardsResponse);

        final ResultActions resultActions = performAddProfileAwards(addProfileAwardsRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 활동명"),
                                        fieldWithPath("awardsRanking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 훈격"),
                                        fieldWithPath("awardsDate")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 시기"),
                                        fieldWithPath("awardsOrganizer")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 주최 및 주관"),
                                        fieldWithPath("awardsDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 설명")
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
                                        fieldWithPath("result.profileAwardsId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 수상 ID"),
                                        fieldWithPath("result.awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 이름"),
                                        fieldWithPath("result.awardsRanking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 훈격"),
                                        fieldWithPath("result.awardsDate")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 시기"),
                                        fieldWithPath("result.awardsOrganizer")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 주최 및 주관"),
                                        fieldWithPath("result.awardsDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 설명")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddProfileAwardsResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddProfileAwardsResponse>>() {
                }
        );

        final CommonResponse<AddProfileAwardsResponse> expected = CommonResponse.onSuccess(addProfileAwardsResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상을 수정할 수 있다.")
    @Test
    void updateProfileAwards() throws Exception {
        // given
        final ProfileAwardsRequestDTO.UpdateProfileAwardsRequest updateProfileAwardsRequest
                = new UpdateProfileAwardsRequest("수상 이름", "수상 훈격", "수상 시기", "수상 주최 및 주관", "수상 설명");

        final UpdateProfileAwardsResponse updateProfileActivityResponse
                = new UpdateProfileAwardsResponse(1L, "수상 이름", "수상 훈격", "수상 시기", "수상 주최 및 주관", "수상 설명");

        // when
        when(profileAwardsService.updateProfileAwards(anyLong(), anyLong(), any())).thenReturn(updateProfileActivityResponse);

        final ResultActions resultActions = performUpdateProfileAwards(1L, updateProfileAwardsRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileAwardsId")
                                        .description("프로필 수상 ID")
                        ),
                        requestFields(
                                fieldWithPath("awardsName")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 이름"),
                                fieldWithPath("awardsRanking")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 훈격"),
                                fieldWithPath("awardsDate")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 시기"),
                                fieldWithPath("awardsOrganizer")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 주최 및 주관"),
                                fieldWithPath("awardsDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 설명")
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
                                fieldWithPath("result.profileAwardsId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 수상 ID"),
                                fieldWithPath("result.awardsName")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 이름"),
                                fieldWithPath("result.awardsRanking")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 훈격"),
                                fieldWithPath("result.awardsDate")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 시기"),
                                fieldWithPath("result.awardsOrganizer")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 주최 및 주관"),
                                fieldWithPath("result.awardsDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 설명")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateProfileAwardsResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateProfileAwardsResponse>>() {
                }
        );

        final CommonResponse<UpdateProfileAwardsResponse> expected = CommonResponse.onSuccess(updateProfileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상을 삭제할 수 있다.")
    @Test
    void removeProfileAwards() throws Exception {
        // given
        final ProfileAwardsResponseDTO.RemoveProfileAwardsResponse removeProfileAwardsResponse
                = new RemoveProfileAwardsResponse(1L);

        // when
        when(profileAwardsService.removeProfileAwards(anyLong(), anyLong())).thenReturn(removeProfileAwardsResponse);

        final ResultActions resultActions = performRemoveProfileAwards(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileAwardsId")
                                        .description("프로필 수상 ID")
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
                                fieldWithPath("result.profileAwardsId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 수상 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileAwardsResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileAwardsResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileAwardsResponse> expected = CommonResponse.onSuccess(removeProfileAwardsResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상 증명서를 추가할 수 있다.")
    @Test
    void addProfileAwardsCertification() throws Exception {
        // given
        final ProfileAwardsCertificationResponse profileAwardsCertificationResponse
                = new ProfileAwardsCertificationResponse(true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        final MockMultipartFile profileAwardsCertificationFile = new MockMultipartFile(
                "profileAwardsCertificationFile",
                "증명서.pdf",
                "multipart/form-data",
                "./src/test/resources/static/증명서.pdf".getBytes()
        );

        final Long profileAwardsId = 1L;

        // when
        when(profileAwardsService.addProfileAwardsCertification(anyLong(), anyLong(), any())).thenReturn(profileAwardsCertificationResponse);

        final ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/v1/profile/awards/certification/{profileAwardsId}", profileAwardsId)
                .file(profileAwardsCertificationFile)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileAwardsId")
                                        .description("프로필 수상 ID")
                        ),
                        requestParts(
                                partWithName("profileAwardsCertificationFile").description("수상 증명 파일")
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
                                fieldWithPath("result.isAwardsCertified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("수상 증명서 존재 여부"),
                                fieldWithPath("result.isAwardsVerified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("수상 증명서 인증 여부"),
                                fieldWithPath("result.awardsCertificationAttachFileName")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 증명서 파일 이름"),
                                fieldWithPath("result.awardsCertificationAttachFilePath")
                                        .type(JsonFieldType.STRING)
                                        .description("수상 증명서 파일 경로")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileAwardsCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileAwardsCertificationResponse>>() {
                }
        );

        final CommonResponse<ProfileAwardsCertificationResponse> expected = CommonResponse.onSuccess(profileAwardsCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 수상 증명서를 추가할 수 있다.")
    @Test
    void removeProfileAwardsCertification() throws Exception {
        // given
        final ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse removeProfileAwardsCertificationResponse
                = new RemoveProfileAwardsCertificationResponse(1L);

        // when
        when(profileAwardsService.removeProfileAwardsCertification(anyLong(), anyLong())).thenReturn(removeProfileAwardsCertificationResponse);

        final ResultActions resultActions = performRemoveAwardsCertification(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileAwardsId")
                                        .description("프로필 수상 ID")
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
                                fieldWithPath("result.profileAwardsId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 수상 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileAwardsCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileAwardsCertificationResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileAwardsCertificationResponse> expected = CommonResponse.onSuccess(removeProfileAwardsCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}

