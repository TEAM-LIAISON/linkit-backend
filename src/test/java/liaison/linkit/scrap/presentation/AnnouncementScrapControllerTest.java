package liaison.linkit.scrap.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.scrap.business.service.AnnouncementScrapService;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapRequestDTO.UpdateAnnouncementScrapRequest;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO.UpdateAnnouncementScrap;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AnnouncementScrapController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AnnouncementScrapControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnnouncementScrapService announcementScrapService;

    private ResultActions performUpdateAnnouncementScrap(final Long teamMemberAnnouncementId, final UpdateAnnouncementScrapRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/announcement/scrap/{teamMemberAnnouncementId}", teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions performGetAnnouncementScraps() throws Exception {
        return mockMvc.perform(
                get("/api/v1/announcement/scrap")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 팀원 공고를 스크랩/스크랩취소를 진행한다.")
    @Test
    void updateAnnouncementScrap() throws Exception {
        // given
        final UpdateAnnouncementScrapRequest updateTeamScrapRequest = UpdateAnnouncementScrapRequest.builder()
                .changeScrapValue(true)
                .build();

        final AnnouncementScrapResponseDTO.UpdateAnnouncementScrap updateAnnouncementScrap = UpdateAnnouncementScrap.builder()
                .teamMemberAnnouncementId(1L)
                .isAnnouncementScrap(true)
                .build();

        // when
        when(announcementScrapService.updateAnnouncementScrap(anyLong(), any(), any())).thenReturn(updateAnnouncementScrap);

        final ResultActions resultActions = performUpdateAnnouncementScrap(1L, updateTeamScrapRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamMemberAnnouncementId")
                                                .description("팀원 공고 ID")
                                ),
                                requestFields(
                                        fieldWithPath("changeScrapValue")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("변경하고자 하는 스크랩 상태 (스크랩하기 -> true, 스크랩취소 -> false)")
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

                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 ID"),
                                        fieldWithPath("result.isAnnouncementScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("변경된 팀원 공고 스크랩 여부 (true -> 스크랩한 상태 / false -> 스크랩하지 않은 상태)")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateAnnouncementScrap> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateAnnouncementScrap>>() {
                }
        );

        final CommonResponse<UpdateAnnouncementScrap> expected = CommonResponse.onSuccess(updateAnnouncementScrap);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 내가 스크랩한 팀원 공고 전체를 조회한다.")
    @Test
    void getAnnouncementScraps() throws Exception {
        // given
        final AnnouncementInformMenus announcementInformMenus = AnnouncementInformMenus.builder()
                .announcementInformMenus(Arrays.asList(
                        AnnouncementInformMenu.builder()
                                .teamMemberAnnouncementId(1L)
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .teamName("팀 이름")
                                .teamScaleItem(
                                        TeamScaleItem.builder()
                                                .teamScaleName("팀 규모 이름 (1인)")
                                                .build()
                                )
                                .regionDetail(
                                        RegionDetail.builder()
                                                .cityName("팀 활동지역 (시/도)")
                                                .divisionName("팀 활동지역 (시/군/구)")
                                                .build()
                                )
                                .announcementDDay(20)
                                .announcementTitle("공고 제목")
                                .isAnnouncementScrap(true)
                                .announcementScrapCount(100)
                                .announcementPositionItem(
                                        AnnouncementPositionItem.builder()
                                                .majorPosition("포지션 대분류")
                                                .subPosition("포지션 소분류")
                                                .build()
                                )
                                .announcementSkillNames(
                                        Arrays.asList(
                                                AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 요구 스킬 1")
                                                        .build(),
                                                AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 요구 스킬 2")
                                                        .build()
                                        )
                                )
                                .build(),
                        AnnouncementInformMenu.builder()
                                .teamMemberAnnouncementId(2L)
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .teamName("팀 이름")
                                .teamScaleItem(
                                        TeamScaleItem.builder()
                                                .teamScaleName("팀 규모 이름 (1인)")
                                                .build()
                                )
                                .regionDetail(
                                        RegionDetail.builder()
                                                .cityName("팀 활동지역 (시/도)")
                                                .divisionName("팀 활동지역 (시/군/구)")
                                                .build()
                                )
                                .announcementDDay(20)
                                .announcementTitle("공고 제목")
                                .isAnnouncementScrap(true)
                                .announcementScrapCount(100)
                                .announcementPositionItem(
                                        AnnouncementPositionItem.builder()
                                                .majorPosition("포지션 대분류")
                                                .subPosition("포지션 소분류")
                                                .build()
                                )
                                .announcementSkillNames(
                                        Arrays.asList(
                                                AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 요구 스킬 1")
                                                        .build(),
                                                AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 요구 스킬 2")
                                                        .build()
                                        )
                                )
                                .build()
                ))
                .build();

        // when
        when(announcementScrapService.getAnnouncementScraps(anyLong())).thenReturn(announcementInformMenus);

        final ResultActions resultActions = performGetAnnouncementScraps();
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
                                                .description("요청 성공 여부"),

                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드"),

                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지"),

                                        // result
                                        subsectionWithPath("result.announcementInformMenus")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 정보 목록"),

                                        fieldWithPath("result.announcementInformMenus[].teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 ID"),

                                        // announcementInformMenus[].teamLogoImagePath
                                        fieldWithPath("result.announcementInformMenus[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),

                                        // announcementInformMenus[].teamName
                                        fieldWithPath("result.announcementInformMenus[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),

                                        // announcementInformMenus[].teamScaleItem
                                        fieldWithPath("result.announcementInformMenus[].teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 규모 정보 객체"),
                                        fieldWithPath("result.announcementInformMenus[].teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모 이름"),

                                        // announcementInformMenus[].regionDetail
                                        fieldWithPath("result.announcementInformMenus[].regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 활동지역 정보 객체"),
                                        fieldWithPath("result.announcementInformMenus[].regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 활동지역 (시/도)"),
                                        fieldWithPath("result.announcementInformMenus[].regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 활동지역 (시/군/구)"),

                                        // announcementDDay
                                        fieldWithPath("result.announcementInformMenus[].announcementDDay")
                                                .type(JsonFieldType.NUMBER)
                                                .description("공고 D-Day (마감까지 남은 일수)"),

                                        // announcementTitle
                                        fieldWithPath("result.announcementInformMenus[].announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 제목"),

                                        // isAnnouncementScrap
                                        fieldWithPath("result.announcementInformMenus[].isAnnouncementScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("공고 스크랩 여부"),

                                        // announcementScrapCount
                                        fieldWithPath("result.announcementInformMenus[].announcementScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("공고 스크랩된 총 횟수"),

                                        // announcementPositionItem
                                        fieldWithPath("result.announcementInformMenus[].announcementPositionItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("공고 포지션 정보 객체"),
                                        fieldWithPath("result.announcementInformMenus[].announcementPositionItem.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 대분류"),
                                        fieldWithPath("result.announcementInformMenus[].announcementPositionItem.subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 소분류"),

                                        // announcementSkillNames
                                        fieldWithPath("result.announcementInformMenus[].announcementSkillNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고에 필요한 스킬 목록"),
                                        fieldWithPath("result.announcementInformMenus[].announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("요구 스킬 이름")
                                )
                        )
                )
                .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AnnouncementInformMenus> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AnnouncementInformMenus>>() {
                }
        );

        final CommonResponse<AnnouncementInformMenus> expected = CommonResponse.onSuccess(announcementInformMenus);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
