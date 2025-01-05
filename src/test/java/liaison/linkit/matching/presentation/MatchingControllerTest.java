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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteReceivedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteRequestedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateReceivedMatchingReadRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
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

    private ResultActions performGetSelectMatchingRequestToProfileMenu(final String emailId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/profile/{emailId}/select/request/menu", emailId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetSelectMatchingRequestToTeamMenu(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/{teamCode}/select/request/menu", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetReceivedMatchingMenu(ReceiverType receiverType, int page, int size) throws Exception {
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

    private ResultActions performGetMatchingRequestedMenu(SenderType senderType, int page, int size) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/requested/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .param("senderType", senderType.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    private ResultActions performUpdateReceivedMatchingRequestedStateRead(final UpdateReceivedMatchingReadRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/matching/received/menu/requested/read")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding("UTF-8")
        );
    }

    private ResultActions performUpdateReceivedMatchingCompletedStateRead(final UpdateReceivedMatchingReadRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/matching/received/menu/completed/read")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding("UTF-8")
        );
    }

    private ResultActions performDeleteReceivedMatchingItems(final DeleteReceivedMatchingRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/matching/received/menu/delete")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding("UTF-8")
        );
    }

    private ResultActions performDeleteRequestedMatchingItems(final DeleteRequestedMatchingRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/matching/requested/menu/delete")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding("UTF-8")
        );
    }

    @DisplayName("매칭 요청을 보낼 프로필의 정보 목록을 조회할 수 있다.")
    @Test
    void getSelectMatchingRequestToProfileMenu() throws Exception {

        // given
        final SelectMatchingRequestToProfileMenu selectMatchingRequestToProfileMenu = SelectMatchingRequestToProfileMenu.builder()
                .isTeamInformationExists(true)
                .senderProfileInformation(
                        SenderProfileInformation.builder()
                                .profileImagePath("발신자 프로필 이미지 경로")
                                .memberName("발신자 회원 이름")
                                .emailId("발신자 회원 유저 아이디")
                                .build()
                )
                .senderTeamInformation(
                        Arrays.asList(
                                SenderTeamInformation.builder()
                                        .teamCode("발신자 팀 아이디 1 (팀 코드)")
                                        .teamName("발신자 팀 이름 1")
                                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 1")
                                        .build(),
                                SenderTeamInformation.builder()
                                        .teamCode("발신자 팀 아이디 2 (팀 코드)")
                                        .teamName("발신자 팀 이름 2")
                                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 2")
                                        .build()
                        )
                )
                .receiverProfileInformation(
                        ReceiverProfileInformation.builder()
                                .profileImagePath("수신자 프로필 이미지 경로")
                                .memberName("수신자 회원 이름")
                                .emailId("수신자 회원 유저 아이디")
                                .build()
                )
                .build();

        // when
        when(matchingService.selectMatchingRequestToProfileMenu(anyLong(), any())).thenReturn(selectMatchingRequestToProfileMenu);

        final ResultActions resultActions = performGetSelectMatchingRequestToProfileMenu("liaison");

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
                                                .description("결과 데이터"),
                                        fieldWithPath("result.isTeamInformationExists")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 정보가 존재하는지 여부"),
                                        fieldWithPath("result.senderProfileInformation")
                                                .type(JsonFieldType.OBJECT)
                                                .description("발신자 프로필 정보"),
                                        fieldWithPath("result.senderProfileInformation.profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 프로필 이미지 경로"),
                                        fieldWithPath("result.senderProfileInformation.memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 회원 이름"),
                                        fieldWithPath("result.senderProfileInformation.emailId")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 회원 유저 아이디"),
                                        fieldWithPath("result.senderProfileInformation.profilePositionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("발신자 프로필 포지션 상세 정보"),
                                        fieldWithPath("result.senderProfileInformation.profilePositionDetail.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .optional()
                                                .description("발신자 프로필 대분류 포지션 (없을 수 있음)"),
                                        fieldWithPath("result.senderTeamInformation")
                                                .type(JsonFieldType.ARRAY)
                                                .description("발신자 팀 정보"),
                                        fieldWithPath("result.senderTeamInformation[].teamCode")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 팀 아이디 (팀 코드)"),
                                        fieldWithPath("result.senderTeamInformation[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 팀 이름"),
                                        fieldWithPath("result.senderTeamInformation[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 팀 로고 이미지 경로"),
                                        fieldWithPath("result.receiverProfileInformation")
                                                .type(JsonFieldType.OBJECT)
                                                .description("수신자 프로필 정보"),
                                        fieldWithPath("result.receiverProfileInformation.profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 프로필 이미지 경로"),
                                        fieldWithPath("result.receiverProfileInformation.memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 회원 이름"),
                                        fieldWithPath("result.receiverProfileInformation.emailId")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 회원 유저 아이디"),
                                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("수신자 프로필 포지션 상세 정보"),
                                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .optional()
                                                .description("수신자 프로필 대분류 포지션 (없을 수 있음)")
                                )
                        )
                ).andReturn();
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
        when(matchingService.getMatchingNotificationMenu(any())).thenReturn(matchingMenu);

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
    void getReceivedMatchingMenu() throws Exception {
        ReceivedMatchingMenu receivedMatchingMenu1 = ReceivedMatchingMenu.builder()
                .matchingId(1L)
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

        ReceivedMatchingMenu receivedMatchingMenu2 = ReceivedMatchingMenu.builder()
                .matchingId(2L)
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

        List<ReceivedMatchingMenu> receivedMatchingMenus = Arrays.asList(receivedMatchingMenu1, receivedMatchingMenu2);
        Page<ReceivedMatchingMenu> matchingReceivedMenusPage = new PageImpl<>(receivedMatchingMenus, PageRequest.of(0, 20), receivedMatchingMenus.size());

        when(matchingService.getReceivedMatchingMenuResponse(anyLong(), any(), any(Pageable.class))).thenReturn(matchingReceivedMenusPage);

        final ResultActions resultActions = performGetReceivedMatchingMenu(
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

                                        fieldWithPath("result.content[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("매칭 ID (PK)"),

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

    @DisplayName("매칭 관리 발신함의 정보를 조회할 수 있다.")
    @Test
    void getRequestedMatchingMenu() throws Exception {
        // given
        RequestedMatchingMenu requestedMatchingMenu1 = RequestedMatchingMenu.builder()
                .matchingId(1L)
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

        RequestedMatchingMenu requestedMatchingMenu2 = RequestedMatchingMenu.builder()
                .matchingId(2L)
                .senderType(SenderType.PROFILE)
                .receiverType(ReceiverType.PROFILE)

                .senderEmailId("발신자 유저 아이디")
                .senderTeamCode("발신자 팀 아이디")
                .receiverEmailId("수신자 유저 아이디")
                .receiverTeamCode("수신자 팀 아이디")
                .receiverAnnouncementId(2L)

                .requestMessage("매칭 요청 메시지")

                .matchingStatusType(MatchingStatusType.REQUESTED)
                .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
                .build();

        List<RequestedMatchingMenu> requestedMatchingMenus = Arrays.asList(requestedMatchingMenu1, requestedMatchingMenu2);
        Page<RequestedMatchingMenu> matchingReceivedMenus = new PageImpl<>(requestedMatchingMenus, PageRequest.of(0, 20), requestedMatchingMenus.size());

        // when
        when(matchingService.getRequestedMatchingMenuResponse(anyLong(), any(), any(Pageable.class))).thenReturn(matchingReceivedMenus);

        final ResultActions resultActions = performGetMatchingRequestedMenu(
                SenderType.PROFILE,
                0,
                20
        );

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("senderType")
                                                .optional()
                                                .description("발신자 타입 (선택적) - [PROFILE, TEAM] 2개"),
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

                                        fieldWithPath("result.content[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("매칭 ID (PK)"),

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

    @DisplayName("매칭 관리 수신함에서 Requested 상태 매칭을 읽음 처리할 수 있다.")
    @Test
    void updateReceivedMatchingRequestedStateRead() throws Exception {
        // given
        final UpdateReceivedMatchingReadRequest request = UpdateReceivedMatchingReadRequest.builder()
                .matchingIds(Arrays.asList(
                        1L,
                        2L
                ))
                .build();

        final UpdateReceivedMatchingRequestedStateToReadItems updateReceivedMatchingRequestedStateToReadItems
                = UpdateReceivedMatchingRequestedStateToReadItems.builder()
                .updateReceivedMatchingRequestedStateToReadItems(Arrays.asList(
                        UpdateReceivedMatchingRequestedStateToReadItem.builder()
                                .matchingId(1L)
                                .receiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING)
                                .build(),
                        UpdateReceivedMatchingRequestedStateToReadItem.builder()
                                .matchingId(2L)
                                .receiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING)
                                .build()
                ))
                .build();

        // when
        when(matchingService.updateReceivedMatchingRequestedStateToRead(anyLong(), any())).thenReturn(updateReceivedMatchingRequestedStateToReadItems);

        final ResultActions resultActions = performUpdateReceivedMatchingRequestedStateRead(request);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("matchingIds")
                                                .type(JsonFieldType.ARRAY)
                                                .description("매칭 ID 목록. 읽음 처리할 매칭 (MatchingStatusType -> REQUESTED && ReceiverReadStatus -> UNREAD_REQUESTED_MATCHING)인 항목들")
                                ),
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 데이터"),
                                        fieldWithPath("result.updateReceivedMatchingRequestedStateToReadItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("업데이트된 매칭 항목 목록"),
                                        fieldWithPath("result.updateReceivedMatchingRequestedStateToReadItems[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("업데이트된 매칭 ID"),
                                        fieldWithPath("result.updateReceivedMatchingRequestedStateToReadItems[].receiverReadStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("업데이트된 매칭의 읽음 상태")
                                ))).andReturn();
    }

    @DisplayName("매칭 관리 수신함에서 Completed 상태 매칭을 읽음 처리할 수 있다.")
    @Test
    void updateReceivedMatchingCompletedStateRead() throws Exception {
        // given
        final UpdateReceivedMatchingReadRequest request = UpdateReceivedMatchingReadRequest.builder()
                .matchingIds(Arrays.asList(
                        1L,
                        2L
                ))
                .build();

        final UpdateReceivedMatchingCompletedStateReadItems updateReceivedMatchingCompletedStateReadItems
                = UpdateReceivedMatchingCompletedStateReadItems.builder()
                .updateReceivedMatchingCompletedStateReadItems(Arrays.asList(
                        UpdateReceivedMatchingCompletedStateReadItem.builder()
                                .matchingId(1L)
                                .receiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING)
                                .build(),
                        UpdateReceivedMatchingCompletedStateReadItem.builder()
                                .matchingId(2L)
                                .receiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING)
                                .build()
                ))
                .build();

        // when
        when(matchingService.updateReceivedMatchingCompletedStateToRead(anyLong(), any())).thenReturn(updateReceivedMatchingCompletedStateReadItems);

        final ResultActions resultActions = performUpdateReceivedMatchingCompletedStateRead(request);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("matchingIds")
                                                .type(JsonFieldType.ARRAY)
                                                .description("매칭 ID 목록. 읽음 처리할 매칭 (MatchingStatusType -> COMPLETED && ReceiverReadStatus -> UNREAD_COMPLETED_MATCHING)인 항목들")
                                ),
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 데이터"),
                                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("업데이트된 매칭 항목 목록"),
                                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("업데이트된 매칭 ID"),
                                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems[].receiverReadStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("업데이트된 매칭의 읽음 상태")
                                ))).andReturn();
    }

    @DisplayName("매칭 관리 수신함에서 매칭을 삭제 처리할 수 있다.")
    @Test
    void deleteReceivedMatchingItems() throws Exception {
        // given
        final DeleteReceivedMatchingRequest request = DeleteReceivedMatchingRequest.builder()
                .matchingIds(Arrays.asList(
                        1L,
                        2L
                ))
                .build();

        final DeleteReceivedMatchingItems deleteReceivedMatchingItems = DeleteReceivedMatchingItems.builder()
                .deleteReceivedMatchingItems(Arrays.asList(
                        DeleteReceivedMatchingItem.builder()
                                .matchingId(1L)
                                .receiverDeleteStatus(ReceiverDeleteStatus.DELETED)
                                .build(),
                        DeleteReceivedMatchingItem.builder()
                                .matchingId(2L)
                                .receiverDeleteStatus(ReceiverDeleteStatus.DELETED)
                                .build()
                ))
                .build();

        // when
        when(matchingService.deleteReceivedMatchingItems(anyLong(), any())).thenReturn(deleteReceivedMatchingItems);

        final ResultActions resultActions = performDeleteReceivedMatchingItems(request);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("matchingIds")
                                                .type(JsonFieldType.ARRAY)
                                                .description("삭제 처리할 매칭 ID 목록")
                                ),
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 데이터"),
                                        fieldWithPath("result.deleteReceivedMatchingItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("삭제된 매칭 항목 목록"),
                                        fieldWithPath("result.deleteReceivedMatchingItems[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("삭제된 매칭 ID"),
                                        fieldWithPath("result.deleteReceivedMatchingItems[].receiverDeleteStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("삭제된 매칭의 삭제 상태")
                                ))).andReturn();
    }

    @DisplayName("매칭 관리 발신함에서 매칭을 삭제 처리할 수 있다.")
    @Test
    void deleteRequestedMatchingItems() throws Exception {
        // given
        final DeleteRequestedMatchingRequest request = DeleteRequestedMatchingRequest.builder()
                .matchingIds(Arrays.asList(
                        1L,
                        2L
                ))
                .build();

        final DeleteRequestedMatchingItems deleteRequestedMatchingItems = DeleteRequestedMatchingItems.builder()
                .deleteRequestedMatchingItems(Arrays.asList(
                        DeleteRequestedMatchingItem.builder()
                                .matchingId(1L)
                                .senderDeleteStatus(SenderDeleteStatus.DELETED)
                                .build(),
                        DeleteRequestedMatchingItem.builder()
                                .matchingId(2L)
                                .senderDeleteStatus(SenderDeleteStatus.DELETED)
                                .build()
                ))
                .build();
        // when
        when(matchingService.deleteRequestedMatchingItems(anyLong(), any())).thenReturn(deleteRequestedMatchingItems);

        final ResultActions resultActions = performDeleteRequestedMatchingItems(request);
        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("matchingIds")
                                                .type(JsonFieldType.ARRAY)
                                                .description("삭제 처리할 매칭 ID 목록")
                                ),
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 데이터"),
                                        fieldWithPath("result.deleteRequestedMatchingItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("삭제된 매칭 항목 목록"),
                                        fieldWithPath("result.deleteRequestedMatchingItems[].matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("삭제된 매칭 ID"),
                                        fieldWithPath("result.deleteRequestedMatchingItems[].senderDeleteStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("삭제된 매칭의 삭제 상태")
                                ))).andReturn();
    }
}
