package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.product.TeamProductController;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.AddTeamProductRequest;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.TeamProductLinkRequest;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.UpdateTeamProductRequest;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.AddTeamProductResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.ProductSubImage;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.RemoveTeamProductResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductDetail;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductImages;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductItems;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductLinkResponse;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductViewItem;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.TeamProductViewItems;
import liaison.linkit.team.presentation.product.dto.TeamProductResponseDTO.UpdateTeamProductResponse;
import liaison.linkit.team.business.service.product.TeamProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamProductControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamProductService teamProductService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamProductViewItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamCode}/product/view", teamCode)
        );
    }

    private ResultActions performGetTeamProductItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamCode}/product", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetTeamProductDetail(final String teamCode, final Long teamProductId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamCode}/product/{teamProductId}", teamCode, teamProductId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performRemoveTeamProduct(final String teamCode, final Long teamProductId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/team/{teamCode}/product/{teamProductId}", teamCode, teamProductId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 팀의 팀 프로덕트 뷰어를 전체 조회할 수 있다.")
    @Test
    void getTeamProductViewItems() throws Exception {

        // given
        final TeamProductResponseDTO.TeamProductViewItems teamProductViewItems = TeamProductViewItems.builder()
                .teamProductViewItems(Arrays.asList(
                        TeamProductViewItem.builder()
                                .teamProductId(1L)
                                .productName("프로덕트 제목 1")
                                .productLineDescription("프로덕트 한 줄 소개 1")
                                .productField("프로덕트 분야 1")
                                .productStartDate("프로덕트 시작 날짜 1")
                                .productEndDate("프로덕트 종료 날짜 1")
                                .productRepresentImagePath("프로덕트 대표 이미지 1")
                                .isProductInProgress(false)
                                .teamProductLinks(Arrays.asList(
                                        TeamProductLinkResponse.builder()
                                                .productLinkId(1L)
                                                .productLinkName("프로덕트 링크 이름 1")
                                                .productLinkPath("프로덕트 링크 경로 1")
                                                .build(),
                                        TeamProductLinkResponse.builder()
                                                .productLinkId(2L)
                                                .productLinkName("프로덕트 링크 이름 2")
                                                .productLinkPath("프로덕트 링크 경로 2")
                                                .build()
                                ))
                                .productDescription("프로덕트 설명 1")
                                .teamProductImages(
                                        TeamProductImages.builder()
                                                .productRepresentImagePath("프로덕트 대표 이미지 경로")
                                                .productSubImages(Arrays.asList(
                                                        ProductSubImage.builder()
                                                                .productSubImagePath("프로덕트 보조 이미지 경로 1")
                                                                .build(),
                                                        ProductSubImage.builder()
                                                                .productSubImagePath("프로덕트 보조 이미지 경로 2")
                                                                .build()
                                                ))
                                                .build()
                                )
                                .build(),
                        TeamProductViewItem.builder()
                                .teamProductId(1L)
                                .productName("프로덕트 제목 2")
                                .productLineDescription("프로덕트 한 줄 소개 2")
                                .productField("프로덕트 분야 2")
                                .productStartDate("프로덕트 시작 날짜 2")
                                .productEndDate("프로덕트 종료 날짜 2")
                                .productRepresentImagePath("프로덕트 대표 이미지 2")
                                .isProductInProgress(false)
                                .teamProductLinks(Arrays.asList(
                                        TeamProductLinkResponse.builder()
                                                .productLinkId(1L)
                                                .productLinkName("프로덕트 링크 이름 1")
                                                .productLinkPath("프로덕트 링크 경로 1")
                                                .build(),
                                        TeamProductLinkResponse.builder()
                                                .productLinkId(2L)
                                                .productLinkName("프로덕트 링크 이름 2")
                                                .productLinkPath("프로덕트 링크 경로 2")
                                                .build()
                                ))
                                .productDescription("프로덕트 설명 2")
                                .teamProductImages(
                                        TeamProductImages.builder()
                                                .productRepresentImagePath("프로덕트 대표 이미지 경로")
                                                .productSubImages(Arrays.asList(
                                                        ProductSubImage.builder()
                                                                .productSubImagePath("프로덕트 보조 이미지 경로 1")
                                                                .build(),
                                                        ProductSubImage.builder()
                                                                .productSubImagePath("프로덕트 보조 이미지 경로 2")
                                                                .build()
                                                ))
                                                .build()
                                )
                                .build()
                ))
                .build();
        // when
        when(teamProductService.getTeamProductViewItems(any())).thenReturn(teamProductViewItems);

        final ResultActions resultActions = performGetTeamProductViewItems("liaison");
        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamCode")
                                                .description("팀 아이디 (팀 코드)")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),
                                        fieldWithPath("result.teamProductViewItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 프로덕트 뷰어 목록"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 프로덕트 ID"),
                                        fieldWithPath("result.teamProductViewItems[].productName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 이름"),
                                        fieldWithPath("result.teamProductViewItems[].productLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 한 줄 소개"),
                                        fieldWithPath("result.teamProductViewItems[].productField")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 분야"),
                                        fieldWithPath("result.teamProductViewItems[].productStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 시작 날짜"),
                                        fieldWithPath("result.teamProductViewItems[].productEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 종료 날짜"),
                                        fieldWithPath("result.teamProductViewItems[].productRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 대표 이미지 경로"),
                                        fieldWithPath("result.teamProductViewItems[].isProductInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로덕트 진행 중 여부"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductLinks")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로덕트 링크 목록"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductLinks[].productLinkId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로덕트 링크 ID"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductLinks[].productLinkName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 이름"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductLinks[].productLinkPath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 경로"),
                                        fieldWithPath("result.teamProductViewItems[].productDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 설명"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductImages")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로덕트 이미지 정보 객체"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductImages.productRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 대표 이미지 경로"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductImages.productSubImages")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로덕트 보조 이미지 리스트"),
                                        fieldWithPath("result.teamProductViewItems[].teamProductImages.productSubImages[].productSubImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 보조 이미지 경로")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamProductResponseDTO.TeamProductViewItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamProductViewItems>>() {
                }
        );

        final CommonResponse<TeamProductViewItems> expected = CommonResponse.onSuccess(teamProductViewItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀 프로덕트를 전체 조회할 수 있다.")
    @Test
    void getTeamProductItems() throws Exception {
        // given
        final TeamProductResponseDTO.TeamProductItem firstTeamProductItem = TeamProductResponseDTO.TeamProductItem.builder()
                .teamProductId(1L)
                .productName("프로덕트 제목 1")
                .productLineDescription("프로덕트 한 줄 소개 1")
                .productField("프로덕트 분야 1")
                .productStartDate("프로덕트 시작 날짜 1")
                .productEndDate("프로덕트 종료 날짜 1")
                .productRepresentImagePath("프로덕트 대표 이미지 1")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(1L)
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("프로덕트 링크 경로 1")
                                .build(),
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(2L)
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("프로덕트 링크 경로 2")
                                .build()
                ))
                .productDescription("프로덕트 설명 1")
                .build();

        final TeamProductResponseDTO.TeamProductItem secondTeamProductItem = TeamProductResponseDTO.TeamProductItem.builder()
                .teamProductId(1L)
                .productName("프로덕트 제목 2")
                .productLineDescription("프로덕트 한 줄 소개 2")
                .productField("프로덕트 분야 2")
                .productStartDate("프로덕트 시작 날짜 2")
                .productEndDate("프로덕트 종료 날짜 2")
                .productRepresentImagePath("프로덕트 대표 이미지 2")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(1L)
                                .productLinkName("프로덕트 링크 이름 3")
                                .productLinkPath("프로덕트 링크 경로 3")
                                .build(),
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(2L)
                                .productLinkName("프로덕트 링크 이름 4")
                                .productLinkPath("프로덕트 링크 경로 4")
                                .build()
                ))
                .productDescription("프로덕트 설명 2")
                .build();

        final TeamProductResponseDTO.TeamProductItems teamProductItems
                = new TeamProductItems(Arrays.asList(firstTeamProductItem, secondTeamProductItem));
        // when
        when(teamProductService.getTeamProductItems(anyLong(), any())).thenReturn(teamProductItems);

        final ResultActions resultActions = performGetTeamProductItems("liaison");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamCode")
                                                .description("팀 아이디 (팀 코드)")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),
                                        fieldWithPath("result.teamProductItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 프로덕트 목록"),
                                        fieldWithPath("result.teamProductItems[].teamProductId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 프로덕트 ID"),
                                        fieldWithPath("result.teamProductItems[].productName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 이름"),
                                        fieldWithPath("result.teamProductItems[].productLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 한 줄 소개"),
                                        fieldWithPath("result.teamProductItems[].productField")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 분야"),
                                        fieldWithPath("result.teamProductItems[].productStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 시작 날짜"),
                                        fieldWithPath("result.teamProductItems[].productEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 종료 날짜"),
                                        fieldWithPath("result.teamProductItems[].productRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 대표 이미지 경로"),
                                        fieldWithPath("result.teamProductItems[].isProductInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로덕트 진행 중 여부"),
                                        fieldWithPath("result.teamProductItems[].teamProductLinks")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로덕트 링크 목록"),
                                        fieldWithPath("result.teamProductItems[].teamProductLinks[].productLinkId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로덕트 링크 ID"),
                                        fieldWithPath("result.teamProductItems[].teamProductLinks[].productLinkName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 이름"),
                                        fieldWithPath("result.teamProductItems[].teamProductLinks[].productLinkPath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 경로"),
                                        fieldWithPath("result.teamProductItems[].productDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 설명")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamProductResponseDTO.TeamProductItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamProductItems>>() {
                }
        );

        final CommonResponse<TeamProductItems> expected = CommonResponse.onSuccess(teamProductItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀 프로덕트를 단일 조회할 수 있다.")
    @Test
    void getTeamProductDetail() throws Exception {
        final TeamProductResponseDTO.TeamProductDetail teamProductDetail = TeamProductResponseDTO.TeamProductDetail.builder()
                .teamProductId(1L)
                .productName("프로덕트 제목 1")
                .productLineDescription("프로덕트 한 줄 소개 1")
                .productField("프로덕트 분야")
                .productStartDate("프로덕트 시작 날짜 1")
                .productEndDate("프로덕트 종료 날짜 1")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(1L)
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("프로덕트 링크 경로 1")
                                .build(),
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(2L)
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("프로덕트 링크 경로 2")
                                .build()
                ))
                .productDescription("프로덕트 설명 1")
                .teamProductImages(
                        TeamProductResponseDTO.TeamProductImages.builder()
                                .productRepresentImagePath("프로덕트 대표 이미지 경로")
                                .productSubImages(Arrays.asList(
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 첫번째 보조 이미지")
                                                .build(),
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 두번째 보조 이미지")
                                                .build()
                                ))
                                .build()
                )
                .build();
        // when
        when(teamProductService.getTeamProductDetail(anyLong(), any(), anyLong())).thenReturn(teamProductDetail);

        final ResultActions resultActions = performGetTeamProductDetail("liaison", 1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamCode")
                                                .description("팀 아이디 (팀 코드)"),
                                        parameterWithName("teamProductId")
                                                .description("팀 프로덕트 ID")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),

                                        fieldWithPath("result.teamProductId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 프로덕트 ID"),
                                        fieldWithPath("result.productName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 이름"),
                                        fieldWithPath("result.productLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 한 줄 소개"),
                                        fieldWithPath("result.productField")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 분야"),
                                        fieldWithPath("result.productStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 시작 날짜"),
                                        fieldWithPath("result.productEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 종료 날짜"),
                                        fieldWithPath("result.isProductInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로덕트 진행 중 여부"),
                                        fieldWithPath("result.teamProductLinks")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로덕트 링크 목록"),
                                        fieldWithPath("result.teamProductLinks[].productLinkId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로덕트 링크 ID"),
                                        fieldWithPath("result.teamProductLinks[].productLinkName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 이름"),
                                        fieldWithPath("result.teamProductLinks[].productLinkPath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 링크 경로"),
                                        fieldWithPath("result.productDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 설명"),
                                        fieldWithPath("result.teamProductImages")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로덕트 이미지 정보"),
                                        fieldWithPath("result.teamProductImages.productRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 대표 이미지 경로"),
                                        fieldWithPath("result.teamProductImages.productSubImages")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로덕트 보조 이미지 목록"),
                                        fieldWithPath("result.teamProductImages.productSubImages[].productSubImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로덕트 보조 이미지 경로")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamProductDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamProductDetail>>() {
                }
        );

        final CommonResponse<TeamProductDetail> expected = CommonResponse.onSuccess(teamProductDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀 프로덕트를 단일 생성할 수 있다.")
    @Test
    void addTeamProduct() throws Exception {
        // given
        final TeamProductRequestDTO.AddTeamProductRequest addTeamProductRequest = AddTeamProductRequest.builder()
                .productName("프로덕트 이름")
                .productLineDescription("프로덕트 한 줄 소개")
                .productField("프로덕트 분야")
                .productStartDate("2012.12")
                .productEndDate("2013.12")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductRequestDTO.TeamProductLinkRequest.builder()
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("https://www.naver.com")
                                .build(),
                        TeamProductRequestDTO.TeamProductLinkRequest.builder()
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("https://www.naver.com")
                                .build()
                ))
                .productDescription("프로덕트 설명")
                .build();

        final MockMultipartFile createRequest = new MockMultipartFile(
                "addTeamProductRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(addTeamProductRequest).getBytes(StandardCharsets.UTF_8)
        );

        // productRepresentImage 생성
        final MockMultipartFile productRepresentImage = new MockMultipartFile(
                "productRepresentImage",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        // productSubImages 생성
        MockMultipartFile subImage1 = new MockMultipartFile(
                "productSubImages",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        MockMultipartFile subImage2 = new MockMultipartFile(
                "productSubImages",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        // 응답
        final TeamProductResponseDTO.AddTeamProductResponse addTeamProductResponse = AddTeamProductResponse
                .builder()
                .teamProductId(1L)
                .productName("프로덕트 이름")
                .productLineDescription("프로덕트 한 줄 소개")
                .productField("프로덕트 분야")
                .productStartDate("2012.12")
                .productEndDate("2013.12")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(1L)
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("https://www.naver.com")
                                .build(),
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(2L)
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("https://www.naver.com")
                                .build()
                ))
                .productDescription("프로덕트 설명 1")
                .teamProductImages(
                        TeamProductResponseDTO.TeamProductImages.builder()
                                .productRepresentImagePath("프로덕트 대표 이미지 경로")
                                .productSubImages(Arrays.asList(
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 첫번째 보조 이미지")
                                                .build(),
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 두번째 보조 이미지")
                                                .build()
                                ))
                                .build()
                )
                .build();

        final String teamCode = "liaison";
        // when
        when(teamProductService.addTeamProduct(anyLong(), any(), any(), any(), any())).thenReturn(addTeamProductResponse);

        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/v1/team/{teamCode}/product", teamCode)
                        .file(productRepresentImage)
                        .file(subImage1)
                        .file(subImage2)
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
                        pathParameters(
                                parameterWithName("teamCode")
                                        .description("팀 아이디 (팀 코드)")
                        ),
                        requestParts(
                                partWithName("addTeamProductRequest")
                                        .description("팀 프로덕트 생성 정보 객체 (JSON)"),
                                partWithName("productRepresentImage")
                                        .description("팀 프로덕트 대표 이미지 파일 (이미지 형식)")
                                        .attributes(
                                                key("contentType").value("image/png"),
                                                key("constraints").value("최대 5MB")
                                        ),
                                partWithName("productSubImages")
                                        .description("팀 프로덕트 보조 이미지 파일 배열 (이미지 형식)")
                                        .attributes(
                                                key("contentType").value("image/jpeg, image/png"),
                                                key("constraints").value("파일 당 최대 5MB")
                                        )
                        ),
                        requestPartFields("addTeamProductRequest",
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("프로덕트 이름"),
                                fieldWithPath("productLineDescription").type(JsonFieldType.STRING).description("프로덕트 한 줄 소개"),
                                fieldWithPath("productField").type(JsonFieldType.STRING).description("프로덕트 분야"),
                                fieldWithPath("productStartDate").type(JsonFieldType.STRING).description("프로덕트 시작 날짜"),
                                fieldWithPath("productEndDate").type(JsonFieldType.STRING).description("프로덕트 종료 날짜"),
                                fieldWithPath("isProductInProgress").type(JsonFieldType.BOOLEAN).description("프로덕트 진행 중 여부"),
                                fieldWithPath("teamProductLinks").type(JsonFieldType.ARRAY).description("프로덕트 링크 목록"),
                                fieldWithPath("teamProductLinks[].productLinkName").type(JsonFieldType.STRING).description("프로덕트 링크 이름"),
                                fieldWithPath("teamProductLinks[].productLinkPath").type(JsonFieldType.STRING).description("프로덕트 링크 경로"),
                                fieldWithPath("productDescription").type(JsonFieldType.STRING).description("프로덕트 설명")
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
                                fieldWithPath("result")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 결과"),
                                fieldWithPath("result.teamProductId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("팀 프로덕트 ID"),
                                fieldWithPath("result.productName")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 이름"),
                                fieldWithPath("result.productLineDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 한 줄 소개"),
                                fieldWithPath("result.productField")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 분야"),
                                fieldWithPath("result.productStartDate")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 시작 날짜"),
                                fieldWithPath("result.productEndDate")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 종료 날짜"),
                                fieldWithPath("result.isProductInProgress")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("프로덕트 진행 중 여부"),
                                fieldWithPath("result.teamProductLinks")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로덕트 링크 목록"),
                                fieldWithPath("result.teamProductLinks[].productLinkId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로덕트 링크 ID"),
                                fieldWithPath("result.teamProductLinks[].productLinkName")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 링크 이름"),
                                fieldWithPath("result.teamProductLinks[].productLinkPath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 링크 경로"),
                                fieldWithPath("result.productDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 설명"),
                                fieldWithPath("result.teamProductImages")
                                        .type(JsonFieldType.OBJECT)
                                        .description("프로덕트 이미지 정보"),
                                fieldWithPath("result.teamProductImages.productRepresentImagePath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 대표 이미지 경로"),
                                fieldWithPath("result.teamProductImages.productSubImages")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로덕트 보조 이미지 목록"),
                                fieldWithPath("result.teamProductImages.productSubImages[].productSubImagePath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 보조 이미지 경로")
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamProductResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddTeamProductResponse>>() {
                }
        );

        final CommonResponse<AddTeamProductResponse> expected = CommonResponse.onSuccess(addTeamProductResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀 프로덕트를 단일 수정할 수 있다.")
    @Test
    void updateTeamProduct() throws Exception {
        // given
        final TeamProductRequestDTO.UpdateTeamProductRequest updateTeamProductRequest = UpdateTeamProductRequest.builder()
                .productName("프로덕트 이름")
                .productLineDescription("프로덕트 한 줄 소개")
                .productField("프로덕트 분야")
                .productStartDate("2012.12")
                .productEndDate("2013.12")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductLinkRequest.builder()
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("https://www.naver.com")
                                .build(),
                        TeamProductLinkRequest.builder()
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("https://www.naver.com")
                                .build()
                ))
                .productDescription("프로덕트 설명")
                .teamProductImages(
                        TeamProductRequestDTO.TeamProductImages.builder()
                                .productRepresentImagePath("유지되는 대표 이미지 경로")
                                .productSubImages(Arrays.asList(
                                        TeamProductRequestDTO.ProductSubImage.builder()
                                                .productSubImagePath("유지되는 프로덕트 보조 이미지 경로 1")
                                                .build(),
                                        TeamProductRequestDTO.ProductSubImage.builder()
                                                .productSubImagePath("유지되는 프로덕트 보조 이미지 경로 2")
                                                .build()
                                ))
                                .build()
                )
                .build();

        final MockMultipartFile updateRequest = new MockMultipartFile(
                "updateTeamProductRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(updateTeamProductRequest).getBytes(StandardCharsets.UTF_8)
        );

        // productRepresentImage 생성
        final MockMultipartFile productRepresentImage = new MockMultipartFile(
                "productRepresentImage",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        // productSubImages 생성
        MockMultipartFile subImage1 = new MockMultipartFile(
                "productSubImages",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        MockMultipartFile subImage2 = new MockMultipartFile(
                "productSubImages",
                "logo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        // 응답
        final TeamProductResponseDTO.UpdateTeamProductResponse updateTeamProductResponse = UpdateTeamProductResponse
                .builder()
                .teamProductId(1L)
                .productName("프로덕트 이름")
                .productLineDescription("프로덕트 한 줄 소개")
                .productField("프로덕트 분야")
                .productStartDate("2012.12")
                .productEndDate("2013.12")
                .isProductInProgress(false)
                .teamProductLinks(Arrays.asList(
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(1L)
                                .productLinkName("프로덕트 링크 이름 1")
                                .productLinkPath("https://www.naver.com")
                                .build(),
                        TeamProductResponseDTO.TeamProductLinkResponse.builder()
                                .productLinkId(2L)
                                .productLinkName("프로덕트 링크 이름 2")
                                .productLinkPath("https://www.naver.com")
                                .build()
                ))
                .productDescription("프로덕트 설명 1")
                .teamProductImages(
                        TeamProductResponseDTO.TeamProductImages.builder()
                                .productRepresentImagePath("프로덕트 대표 이미지 경로")
                                .productSubImages(Arrays.asList(
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 첫번째 보조 이미지")
                                                .build(),
                                        ProductSubImage.builder()
                                                .productSubImagePath("프로덕트 두번째 보조 이미지")
                                                .build()
                                ))
                                .build()
                )
                .build();

        final String teamCode = "liaison";
        final Long teamProductId = 1L;
        // when
        when(teamProductService.updateTeamProduct(anyLong(), any(), anyLong(), any(), any(), any())).thenReturn(updateTeamProductResponse);

        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/v1/team/{teamCode}/product/{teamProductId}", teamCode, teamProductId)
                        .file(productRepresentImage)
                        .file(subImage1)
                        .file(subImage2)
                        .file(updateRequest)
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
                        pathParameters(
                                parameterWithName("teamCode")
                                        .description("팀 아이디 (팀 코드)"),
                                parameterWithName("teamProductId")
                                        .description("팀 프로덕트 ID")
                        ),
                        requestParts(
                                partWithName("updateTeamProductRequest")
                                        .description("팀 프로덕트 수정 정보 객체 (JSON)"),
                                partWithName("productRepresentImage")
                                        .description("팀 프로덕트 대표 이미지 파일 (이미지 형식)")
                                        .attributes(
                                                key("contentType").value("image/png"),
                                                key("constraints").value("최대 5MB")
                                        ),
                                partWithName("productSubImages")
                                        .description("팀 프로덕트 보조 이미지 파일 배열 (이미지 형식)")
                                        .attributes(
                                                key("contentType").value("image/jpeg, image/png"),
                                                key("constraints").value("파일 당 최대 5MB")
                                        )
                        ),
                        requestPartFields("updateTeamProductRequest",
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("프로덕트 이름"),
                                fieldWithPath("productLineDescription").type(JsonFieldType.STRING).description("프로덕트 한 줄 소개"),
                                fieldWithPath("productField").type(JsonFieldType.STRING).description("프로덕트 분야"),
                                fieldWithPath("productStartDate").type(JsonFieldType.STRING).description("프로덕트 시작 날짜 (YYYY.MM)"),
                                fieldWithPath("productEndDate").type(JsonFieldType.STRING).description("프로덕트 종료 날짜 (YYYY.MM)"),
                                fieldWithPath("isProductInProgress").type(JsonFieldType.BOOLEAN).description("프로덕트 진행 중 여부"),
                                fieldWithPath("teamProductLinks").type(JsonFieldType.ARRAY).description("프로덕트 링크 목록"),
                                fieldWithPath("teamProductLinks[].productLinkName").type(JsonFieldType.STRING).description("프로덕트 링크 이름"),
                                fieldWithPath("teamProductLinks[].productLinkPath").type(JsonFieldType.STRING).description("프로덕트 링크 경로"),
                                fieldWithPath("productDescription").type(JsonFieldType.STRING).description("프로덕트 설명"),
                                // 추가: teamProductImages 객체
                                fieldWithPath("teamProductImages").type(JsonFieldType.OBJECT).description("프로덕트 이미지 정보"),
                                fieldWithPath("teamProductImages.productRepresentImagePath").type(JsonFieldType.STRING).description("유지되는 대표 이미지 경로"),
                                fieldWithPath("teamProductImages.productSubImages").type(JsonFieldType.ARRAY).description("유지되는 보조 이미지 목록"),
                                fieldWithPath("teamProductImages.productSubImages[].productSubImagePath").type(JsonFieldType.STRING).description("유지되는 보조 이미지 경로")
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
                                fieldWithPath("result")
                                        .type(JsonFieldType.OBJECT)
                                        .description("응답 결과"),
                                fieldWithPath("result.teamProductId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("팀 프로덕트 ID"),
                                fieldWithPath("result.productName")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 이름"),
                                fieldWithPath("result.productLineDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 한 줄 소개"),
                                fieldWithPath("result.productField")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 분야"),
                                fieldWithPath("result.productStartDate")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 시작 날짜"),
                                fieldWithPath("result.productEndDate")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 종료 날짜"),
                                fieldWithPath("result.isProductInProgress")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("프로덕트 진행 중 여부"),
                                fieldWithPath("result.teamProductLinks")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로덕트 링크 목록"),
                                fieldWithPath("result.teamProductLinks[].productLinkId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로덕트 링크 ID"),
                                fieldWithPath("result.teamProductLinks[].productLinkName")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 링크 이름"),
                                fieldWithPath("result.teamProductLinks[].productLinkPath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 링크 경로"),
                                fieldWithPath("result.productDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 설명"),
                                fieldWithPath("result.teamProductImages")
                                        .type(JsonFieldType.OBJECT)
                                        .description("프로덕트 이미지 정보"),
                                fieldWithPath("result.teamProductImages.productRepresentImagePath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 대표 이미지 경로"),
                                fieldWithPath("result.teamProductImages.productSubImages")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로덕트 보조 이미지 목록"),
                                fieldWithPath("result.teamProductImages.productSubImages[].productSubImagePath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로덕트 보조 이미지 경로")
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamProductResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamProductResponse>>() {
                }
        );

        final CommonResponse<UpdateTeamProductResponse> expected = CommonResponse.onSuccess(updateTeamProductResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀 프로덕트를 단일 삭제할 수 있다.")
    @Test
    void removeTeamProduct() throws Exception {
        // given
        final TeamProductResponseDTO.RemoveTeamProductResponse removeTeamProductResponse =
                RemoveTeamProductResponse.builder()
                        .teamProductId(1L)
                        .build();

        // when
        when(teamProductService.removeTeamProduct(any(), anyLong())).thenReturn(removeTeamProductResponse);

        final ResultActions resultActions = performRemoveTeamProduct("liaison", 1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamCode")
                                        .description("팀 아이디 (팀 코드)"),
                                parameterWithName("teamProductId")
                                        .description("팀 프로덕트 ID")
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
                                fieldWithPath("result.teamProductId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("삭제된 팀 프로덕트 ID")
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveTeamProductResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveTeamProductResponse>>() {
                }
        );

        final CommonResponse<RemoveTeamProductResponse> expected = CommonResponse.onSuccess(removeTeamProductResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
