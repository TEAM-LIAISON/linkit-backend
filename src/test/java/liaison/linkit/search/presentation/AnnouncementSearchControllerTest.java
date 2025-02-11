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
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
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

@WebMvcTest(AnnouncementSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AnnouncementSearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnnouncementSearchService announcementSearchService;

    private ResultActions performSearchAnnouncements(
        List<String> majorPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> scaleName,
        int page,
        int size
    ) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/announcement/search")
                .param("majorPosition", majorPosition.toArray(new String[0]))
                .param("skillName", skillName.toArray(new String[0]))
                .param("cityName", cityName.toArray(new String[0]))
                .param("scaleName", scaleName.toArray(new String[0]))
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        );
    }

    @DisplayName("회원/비회원이 팀원 공고를 검색할 수 있다. (필터링 가능)")
    @Test
    void searchAnnouncements() throws Exception {
        // given
        AnnouncementInformMenu announcementInformMenu1 = AnnouncementInformMenu.builder()
            .teamMemberAnnouncementId(1L)
            .teamLogoImagePath("팀 로고 이미지 경로")
            .teamName("팀 이름 1")
            .teamCode("팀 아이디 1(팀 코드)")
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
            .isPermanentRecruitment(false)
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
            .build();

        AnnouncementInformMenu announcementInformMenu2 = AnnouncementInformMenu.builder()
            .teamMemberAnnouncementId(2L)
            .teamLogoImagePath("팀 로고 이미지 경로 2")
            .teamName("팀 이름 2")
            .teamCode("팀 아이디 2(팀 코드)")
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
            .isPermanentRecruitment(false)
            .announcementTitle("공고 제목 2")
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
            .build();

        List<AnnouncementInformMenu> announcementInformMenus = Arrays.asList(announcementInformMenu1, announcementInformMenu2);
        Page<AnnouncementInformMenu> announcementInformPage = new PageImpl<>(announcementInformMenus, PageRequest.of(0, 20), announcementInformMenus.size());

        // when
        when(announcementSearchService.searchAnnouncements(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(Pageable.class)
        )).thenReturn(announcementInformPage);

        final ResultActions resultActions = performSearchAnnouncements(
            Arrays.asList("개발자"),
            Arrays.asList("Java", "Spring"),
            Arrays.asList("서울특별시"),
            Arrays.asList("1인"),
            0,
            20
        );

        // then
        final MvcResult mvcResult = resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
            .andDo(
                restDocs.document(
                    queryParameters(
                        parameterWithName("majorPosition")
                            .optional()
                            .description("포지션 대분류 (선택사항)"),
                        parameterWithName("skillName")
                            .optional()
                            .description("스킬 이름 (선택사항)"),
                        parameterWithName("cityName")
                            .optional()
                            .description("시/도 이름 (선택사항)"),
                        parameterWithName("scaleName")
                            .optional()
                            .description("팀 규모 이름 (선택사항)"),
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

                        // result
                        fieldWithPath("result.content")
                            .type(JsonFieldType.ARRAY)
                            .description("팀 공고 정보 목록"),
                        fieldWithPath("result.content[].teamMemberAnnouncementId")
                            .type(JsonFieldType.NUMBER)
                            .description("팀원 공고 ID PK"),
                        fieldWithPath("result.content[].teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("팀 로고 이미지 경로"),
                        fieldWithPath("result.content[].teamName")
                            .type(JsonFieldType.STRING)
                            .description("팀 이름"),
                        fieldWithPath("result.content[].teamCode")
                            .type(JsonFieldType.STRING)
                            .description("팀 아이디 (팀 코드)"),
                        fieldWithPath("result.content[].teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("팀 규모 이름"),
                        fieldWithPath("result.content[].regionDetail.cityName")
                            .type(JsonFieldType.STRING)
                            .description("팀 활동 지역 (시/도)"),
                        fieldWithPath("result.content[].regionDetail.divisionName")
                            .type(JsonFieldType.STRING)
                            .description("팀 활동 지역 (시/군/구)"),

                        fieldWithPath("result.content[].announcementDDay")
                            .type(JsonFieldType.NUMBER)
                            .description("공고 마감까지 남은 일수 (디데이)"),
                        fieldWithPath("result.content[].isPermanentRecruitment")
                            .type(JsonFieldType.BOOLEAN)
                            .description("상시 모집 여부 (false이면 상시 모집하지 않음)"),
                        fieldWithPath("result.content[].announcementTitle")
                            .type(JsonFieldType.STRING)
                            .description("공고 제목"),

                        fieldWithPath("result.content[].isAnnouncementScrap")
                            .type(JsonFieldType.BOOLEAN)
                            .description("현재 사용자가 이 공고를 스크랩했는지 여부"),
                        fieldWithPath("result.content[].announcementScrapCount")
                            .type(JsonFieldType.NUMBER)
                            .description("공고가 스크랩된 총 횟수"),

                        fieldWithPath("result.content[].announcementPositionItem.majorPosition")
                            .type(JsonFieldType.STRING)
                            .description("포지션 대분류"),
                        fieldWithPath("result.content[].announcementPositionItem.subPosition")
                            .type(JsonFieldType.STRING)
                            .description("포지션 소분류"),

                        fieldWithPath("result.content[].announcementSkillNames")
                            .type(JsonFieldType.ARRAY)
                            .description("공고에 필요한 스킬 목록"),
                        fieldWithPath("result.content[].announcementSkillNames[].announcementSkillName")
                            .type(JsonFieldType.STRING)
                            .description("요구 스킬 이름"),

                        // 페이징 정보
                        fieldWithPath("result.pageable")
                            .type(JsonFieldType.OBJECT)
                            .description("페이징 상세 정보"),
                        fieldWithPath("result.pageable.pageNumber")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호"),
                        fieldWithPath("result.pageable.pageSize")
                            .type(JsonFieldType.NUMBER)
                            .description("페이지 크기"),
                        fieldWithPath("result.pageable.sort")
                            .type(JsonFieldType.OBJECT)
                            .description("정렬 정보"),
                        fieldWithPath("result.pageable.sort.empty")
                            .type(JsonFieldType.BOOLEAN)
                            .description("정렬 기준이 비어있는지 여부"),
                        fieldWithPath("result.pageable.sort.sorted")
                            .type(JsonFieldType.BOOLEAN)
                            .description("정렬이 적용되었는지 여부"),
                        fieldWithPath("result.pageable.sort.unsorted")
                            .type(JsonFieldType.BOOLEAN)
                            .description("정렬이 적용되지 않았는지 여부"),
                        fieldWithPath("result.pageable.offset")
                            .type(JsonFieldType.NUMBER)
                            .description("해당 페이지의 시작 데이터 위치"),
                        fieldWithPath("result.pageable.paged")
                            .type(JsonFieldType.BOOLEAN)
                            .description("페이징 방식 적용 여부"),
                        fieldWithPath("result.pageable.unpaged")
                            .type(JsonFieldType.BOOLEAN)
                            .description("페이징이 적용되지 않았는지 여부"),

                        fieldWithPath("result.last")
                            .type(JsonFieldType.BOOLEAN)
                            .description("마지막 페이지인지 여부"),
                        fieldWithPath("result.totalPages")
                            .type(JsonFieldType.NUMBER)
                            .description("전체 페이지 수"),
                        fieldWithPath("result.totalElements")
                            .type(JsonFieldType.NUMBER)
                            .description("전체 데이터 개수"),
                        fieldWithPath("result.first")
                            .type(JsonFieldType.BOOLEAN)
                            .description("첫 페이지인지 여부"),
                        fieldWithPath("result.size")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 페이지 크기"),
                        fieldWithPath("result.number")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 페이지 번호"),
                        fieldWithPath("result.sort")
                            .type(JsonFieldType.OBJECT)
                            .description("전체 정렬 정보 (페이지 단위)"),
                        fieldWithPath("result.sort.empty")
                            .type(JsonFieldType.BOOLEAN)
                            .description("전체 정렬 기준이 비어있는지 여부"),
                        fieldWithPath("result.sort.sorted")
                            .type(JsonFieldType.BOOLEAN)
                            .description("전체 정렬이 적용되었는지 여부"),
                        fieldWithPath("result.sort.unsorted")
                            .type(JsonFieldType.BOOLEAN)
                            .description("전체 정렬이 적용되지 않았는지 여부"),
                        fieldWithPath("result.numberOfElements")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 페이지에 조회된 데이터 개수"),
                        fieldWithPath("result.empty")
                            .type(JsonFieldType.BOOLEAN)
                            .description("현재 페이지가 비어있는지 여부")
                    )
                )
            ).andReturn();

    }


}
