package liaison.linkit.matching.presentation;

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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingReceivedMenu;
import liaison.linkit.matching.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(MatchingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MatchingControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetMatchingNotificationMenu() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/notification/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetMatchingReceivedMenu(
            ReceiverType receiverType,
            int page,
            int size
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/received/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .param("receiverType", receiverType.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    @DisplayName("매칭 관리 상단 정보를 조회할 수 있다.")
    @Test
    void getMatchingMenu() throws Exception {
        // given
        final MatchingMenu matchingMenu = MatchingMenu.builder()
                .receivedMatchingNotificationCount(10)
                .requestedMatchingNotificationCount(12)
                .build();

        // when
        when(matchingService.getMatchingMenu(any())).thenReturn(matchingMenu);

        final ResultActions resultActions = performGetMatchingNotificationMenu();

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
                                        subsectionWithPath("result.receivedMatchingNotificationCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("수신함 안읽은 알림 개수"),
                                        subsectionWithPath("result.requestedMatchingNotificationCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("발신함 안읽은 알림 개수")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MatchingMenu> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<MatchingMenu>>() {
                }
        );

        final CommonResponse<MatchingMenu> expected = CommonResponse.onSuccess(matchingMenu);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("매칭 관리 수신함의 정보를 조회할 수 있다.")
    @Test
    void getMatchingReceivedMenu() throws Exception {
        MatchingReceivedMenu matchingReceivedMenu1 = MatchingReceivedMenu.builder()
                .senderType(SenderType.PROFILE)
                .receiverType(ReceiverType.PROFILE)

                .senderEmailId("발신자 유저 아이디")
                .senderTeamCode("발신자 팀 아이디")
                .receiverEmailId("수신자 유저 아이디")
                .receiverTeamCode("수신자 팀 아이디")
                .receiverAnnouncementId(1L)

                .requestMessage("매칭 요청 메시지")

                .matchingStatusType(MatchingStatusType.REQUESTED)
                .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)

                .build();

        MatchingReceivedMenu matchingReceivedMenu2 = MatchingReceivedMenu.builder()
                .senderType(SenderType.TEAM)
                .receiverType(ReceiverType.PROFILE)

                .senderEmailId("발신자 유저 아이디")
                .senderTeamCode("발신자 팀 아이디")
                .receiverEmailId("수신자 유저 아이디")
                .receiverTeamCode("수신자 팀 아이디")
                .receiverAnnouncementId(2L)

                .requestMessage("매칭 요청 메시지")

                .matchingStatusType(MatchingStatusType.COMPLETED)
                .receiverReadStatus(ReceiverReadStatus.READ_COMPLETED_MATCHING)

                .build();

        List<MatchingReceivedMenu> matchingReceivedMenus = Arrays.asList(matchingReceivedMenu1, matchingReceivedMenu2);
        Page<MatchingReceivedMenu> matchingReceivedMenusPage = new PageImpl<>(matchingReceivedMenus, PageRequest.of(0, 20), matchingReceivedMenus.size());

        when(matchingService.getMatchingReceivedMenuResponse(anyLong(), any(), any(Pageable.class))).thenReturn(matchingReceivedMenusPage);

        final ResultActions resultActions = performGetMatchingReceivedMenu(
                ReceiverType.PROFILE,
                0,
                20
        );

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("receiverType")
                                                .optional()
                                                .description("수신자 타입 (선택적) - [PROFILE, TEAM, ANNOUNCEMENT] 3개"),
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
                                        fieldWithPath("result.content")
                                                .type(JsonFieldType.ARRAY)
                                                .description("매칭 요청 목록"),
                                        fieldWithPath("result.content[].senderType")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 타입 - [PROFILE, TEAM]"),
                                        fieldWithPath("result.content[].receiverType")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 타입 - [PROFILE, TEAM, ANNOUNCEMENT]"),
                                        fieldWithPath("result.content[].senderEmailId")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 유저 아이디"),
                                        fieldWithPath("result.content[].senderTeamCode")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 팀 아이디"),
                                        fieldWithPath("result.content[].receiverEmailId")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 유저 아이디"),
                                        fieldWithPath("result.content[].receiverTeamCode")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 팀 아이디"),
                                        fieldWithPath("result.content[].receiverAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("수신자 공고 ID"),
                                        fieldWithPath("result.content[].requestMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 요청 메시지"),
                                        fieldWithPath("result.content[].matchingStatusType")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 상태 타입 - [REQUESTED, COMPLETED 등]"),
                                        fieldWithPath("result.content[].receiverReadStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 읽음 상태 - [UNREAD_REQUESTED_MATCHING, READ_COMPLETED_MATCHING 등]"),
                                        fieldWithPath("result.pageable")
                                                .type(JsonFieldType.OBJECT)
                                                .description("페이지 정보"),

                                        fieldWithPath("result.pageable.sort")
                                                .type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("result.pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("result.pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않은 상태인지 여부"),
                                        fieldWithPath("result.pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬된 상태인지 여부"),
                                        fieldWithPath("result.sort")
                                                .type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("result.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보가 비어 있는지 여부"),
                                        fieldWithPath("result.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않은 상태인지 여부"),
                                        fieldWithPath("result.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬된 상태인지 여부"),

                                        fieldWithPath("result.pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 시작 오프셋"),
                                        fieldWithPath("result.pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이징 여부"),
                                        fieldWithPath("result.pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("비페이징 여부"),

                                        fieldWithPath("result.totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 페이지 수"),
                                        fieldWithPath("result.totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 요소 수"),
                                        fieldWithPath("result.last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("마지막 페이지 여부"),
                                        fieldWithPath("result.first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("첫 번째 페이지 여부"),
                                        fieldWithPath("result.size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 요소 수"),
                                        fieldWithPath("result.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("결과가 비어 있는지 여부")

                                )
                        )
                )
                .andReturn();
    }

}
