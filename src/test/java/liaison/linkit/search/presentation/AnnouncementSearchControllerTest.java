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

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.business.service.AnnouncementSearchService;
import liaison.linkit.search.presentation.dto.announcement.AnnouncementListResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(AnnouncementSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AnnouncementSearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private AnnouncementSearchService announcementSearchService;

    private ResultActions performSearchAnnouncements(
            List<String> subPositions,
            List<String> cityNames,
            List<String> projectTypeNames,
            List<String> workTypeNames,
            AnnouncementSortType sortBy,
            CursorRequest cursorRequest)
            throws Exception {

        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/announcement/search");

        // 커서 페이징 요청이 있을 경우
        if (cursorRequest != null) {
            // size 설정
            requestBuilder.param("size", String.valueOf(cursorRequest.size()));

            // cursor가 존재한다면 cursor 파라미터 추가
            if (cursorRequest.cursor() != null) {
                requestBuilder.param("cursor", cursorRequest.cursor());
            }
        }

        // 파라미터가 비어있지 않다면 해당 파라미터를 추가
        if (subPositions != null && !subPositions.isEmpty()) {
            subPositions.forEach(subPosition -> requestBuilder.param("subPosition", subPosition));
        }
        if (cityNames != null && !cityNames.isEmpty()) {
            cityNames.forEach(cityName -> requestBuilder.param("cityName", cityName));
        }
        if (projectTypeNames != null && !projectTypeNames.isEmpty()) {
            projectTypeNames.forEach(
                    projectTypeName -> requestBuilder.param("projectTypeName", projectTypeName));
        }
        if (workTypeNames != null && !workTypeNames.isEmpty()) {
            workTypeNames.forEach(
                    workTypeName -> requestBuilder.param("workTypeName", workTypeName));
        }
        if (sortBy != null) {
            requestBuilder.param("sortBy", sortBy.name());
        }

        return mockMvc.perform(requestBuilder);
    }

    private ResultActions performFeaturedAnnouncements() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/announcement/search/featured");

        return mockMvc.perform(requestBuilder);
    }

    @DisplayName("회원/비회원이 팀원 공고를 검색할 수 있다. (필터링 가능)")
    @Test
    void searchAnnouncements() throws Exception {
        // given
        CursorRequest cursorRequest = new CursorRequest("1", 20);

        AnnouncementInformMenu announcementInformMenu1 =
                AnnouncementInformMenu.builder()
                        .teamMemberAnnouncementId(1L)
                        .teamLogoImagePath("팀 로고 이미지 경로")
                        .teamName("팀 이름 1")
                        .teamCode("팀 아이디 1(팀 코드)")
                        .teamScaleItem(
                                TeamScaleItem.builder().teamScaleName("팀 규모 이름 (1인)").build())
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("팀 활동지역 (시/도)")
                                        .divisionName("팀 활동지역 (시/군/구)")
                                        .build())
                        .announcementDDay(20)
                        .isClosed(false)
                        .isPermanentRecruitment(false)
                        .announcementTitle("공고 제목")
                        .isAnnouncementScrap(true)
                        .announcementScrapCount(100)
                        .viewCount(120L)
                        .createdAt("1시간 전")
                        .announcementPositionItem(
                                AnnouncementPositionItem.builder()
                                        .majorPosition("포지션 대분류")
                                        .subPosition("포지션 소분류")
                                        .build())
                        .announcementSkillNames(
                                Arrays.asList(
                                        AnnouncementSkillName.builder()
                                                .announcementSkillName("공고 요구 스킬 1")
                                                .build(),
                                        AnnouncementSkillName.builder()
                                                .announcementSkillName("공고 요구 스킬 2")
                                                .build()))
                        .build();

        AnnouncementInformMenu announcementInformMenu2 =
                AnnouncementInformMenu.builder()
                        .teamMemberAnnouncementId(2L)
                        .teamLogoImagePath("팀 로고 이미지 경로 2")
                        .teamName("팀 이름 2")
                        .teamCode("팀 아이디 2(팀 코드)")
                        .teamScaleItem(
                                TeamScaleItem.builder().teamScaleName("팀 규모 이름 (1인)").build())
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("팀 활동지역 (시/도)")
                                        .divisionName("팀 활동지역 (시/군/구)")
                                        .build())
                        .announcementDDay(20)
                        .isClosed(false)
                        .isPermanentRecruitment(false)
                        .announcementTitle("공고 제목 2")
                        .isAnnouncementScrap(true)
                        .announcementScrapCount(100)
                        .viewCount(120L)
                        .createdAt("1시간 전")
                        .announcementPositionItem(
                                AnnouncementPositionItem.builder()
                                        .majorPosition("포지션 대분류")
                                        .subPosition("포지션 소분류")
                                        .build())
                        .announcementSkillNames(
                                Arrays.asList(
                                        AnnouncementSkillName.builder()
                                                .announcementSkillName("공고 요구 스킬 1")
                                                .build(),
                                        AnnouncementSkillName.builder()
                                                .announcementSkillName("공고 요구 스킬 2")
                                                .build()))
                        .build();

        List<AnnouncementInformMenu> announcementInformMenus =
                Arrays.asList(announcementInformMenu1, announcementInformMenu2);

        CursorResponse<AnnouncementInformMenu> announcementInformMenuCursorResponse =
                CursorResponse.<AnnouncementInformMenu>builder()
                        .content(announcementInformMenus)
                        .nextCursor("nextAnnouncementId")
                        .hasNext(true)
                        .build();

        CursorResponse<AnnouncementInformMenu> cursorResponse =
                CursorResponse.of(announcementInformMenus, "nextAnnouncementId");

        // when
        when(announcementSearchService.searchAnnouncementsWithCursor(
                        any(), any(), any(), any(), any(), any(), any(CursorRequest.class)))
                .thenReturn(cursorResponse);

        final ResultActions resultActions =
                performSearchAnnouncements(
                        Arrays.asList("프론트엔드 개발자", "백엔드 개발자"),
                        Arrays.asList("서울특별시", "경기도"),
                        Arrays.asList("스터디", "창업·스타트업"),
                        Arrays.asList("대면", "비대면"),
                        AnnouncementSortType.LATEST,
                        cursorRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        queryParameters(
                                                parameterWithName("subPosition")
                                                        .optional()
                                                        .description("포지션 소분류 (선택사항)"),
                                                parameterWithName("cityName")
                                                        .optional()
                                                        .description("시/도 이름 (선택사항)"),
                                                parameterWithName("projectTypeName")
                                                        .optional()
                                                        .description("프로젝트 유형 (선택사항)"),
                                                parameterWithName("workTypeName")
                                                        .optional()
                                                        .description("업무 형태 이름 (선택사항)"),
                                                parameterWithName("sortBy")
                                                        .optional()
                                                        .description(
                                                                "1차 필터 적용 이후에 적용되는 내부 필터 (LATEST, POPULAR, DEADLINE (선택사항))"),
                                                parameterWithName("cursor")
                                                        .optional()
                                                        .description("마지막으로 조회한 팀의 ID (선택적)"),
                                                parameterWithName("size")
                                                        .optional()
                                                        .description("페이지 크기 (기본값: 20)")),
                                        // 예시: 실제 응답에 맞게 수정
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

                                                // result 내의 내용
                                                fieldWithPath("result.content[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀원 공고 목록"),
                                                fieldWithPath(
                                                                "result.content[].teamMemberAnnouncementId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀원 공고 ID"),
                                                fieldWithPath("result.content[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로"),
                                                fieldWithPath("result.content[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름"),
                                                fieldWithPath("result.content[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 코드"),
                                                fieldWithPath(
                                                                "result.content[].teamScaleItem.teamScaleName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 규모 이름"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 활동 지역 (시/도)"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 활동 지역 (시/군/구)"),
                                                fieldWithPath("result.content[].announcementDDay")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고 마감까지 남은 일수(디데이)"),
                                                fieldWithPath("result.content[].isClosed")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("공고 마감 여부"),
                                                fieldWithPath(
                                                                "result.content[].isPermanentRecruitment")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("상시 모집 여부"),
                                                fieldWithPath("result.content[].announcementTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("공고 제목"),
                                                fieldWithPath(
                                                                "result.content[].isAnnouncementScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("공고 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.content[].announcementScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고 스크랩 횟수"),
                                                fieldWithPath("result.content[].viewCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고 조회 수"),
                                                fieldWithPath("result.content[].createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("공고가 업로드 된 시간"),
                                                fieldWithPath(
                                                                "result.content[].announcementPositionItem.majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.content[].announcementPositionItem.subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 소분류"),
                                                fieldWithPath(
                                                                "result.content[].announcementSkillNames")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("공고 필요 스킬 목록"),
                                                fieldWithPath(
                                                                "result.content[].announcementSkillNames[].announcementSkillName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요구 스킬 이름"),

                                                // 페이지네이션 대신, Cursor 로직에 따른 필드
                                                fieldWithPath("result.nextCursor")
                                                        .type(JsonFieldType.STRING)
                                                        .description("다음 공고를 조회하기 위한 커서 값"),
                                                fieldWithPath("result.hasNext")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("다음 페이지(커서) 존재 여부"))))
                        .andReturn();
    }

    @DisplayName("회원/비회원이 정적 팀원 공고를 조회할 수 있다. (필터링 불가)")
    @Test
    void getFeaturedAnnouncements() throws Exception {
        // given
        AnnouncementListResponseDTO announcementListResponseDTO =
                AnnouncementListResponseDTO.builder()
                        .hotAnnouncements(
                                Arrays.asList(
                                        AnnouncementInformMenu.builder()
                                                .teamMemberAnnouncementId(1L)
                                                .teamLogoImagePath("팀 로고 이미지 경로")
                                                .teamName("팀 이름 1")
                                                .teamCode("팀 아이디 1(팀 코드)")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("팀 규모 이름 (1인)")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("팀 활동지역 (시/도)")
                                                                .divisionName("팀 활동지역 (시/군/구)")
                                                                .build())
                                                .announcementDDay(20)
                                                .isClosed(false)
                                                .isPermanentRecruitment(false)
                                                .announcementTitle("공고 제목")
                                                .isAnnouncementScrap(true)
                                                .announcementScrapCount(100)
                                                .viewCount(100L)
                                                .createdAt("1시간 전")
                                                .announcementPositionItem(
                                                        AnnouncementPositionItem.builder()
                                                                .majorPosition("포지션 대분류")
                                                                .subPosition("포지션 소분류")
                                                                .build())
                                                .announcementSkillNames(
                                                        Arrays.asList(
                                                                AnnouncementSkillName.builder()
                                                                        .announcementSkillName(
                                                                                "공고 요구 스킬 1")
                                                                        .build(),
                                                                AnnouncementSkillName.builder()
                                                                        .announcementSkillName(
                                                                                "공고 요구 스킬 2")
                                                                        .build()))
                                                .build(),
                                        AnnouncementInformMenu.builder()
                                                .teamMemberAnnouncementId(2L)
                                                .teamLogoImagePath("팀 로고 이미지 경로 2")
                                                .teamName("팀 이름 2")
                                                .teamCode("팀 아이디 2(팀 코드)")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("팀 규모 이름 (1인)")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("팀 활동지역 (시/도)")
                                                                .divisionName("팀 활동지역 (시/군/구)")
                                                                .build())
                                                .announcementDDay(20)
                                                .isClosed(false)
                                                .isPermanentRecruitment(false)
                                                .announcementTitle("공고 제목 2")
                                                .isAnnouncementScrap(true)
                                                .announcementScrapCount(100)
                                                .viewCount(120L)
                                                .createdAt("1시간 전")
                                                .announcementPositionItem(
                                                        AnnouncementPositionItem.builder()
                                                                .majorPosition("포지션 대분류")
                                                                .subPosition("포지션 소분류")
                                                                .build())
                                                .announcementSkillNames(
                                                        Arrays.asList(
                                                                AnnouncementSkillName.builder()
                                                                        .announcementSkillName(
                                                                                "공고 요구 스킬 1")
                                                                        .build(),
                                                                AnnouncementSkillName.builder()
                                                                        .announcementSkillName(
                                                                                "공고 요구 스킬 2")
                                                                        .build()))
                                                .build()))
                        .build();

        when(announcementSearchService.getFeaturedAnnouncements(any()))
                .thenReturn(announcementListResponseDTO);

        // when
        final ResultActions resultActions = performFeaturedAnnouncements();

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
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

                                                // 아래부터 실제 result 내부 필드 문서화
                                                fieldWithPath("result.hotAnnouncements[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("핫(Hot) 팀원 공고 목록"),

                                                // 배열 내부 객체 필드들
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].teamMemberAnnouncementId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀원 공고 ID(PK)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로"),
                                                fieldWithPath("result.hotAnnouncements[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름"),
                                                fieldWithPath("result.hotAnnouncements[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 코드 (고유 식별자)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].teamScaleItem.teamScaleName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 규모 이름 (예: '1인', '6~9인')"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 활동 지역 (시/도)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 활동 지역 (시/군/구)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementDDay")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고 마감까지 남은 일수 (디데이)"),
                                                fieldWithPath("result.hotAnnouncements[].isClosed")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("공고 마감 여부(true/false)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].isPermanentRecruitment")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("상시 모집 여부(true/false)"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("공고 제목"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].isAnnouncementScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("현재 사용자 기준 공고 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고가 스크랩된 총 횟수"),
                                                fieldWithPath("result.hotAnnouncements[].viewCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("공고 조회 수"),
                                                fieldWithPath("result.hotAnnouncements[].createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("공고가 업로드 된 시간"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementPositionItem.majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementPositionItem.subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 소분류"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementSkillNames")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("공고에 필요한 스킬 목록"),
                                                fieldWithPath(
                                                                "result.hotAnnouncements[].announcementSkillNames[].announcementSkillName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요구 스킬 이름"))))
                        .andReturn();
    }
}
