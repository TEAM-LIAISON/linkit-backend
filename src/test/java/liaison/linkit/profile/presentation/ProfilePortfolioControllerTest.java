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
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.domain.portfolio.ProjectContribution;
import liaison.linkit.profile.domain.portfolio.ProjectSize;
import liaison.linkit.profile.presentation.portfolio.ProfilePortfolioController;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioImages;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.PortfolioSubImage;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioDetail;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItems;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectRoleAndContribution;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProjectSkillName;
import liaison.linkit.profile.service.ProfilePortfolioService;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(ProfilePortfolioController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfilePortfolioControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfilePortfolioService profilePortfolioService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfilePortfolioItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/portfolio")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetProfilePortfolioDetail(final Long profilePortfolioId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/portfolio/{profilePortfolioId}", profilePortfolioId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 나의 포트폴리오를 전체 조회할 수 있다.")
    @Test
    void getProfilePortfolioItems() throws Exception {
        // given
        final ProfilePortfolioResponseDTO.ProfilePortfolioItem firstProfilePortfolioItem
                = new ProfilePortfolioItem(1L, "프로젝트 이름", "프로젝트 한 줄 소개", ProjectSize.PERSONAL, "2023.02", "2023.04", false, Arrays.asList("컨텐츠 제작", "브랜딩", "계정 관리"), "logo.png");

        final ProfilePortfolioResponseDTO.ProfilePortfolioItem secondProfilePortfolioItem
                = new ProfilePortfolioItem(2L, "프로젝트 이름", "프로젝트 한 줄 소개", ProjectSize.TEAM, "2023.03", "2023.06", true, Arrays.asList("컨텐츠 제작", "브랜딩", "계정 관리"), "logo.png");

        final ProfilePortfolioResponseDTO.ProfilePortfolioItems profilePortfolioItems
                = new ProfilePortfolioItems(Arrays.asList(firstProfilePortfolioItem, secondProfilePortfolioItem));

        // when
        when(profilePortfolioService.getProfilePortfolioItems(anyLong())).thenReturn(profilePortfolioItems);

        final ResultActions resultActions = performGetProfilePortfolioItems();

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
                                        subsectionWithPath("result.profilePortfolioItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 수상 아이템 배열"),
                                        fieldWithPath("result.profilePortfolioItems[].profilePortfolioId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 수상 ID"),
                                        fieldWithPath("result.profilePortfolioItems[].projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 이름"),
                                        fieldWithPath("result.profilePortfolioItems[].projectLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 한 줄 소개"),
                                        fieldWithPath("result.profilePortfolioItems[].projectSize")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 규모"),
                                        fieldWithPath("result.profilePortfolioItems[].projectStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 시작 날짜"),
                                        fieldWithPath("result.profilePortfolioItems[].projectEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 종료 날짜"),
                                        fieldWithPath("result.profilePortfolioItems[].isProjectInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로젝트 진행 여부"),
                                        fieldWithPath("result.profilePortfolioItems[].projectRoles[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로젝트 역할 배열"),
                                        fieldWithPath("result.profilePortfolioItems[].projectRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 대표 이미지 경로")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfilePortfolioItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfilePortfolioItems>>() {
                }
        );

        final CommonResponse<ProfilePortfolioItems> expected = CommonResponse.onSuccess(profilePortfolioItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 포트폴리오를 상세 조회할 수 있다.")
    @Test
    void getProfilePortfolioDetail() throws Exception {
        // given

        final List<ProjectRoleAndContribution> projectRoleAndContributions = Arrays.asList(
                new ProjectRoleAndContribution("백엔드 개발자", ProjectContribution.HIGH),
                new ProjectRoleAndContribution("PM", ProjectContribution.MIDDLE)
        );

        final List<ProjectSkillName> projectSkillNames = Arrays.asList(
                new ProjectSkillName("Java"),
                new ProjectSkillName("Figma")
        );

        final List<PortfolioSubImage> portfolioSubImages = Arrays.asList(
                new PortfolioSubImage("프로젝트 보조 이미지 1"),
                new PortfolioSubImage("프로젝트 보조 이미지 2")
        );

        final PortfolioImages portfolioImages = new PortfolioImages("대표 이미지", portfolioSubImages);

        final ProfilePortfolioResponseDTO.ProfilePortfolioDetail profilePortfolioDetail
                = new ProfilePortfolioDetail(1L, "프로젝트 이름", "프로젝트 한 줄 설명", ProjectSize.TEAM, 4, "팀 구성", "2022.06", "2026.06", false, projectRoleAndContributions, projectSkillNames, "프로젝트 링크",
                "프로젝트 설명", portfolioImages);

        // when
        when(profilePortfolioService.getProfilePortfolioDetail(anyLong(), anyLong())).thenReturn(profilePortfolioDetail);

        final ResultActions resultActions = performGetProfilePortfolioDetail(1L);

        // then
        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("profilePortfolioId")
                                                .description("프로필 포트폴리오 ID")
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
                                        fieldWithPath("result.profilePortfolioId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 포트폴리오 ID"),
                                        fieldWithPath("result.projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 이름"),
                                        fieldWithPath("result.projectLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 한 줄 설명"),
                                        fieldWithPath("result.projectSize")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 규모"),
                                        fieldWithPath("result.projectHeadCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로젝트 인원 수"),
                                        fieldWithPath("result.projectTeamComposition")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 팀 구성"),
                                        fieldWithPath("result.projectStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 시작 날짜"),
                                        fieldWithPath("result.projectEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 종료 날짜"),
                                        fieldWithPath("result.isProjectInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로젝트 진행 여부"),
                                        fieldWithPath("result.projectRoleAndContributions[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로젝트 역할 및 기여도 목록"),
                                        fieldWithPath("result.projectRoleAndContributions[].projectRole")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 역할"),
                                        fieldWithPath("result.projectRoleAndContributions[].projectContribution")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 기여도 (예: HIGH, UPPER_MIDDLE, MIDDLE, LOWER_MIDDLE, LOWER)"),
                                        fieldWithPath("result.projectSkillNames[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로젝트 스킬 이름 목록"),
                                        fieldWithPath("result.projectSkillNames[].projectSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 스킬 이름"),
                                        fieldWithPath("result.projectLink")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 링크"),
                                        fieldWithPath("result.projectDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 설명"),
                                        fieldWithPath("result.portfolioImages")
                                                .type(JsonFieldType.OBJECT)
                                                .description("포트폴리오 이미지 정보"),
                                        fieldWithPath("result.portfolioImages.projectRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 대표 이미지 경로"),
                                        fieldWithPath("result.portfolioImages.portfolioSubImages")
                                                .type(JsonFieldType.ARRAY)
                                                .description("포트폴리오 서브 이미지 목록"),
                                        fieldWithPath("result.portfolioImages.portfolioSubImages[].projectSubImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 서브 이미지 경로")
                                )
                        )
                ).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfilePortfolioDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfilePortfolioDetail>>() {
                }
        );

        final CommonResponse<ProfilePortfolioDetail> expected = CommonResponse.onSuccess(profilePortfolioDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
