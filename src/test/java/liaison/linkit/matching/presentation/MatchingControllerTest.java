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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
import liaison.linkit.matching.business.service.ReceiveMatchingService;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.AddMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteReceivedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteRequestedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateMatchingStatusTypeRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateReceivedMatchingReadRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingNotificationMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateMatchingStatusTypeResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.business.service.MatchingService;
import liaison.linkit.matching.business.service.SendMatchingService;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
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

    @MockBean
    private SendMatchingService sendMatchingService;

    @MockBean
    private ReceiveMatchingService receiveMatchingService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performPostAddMatching(final AddMatchingRequest addMatchingRequest) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/matching")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addMatchingRequest))
        );
    }

    private ResultActions performPostUpdateMatchingStatusType(final Long matchingId, final UpdateMatchingStatusTypeRequest updateMatchingStatusTypeRequest) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/matching/{matchingId}", matchingId)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMatchingStatusTypeRequest))
        );
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
            RestDocumentationRequestBuilders.get("/api/v1/matching/team/{teamCode}/select/request/menu", teamCode)
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

    private ResultActions performUpdateReceivedMatchingStateRead(final UpdateReceivedMatchingReadRequest request) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/matching/received/menu/read")
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

    @DisplayName("매칭 요청을 보낼 수 있다.")
    @Test
    void addMatching() throws Exception {
        // given
        final AddMatchingRequest addMatchingRequest = AddMatchingRequest.builder()
            .senderType(SenderType.PROFILE)
            .receiverType(ReceiverType.PROFILE)
            .senderEmailId("발신자 유저 아이디")
            .senderTeamCode("발신자 팀 아이디 (팀 코드)")
            .receiverEmailId("수신자 유저 아이디")
            .receiverTeamCode("수신자 팀 아이디 (팀 코드)")
            .receiverAnnouncementId(1L)
            .requestMessage("매칭 요청 메시지입니다.")
            .build();

        final MatchingResponseDTO.AddMatchingResponse addMatchingResponse = AddMatchingResponse.builder()
            .matchingId(1L)
            .senderType(SenderType.PROFILE)
            .receiverType(ReceiverType.PROFILE)
            .isChatRoomCreated(false)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 이름")
                    .emailId("발신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                SenderTeamInformation.builder()
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로")
                    .teamCode("발신자 팀 아이디 (팀 코드)")
                    .teamName("발신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("발신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 이름")
                    .emailId("수신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamLogoImagePath("수신자 팀 로고 이미지 경로")
                    .teamCode("수신자 팀 아이디 (팀 코드)")
                    .teamName("수신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("수신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverAnnouncementInformation(
                ReceiverAnnouncementInformation.builder()
                    .teamMemberAnnouncementId(1L)
                    .teamName("팀 이름")
                    .announcementTitle("공고 제목")
                    .teamLogoImagePath("팀 로고 이미지 경로")
                    .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                            .majorPosition("공고 포지션 대분류")
                            .subPosition("공고 포지션 소분류")
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
            )
            .requestMessage("매칭 요청 메시지")
            .matchingStatusType(MatchingStatusType.REQUESTED)
            .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
            .build();
        // when
        when(sendMatchingService.addMatching(anyLong(), any())).thenReturn(addMatchingResponse);

        final ResultActions resultActions = performPostAddMatching(addMatchingRequest);

        // then
        final MvcResult mvcResult = resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
            .andDo(
                restDocs.document(
                    requestFields(
                        fieldWithPath("senderType")
                            .type(JsonFieldType.STRING)
                            .description("발신자 타입 - [PROFILE, TEAM]")
                            .attributes(field("constraint", "PROFILE 또는 TEAM")),
                        fieldWithPath("receiverType")
                            .type(JsonFieldType.STRING)
                            .description("수신자 타입 - [PROFILE, TEAM, ANNOUNCEMENT]")
                            .attributes(field("constraint", "PROFILE, TEAM 또는 ANNOUNCEMENT")),
                        fieldWithPath("senderEmailId")
                            .type(JsonFieldType.STRING)
                            .description("발신자 유저 아이디")
                            .optional(),
                        fieldWithPath("senderTeamCode")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 아이디")
                            .optional(),
                        fieldWithPath("receiverEmailId")
                            .type(JsonFieldType.STRING)
                            .description("수신자 유저 아이디")
                            .optional(),
                        fieldWithPath("receiverTeamCode")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 아이디")
                            .optional(),
                        fieldWithPath("receiverAnnouncementId")
                            .type(JsonFieldType.NUMBER)
                            .description("수신자 공고 아이디")
                            .optional(),
                        fieldWithPath("requestMessage")
                            .type(JsonFieldType.STRING)
                            .description("매칭 요청 메시지")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess")
                            .type(JsonFieldType.BOOLEAN)
                            .description("요청 성공 여부"),
                        fieldWithPath("code")
                            .type(JsonFieldType.STRING)
                            .description("응답 코드"),
                        fieldWithPath("message")
                            .type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("result.matchingId")
                            .type(JsonFieldType.NUMBER)
                            .description("매칭 ID (PK)"),

                        fieldWithPath("result.isChatRoomCreated")
                            .type(JsonFieldType.BOOLEAN)
                            .description("채팅방 생성 여부"),

                        fieldWithPath("result.senderType")
                            .type(JsonFieldType.STRING)
                            .description("발신자 타입 - [PROFILE, TEAM]"),
                        fieldWithPath("result.receiverType")
                            .type(JsonFieldType.STRING)
                            .description("수신자 타입 - [PROFILE, TEAM, ANNOUNCEMENT]"),

                        // ----- senderProfileInformation
                        fieldWithPath("result.senderProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 정보"),
                        fieldWithPath("result.senderProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지 경로"),
                        fieldWithPath("result.senderProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 이름"),
                        fieldWithPath("result.senderProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("발신자 유저 아이디"),
                        fieldWithPath("result.senderProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 포지션 상세 정보"),
                        fieldWithPath("result.senderProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.senderProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 소분류 (null 가능)"),

                        // ----- senderTeamInformation
                        fieldWithPath("result.senderTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 정보"),
                        fieldWithPath("result.senderTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.senderTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 이름"),
                        fieldWithPath("result.senderTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.senderTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 규모 정보"),
                        fieldWithPath("result.senderTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 규모명"),

                        // ----- receiverProfileInformation
                        fieldWithPath("result.receiverProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 정보"),
                        fieldWithPath("result.receiverProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 프로필 이미지 경로"),
                        fieldWithPath("result.receiverProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 이름"),
                        fieldWithPath("result.receiverProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("수신자 유저 아이디"),
                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 포지션 상세 정보"),
                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 소분류 (null 가능)"),

                        // ----- receiverTeamInformation
                        fieldWithPath("result.receiverTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 정보"),
                        fieldWithPath("result.receiverTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.receiverTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 이름"),
                        fieldWithPath("result.receiverTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.receiverTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 규모 정보"),
                        fieldWithPath("result.receiverTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 규모명"),

                        // ----- receiverAnnouncementInformation 추가
                        fieldWithPath("result.receiverAnnouncementInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 공고 정보"),

                        fieldWithPath("result.receiverAnnouncementInformation.teamMemberAnnouncementId")
                            .type(JsonFieldType.NUMBER)
                            .description("공고 아이디"),
                        fieldWithPath("result.receiverAnnouncementInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 이름"),

                        fieldWithPath("result.receiverAnnouncementInformation.announcementTitle")
                            .type(JsonFieldType.STRING)
                            .description("공고 제목"),

                        fieldWithPath("result.receiverAnnouncementInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 로고 이미지 경로"),

                        fieldWithPath("result.receiverAnnouncementInformation.announcementPositionItem")
                            .type(JsonFieldType.OBJECT)
                            .description("공고 포지션 정보"),
                        fieldWithPath("result.receiverAnnouncementInformation.announcementPositionItem.majorPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 대분류"),
                        fieldWithPath("result.receiverAnnouncementInformation.announcementPositionItem.subPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 소분류"),
                        fieldWithPath("result.receiverAnnouncementInformation.announcementSkillNames")
                            .type(JsonFieldType.ARRAY)
                            .description("공고 요구 스킬 목록"),
                        fieldWithPath("result.receiverAnnouncementInformation.announcementSkillNames[].announcementSkillName")
                            .type(JsonFieldType.STRING)
                            .description("공고 요구 스킬 이름"),

                        // ----- 추가 필드
                        fieldWithPath("result.requestMessage")
                            .type(JsonFieldType.STRING)
                            .description("매칭 요청 메시지"),
                        fieldWithPath("result.matchingStatusType")
                            .type(JsonFieldType.STRING)
                            .description("매칭 상태 - [REQUESTED, COMPLETED 등]"),
                        fieldWithPath("result.receiverReadStatus")
                            .type(JsonFieldType.STRING)
                            .description("수신자 읽음 상태 - [UNREAD_REQUESTED_MATCHING, READ_COMPLETED_MATCHING 등]")
                    )
                )
            ).andReturn();
    }

    @DisplayName("매칭 요청에 대해 수신자가 수락하거나 거절할 수 있다.")
    @Test
    void updateMatchingStatusTypeResponse() throws Exception {
        // given
        final UpdateMatchingStatusTypeRequest updateMatchingStatusTypeRequest = UpdateMatchingStatusTypeRequest.builder()
            .matchingStatusType(MatchingStatusType.COMPLETED)
            .build();

        final UpdateMatchingStatusTypeResponse updateMatchingStatusTypeResponse = UpdateMatchingStatusTypeResponse.builder()
            .matchingId(1L)
            .matchingStatusType(MatchingStatusType.COMPLETED)
            .build();
        // when
        when(receiveMatchingService.updateMatchingStatusType(anyLong(), anyLong(), any())).thenReturn(updateMatchingStatusTypeResponse);

        final ResultActions resultActions = performPostUpdateMatchingStatusType(1L, updateMatchingStatusTypeRequest);
        // then

        final MvcResult mvcResult = resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value("true"))
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("matchingId")
                            .description("매칭 ID (PK)")
                    ),
                    requestFields(
                        fieldWithPath("matchingStatusType")
                            .type(JsonFieldType.STRING)
                            .description("매칭 상태 타입 (COMPLETED, DENIED, REQUESTED)")
                            .attributes(field("constraint", "(COMPLETED, DENIED) 2개 중에 1개"))
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
                        subsectionWithPath("result.matchingId")
                            .type(JsonFieldType.NUMBER)
                            .description("업데이트된 매칭 아이디 (PK)"),
                        subsectionWithPath("result.matchingStatusType")
                            .type(JsonFieldType.STRING)
                            .description("변경된 매칭의 상태")
                    )
                )
            ).andReturn();
    }

    @DisplayName("프로필 뷰어에 매칭 요청을 보낼 프로필의 정보 목록을 조회할 수 있다.")
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
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                Arrays.asList(
                    SenderTeamInformation.builder()
                        .teamCode("발신자 팀 아이디 1 (팀 코드)")
                        .teamName("발신자 팀 이름 1")
                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 1")
                        .teamScaleItem(
                            TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                        )
                        .build(),
                    SenderTeamInformation.builder()
                        .teamCode("발신자 팀 아이디 2 (팀 코드)")
                        .teamName("발신자 팀 이름 2")
                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 2")
                        .teamScaleItem(
                            TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                        )
                        .build()
                )
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 회원 이름")
                    .emailId("수신자 회원 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .build();

        // when
        when(sendMatchingService.selectMatchingRequestToProfileMenu(anyLong(), any())).thenReturn(selectMatchingRequestToProfileMenu);

        final ResultActions resultActions = performGetSelectMatchingRequestToProfileMenu("liaison");

        // then
        final MvcResult mvcResult = resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value("true"))
            .andExpect(jsonPath("$.code").value("1000"))
            .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("emailId")
                            .description("수신자 유저 아이디")
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
                        fieldWithPath("result.senderProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 프로필 소분류 포지션 (없을 수 있음)"),
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
                        fieldWithPath("result.senderTeamInformation[].teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 규모 정보"),
                        fieldWithPath("result.senderTeamInformation[].teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 규모 이름"),
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
                            .description("수신자 프로필 대분류 포지션 (없을 수 있음)"),
                        fieldWithPath("result.receiverProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 프로필 소분류 포지션 (없을 수 있음)")
                    )
                )
            ).andReturn();
    }

    @DisplayName("팀 뷰어에 매칭 요청을 보낼 프로필의 정보 목록을 조회할 수 있다.")
    @Test
    void getSelectMatchingRequestToTeamMenu() throws Exception {
        // given
        final SelectMatchingRequestToTeamMenu selectMatchingRequestToTeamMenu = SelectMatchingRequestToTeamMenu.builder()
            .isTeamInformationExists(true)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 회원 이름")
                    .emailId("발신자 회원 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                Arrays.asList(
                    SenderTeamInformation.builder()
                        .teamCode("발신자 팀 아이디 1 (팀 코드)")
                        .teamName("발신자 팀 이름 1")
                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 1")
                        .teamScaleItem(
                            TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                        )
                        .build(),
                    SenderTeamInformation.builder()
                        .teamCode("발신자 팀 아이디 2 (팀 코드)")
                        .teamName("발신자 팀 이름 2")
                        .teamLogoImagePath("발신자 팀 로고 이미지 경로 2")
                        .teamScaleItem(
                            TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                        )
                        .build()
                )
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamCode("발신자 팀 아이디 1 (팀 코드)")
                    .teamName("발신자 팀 이름 1")
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로 1")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("팀 규모 이름")
                            .build()
                    )
                    .build()
            )
            .build();

        // when
        when(sendMatchingService.selectMatchingRequestToTeamMenu(anyLong(), any())).thenReturn(selectMatchingRequestToTeamMenu);

        final ResultActions resultActions = performGetSelectMatchingRequestToTeamMenu("liaison");
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
                            .description("수신자 팀 아이디 (팀 코드)")
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
                        fieldWithPath("result.senderProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 프로필 소분류 포지션 (없을 수 있음)"),

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

                        fieldWithPath("result.senderTeamInformation[].teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 규모 정보"),
                        fieldWithPath("result.senderTeamInformation[].teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 규모 이름"),

                        fieldWithPath("result.receiverTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 정보"),
                        fieldWithPath("result.receiverTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 아이디 (팀 아이디)"),
                        fieldWithPath("result.receiverTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 이름 (팀 이름)"),
                        fieldWithPath("result.receiverTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.receiverTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 규모 정보"),
                        fieldWithPath("result.receiverTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 규모 이름")
                    )
                )
            ).andReturn();
    }

    @DisplayName("매칭 관리 상단 메뉴 정보를 조회할 수 있다.")
    @Test
    void getMatchingNotificationMenu() throws Exception {
        // given
        final MatchingNotificationMenu matchingNotificationMenu = MatchingNotificationMenu.builder()
            .receivedMatchingNotificationCount(10)
            .requestedMatchingNotificationCount(12)
            .build();

        // when
        when(matchingService.getMatchingNotificationMenu(any())).thenReturn(matchingNotificationMenu);

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
        final CommonResponse<MatchingNotificationMenu> actual = objectMapper.readValue(
            jsonResponse,
            new TypeReference<CommonResponse<MatchingNotificationMenu>>() {
            }
        );

        final CommonResponse<MatchingNotificationMenu> expected = CommonResponse.onSuccess(matchingNotificationMenu);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("매칭 관리 수신함의 정보를 조회할 수 있다.")
    @Test
    void getReceivedMatchingMenu() throws Exception {
        ReceivedMatchingMenu receivedMatchingMenu1 = ReceivedMatchingMenu.builder()
            .matchingId(1L)
            .senderType(SenderType.PROFILE)
            .receiverType(ReceiverType.PROFILE)
            .isChatRoomCreated(true)
            .chatRoomId(1L)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 이름")
                    .emailId("발신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                SenderTeamInformation.builder()
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로")
                    .teamCode("발신자 팀 아이디 (팀 코드)")
                    .teamName("발신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("발신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 이름")
                    .emailId("수신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamLogoImagePath("수신자 팀 로고 이미지 경로")
                    .teamCode("수신자 팀 아이디 (팀 코드)")
                    .teamName("수신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("수신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverAnnouncementInformation(
                ReceiverAnnouncementInformation.builder()
                    .teamMemberAnnouncementId(1L)
                    .teamName("팀 이름")
                    .announcementTitle("공고 제목")
                    .teamLogoImagePath("팀 로고 이미지 경로")
                    .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                            .majorPosition("공고 포지션 대분류")
                            .subPosition("공고 포지션 소분류")
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
            )
            .requestMessage("매칭 요청 메시지")
            .modifiedAt("최종 매칭 정보 변경 시간")
            .matchingStatusType(MatchingStatusType.REQUESTED)
            .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
            .build();

        ReceivedMatchingMenu receivedMatchingMenu2 = ReceivedMatchingMenu.builder()
            .matchingId(2L)
            .senderType(SenderType.TEAM)
            .receiverType(ReceiverType.PROFILE)
            .isChatRoomCreated(true)
            .chatRoomId(2L)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 이름")
                    .emailId("발신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                SenderTeamInformation.builder()
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로")
                    .teamCode("발신자 팀 아이디 (팀 코드)")
                    .teamName("발신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("발신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 이름")
                    .emailId("수신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamLogoImagePath("수신자 팀 로고 이미지 경로")
                    .teamCode("수신자 팀 아이디 (팀 코드)")
                    .teamName("수신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("수신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverAnnouncementInformation(
                ReceiverAnnouncementInformation.builder()
                    .teamMemberAnnouncementId(1L)
                    .teamName("팀 이름")
                    .announcementTitle("공고 제목")
                    .teamLogoImagePath("팀 로고 이미지 경로")
                    .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                            .majorPosition("공고 포지션 대분류")
                            .subPosition("공고 포지션 소분류")
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
            )
            .requestMessage("매칭 요청 메시지")
            .modifiedAt("최종 매칭 정보 변경 시간")
            .matchingStatusType(MatchingStatusType.COMPLETED)
            .receiverReadStatus(ReceiverReadStatus.READ_COMPLETED_MATCHING)
            .build();

        List<ReceivedMatchingMenu> receivedMatchingMenus = Arrays.asList(receivedMatchingMenu1, receivedMatchingMenu2);
        Page<ReceivedMatchingMenu> matchingReceivedMenusPage = new PageImpl<>(receivedMatchingMenus, PageRequest.of(0, 20), receivedMatchingMenus.size());

        when(receiveMatchingService.getReceivedMatchingMenuResponse(anyLong(), any(), any(Pageable.class))).thenReturn(matchingReceivedMenusPage);

        final ResultActions resultActions = performGetReceivedMatchingMenu(
            ReceiverType.PROFILE,
            0,
            20
        );

        final MvcResult mvcResult = resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
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

                        // ----- result
                        fieldWithPath("result.content")
                            .type(JsonFieldType.ARRAY)
                            .description("매칭 수신 목록"),

                        fieldWithPath("result.content[].matchingId")
                            .type(JsonFieldType.NUMBER)
                            .description("매칭 ID (PK)"),

                        fieldWithPath("result.content[].senderType")
                            .type(JsonFieldType.STRING)
                            .description("발신자 타입 - [PROFILE, TEAM]"),
                        fieldWithPath("result.content[].receiverType")
                            .type(JsonFieldType.STRING)
                            .description("수신자 타입 - [PROFILE, TEAM, ANNOUNCEMENT]"),

                        fieldWithPath("result.content[].isChatRoomCreated")
                            .type(JsonFieldType.BOOLEAN)
                            .description("채팅방 생성 여부"),

                        fieldWithPath("result.content[].chatRoomId")
                            .type(JsonFieldType.NUMBER)
                            .description("채팅방이 생성된 경우 해당 채팅방의 ID"),

                        // ----- senderProfileInformation
                        fieldWithPath("result.content[].senderProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 정보"),
                        fieldWithPath("result.content[].senderProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지 경로"),
                        fieldWithPath("result.content[].senderProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 이름"),
                        fieldWithPath("result.content[].senderProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("발신자 유저 아이디"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 포지션 정보"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 소분류 (null 가능)"),

                        // ----- senderTeamInformation
                        fieldWithPath("result.content[].senderTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 정보"),
                        fieldWithPath("result.content[].senderTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.content[].senderTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 이름"),
                        fieldWithPath("result.content[].senderTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.content[].senderTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 규모 정보"),
                        fieldWithPath("result.content[].senderTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 규모명"),

                        // ----- receiverProfileInformation
                        fieldWithPath("result.content[].receiverProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 정보"),
                        fieldWithPath("result.content[].receiverProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 프로필 이미지 경로"),
                        fieldWithPath("result.content[].receiverProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 이름"),
                        fieldWithPath("result.content[].receiverProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("수신자 유저 아이디"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 포지션 정보"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 소분류 (null 가능)"),

                        // ----- receiverTeamInformation
                        fieldWithPath("result.content[].receiverTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 정보"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 이름"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 규모 정보"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 규모명"),

                        // ----- receiverAnnouncementInformation 추가
                        fieldWithPath("result.content[].receiverAnnouncementInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 공고 정보"),

                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamMemberAnnouncementId")
                            .type(JsonFieldType.NUMBER)
                            .description("공고 아이디"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 이름"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementTitle")
                            .type(JsonFieldType.STRING)
                            .description("공고 제목"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 로고 이미지 경로"),

                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem")
                            .type(JsonFieldType.OBJECT)
                            .description("공고 포지션 정보"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem.majorPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 대분류"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem.subPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 소분류"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementSkillNames")
                            .type(JsonFieldType.ARRAY)
                            .description("공고 요구 스킬 목록"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementSkillNames[].announcementSkillName")
                            .type(JsonFieldType.STRING)
                            .description("공고 요구 스킬 이름"),

                        // ----- 추가 필드
                        fieldWithPath("result.content[].requestMessage")
                            .type(JsonFieldType.STRING)
                            .description("매칭 요청 메시지"),
                        fieldWithPath("result.content[].modifiedAt")
                            .type(JsonFieldType.STRING)
                            .description("개별 매칭 정보 최종 수정 시간"),

                        fieldWithPath("result.content[].matchingStatusType")
                            .type(JsonFieldType.STRING)
                            .description("매칭 상태 - [REQUESTED, COMPLETED 등]"),
                        fieldWithPath("result.content[].receiverReadStatus")
                            .type(JsonFieldType.STRING)
                            .description("수신자 읽음 상태 - [UNREAD_REQUESTED_MATCHING, READ_COMPLETED_MATCHING 등]"),

                        // ----- 페이지네이션 필드
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
            .isChatRoomCreated(true)
            .chatRoomId(1L)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 이름")
                    .emailId("발신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                SenderTeamInformation.builder()
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로")
                    .teamCode("발신자 팀 아이디 (팀 코드)")
                    .teamName("발신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("발신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 이름")
                    .emailId("수신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamLogoImagePath("수신자 팀 로고 이미지 경로")
                    .teamCode("수신자 팀 아이디 (팀 코드)")
                    .teamName("수신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("수신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverAnnouncementInformation(
                ReceiverAnnouncementInformation.builder()
                    .teamMemberAnnouncementId(1L)
                    .teamName("팀 이름")
                    .announcementTitle("공고 제목")
                    .teamLogoImagePath("팀 로고 이미지 경로")
                    .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                            .majorPosition("공고 포지션 대분류")
                            .subPosition("공고 포지션 소분류")
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
            )
            .requestMessage("매칭 요청 메시지")
            .modifiedAt("최신 매칭 수정 시간")
            .matchingStatusType(MatchingStatusType.REQUESTED)
            .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
            .build();

        RequestedMatchingMenu requestedMatchingMenu2 = RequestedMatchingMenu.builder()
            .matchingId(2L)
            .senderType(SenderType.PROFILE)
            .receiverType(ReceiverType.PROFILE)
            .isChatRoomCreated(true)
            .chatRoomId(2L)
            .senderProfileInformation(
                SenderProfileInformation.builder()
                    .profileImagePath("발신자 프로필 이미지 경로")
                    .memberName("발신자 이름")
                    .emailId("발신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("발신자 포지션 대분류")
                            .subPosition("발신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .senderTeamInformation(
                SenderTeamInformation.builder()
                    .teamLogoImagePath("발신자 팀 로고 이미지 경로")
                    .teamCode("발신자 팀 아이디 (팀 코드)")
                    .teamName("발신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("발신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverProfileInformation(
                ReceiverProfileInformation.builder()
                    .profileImagePath("수신자 프로필 이미지 경로")
                    .memberName("수신자 이름")
                    .emailId("수신자 유저 아이디")
                    .profilePositionDetail(
                        ProfilePositionDetail.builder()
                            .majorPosition("수신자 포지션 대분류")
                            .subPosition("수신자 포지션 소분류")
                            .build()
                    )
                    .build()
            )
            .receiverTeamInformation(
                ReceiverTeamInformation.builder()
                    .teamLogoImagePath("수신자 팀 로고 이미지 경로")
                    .teamCode("수신자 팀 아이디 (팀 코드)")
                    .teamName("수신자 팀 이름")
                    .teamScaleItem(
                        TeamScaleItem.builder()
                            .teamScaleName("수신자 팀 규모명")
                            .build()
                    )
                    .build()
            )
            .receiverAnnouncementInformation(
                ReceiverAnnouncementInformation.builder()
                    .teamMemberAnnouncementId(1L)
                    .teamName("팀 이름")
                    .announcementTitle("공고 제목")
                    .teamLogoImagePath("팀 로고 이미지 경로")
                    .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                            .majorPosition("공고 포지션 대분류")
                            .subPosition("공고 포지션 소분류")
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
            )
            .requestMessage("매칭 요청 메시지")
            .modifiedAt("최신 매칭 수정 시간")
            .matchingStatusType(MatchingStatusType.REQUESTED)
            .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
            .build();

        List<RequestedMatchingMenu> requestedMatchingMenus = Arrays.asList(requestedMatchingMenu1, requestedMatchingMenu2);
        Page<RequestedMatchingMenu> matchingReceivedMenus = new PageImpl<>(requestedMatchingMenus, PageRequest.of(0, 20), requestedMatchingMenus.size());

        // when
        when(sendMatchingService.getRequestedMatchingMenuResponse(anyLong(), any(), any(Pageable.class))).thenReturn(matchingReceivedMenus);

        final ResultActions resultActions = performGetMatchingRequestedMenu(
            SenderType.PROFILE,
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

                        // result 객체
                        fieldWithPath("result.content")
                            .type(JsonFieldType.ARRAY)
                            .description("매칭 발신 목록"),
                        fieldWithPath("result.content[].matchingId")
                            .type(JsonFieldType.NUMBER)
                            .description("매칭 ID (PK)"),
                        fieldWithPath("result.content[].isChatRoomCreated")
                            .type(JsonFieldType.BOOLEAN)
                            .description("채팅방 생성 여부"),
                        fieldWithPath("result.content[].chatRoomId")
                            .type(JsonFieldType.NUMBER)
                            .description("채팅방이 생성된 경우 해당 채팅방의 ID"),
                        fieldWithPath("result.content[].senderType")
                            .type(JsonFieldType.STRING)
                            .description("발신자 타입 - [PROFILE, TEAM]"),
                        fieldWithPath("result.content[].receiverType")
                            .type(JsonFieldType.STRING)
                            .description("수신자 타입 - [PROFILE, TEAM, ANNOUNCEMENT]"),

                        // senderProfileInformation
                        fieldWithPath("result.content[].senderProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 정보"),
                        fieldWithPath("result.content[].senderProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 프로필 이미지 경로"),
                        fieldWithPath("result.content[].senderProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 이름"),
                        fieldWithPath("result.content[].senderProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("발신자 유저 아이디"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 프로필 포지션 정보"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.content[].senderProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("발신자 포지션 소분류 (null 가능)"),

                        // senderTeamInformation
                        fieldWithPath("result.content[].senderTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 정보"),
                        fieldWithPath("result.content[].senderTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.content[].senderTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 이름"),
                        fieldWithPath("result.content[].senderTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.content[].senderTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("발신자 팀 규모 정보"),
                        fieldWithPath("result.content[].senderTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("발신자 팀 규모명"),

                        // receiverProfileInformation
                        fieldWithPath("result.content[].receiverProfileInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 정보"),
                        fieldWithPath("result.content[].receiverProfileInformation.profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 프로필 이미지 경로"),
                        fieldWithPath("result.content[].receiverProfileInformation.memberName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 이름"),
                        fieldWithPath("result.content[].receiverProfileInformation.emailId")
                            .type(JsonFieldType.STRING)
                            .description("수신자 유저 아이디"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 프로필 포지션 정보"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail.majorPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 대분류 (null 가능)"),
                        fieldWithPath("result.content[].receiverProfileInformation.profilePositionDetail.subPosition")
                            .type(JsonFieldType.STRING)
                            .optional()
                            .description("수신자 포지션 소분류 (null 가능)"),

                        // receiverTeamInformation
                        fieldWithPath("result.content[].receiverTeamInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 정보"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamCode")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 이름"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 로고 이미지 경로"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamScaleItem")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 팀 규모 정보"),
                        fieldWithPath("result.content[].receiverTeamInformation.teamScaleItem.teamScaleName")
                            .type(JsonFieldType.STRING)
                            .description("수신자 팀 규모명"),

                        // receiverAnnouncementInformation (수정된 부분: content[] 내부)
                        fieldWithPath("result.content[].receiverAnnouncementInformation")
                            .type(JsonFieldType.OBJECT)
                            .description("수신자 공고 정보"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamMemberAnnouncementId")
                            .type(JsonFieldType.NUMBER)
                            .description("공고 아이디"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamName")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 이름"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementTitle")
                            .type(JsonFieldType.STRING)
                            .description("공고 제목"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("공고 올림 팀의 로고 이미지 경로"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem")
                            .type(JsonFieldType.OBJECT)
                            .description("공고 포지션 정보"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem.majorPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 대분류"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementPositionItem.subPosition")
                            .type(JsonFieldType.STRING)
                            .description("공고 포지션 소분류"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementSkillNames")
                            .type(JsonFieldType.ARRAY)
                            .description("공고 요구 스킬 목록"),
                        fieldWithPath("result.content[].receiverAnnouncementInformation.announcementSkillNames[].announcementSkillName")
                            .type(JsonFieldType.STRING)
                            .description("공고 요구 스킬 이름"),

                        // 일반 필드
                        fieldWithPath("result.content[].requestMessage")
                            .type(JsonFieldType.STRING)
                            .description("매칭 요청 메시지"),
                        fieldWithPath("result.content[].modifiedAt")
                            .type(JsonFieldType.STRING)
                            .description("개별 매칭 케이스 최종 수정 시간"),
                        fieldWithPath("result.content[].matchingStatusType")
                            .type(JsonFieldType.STRING)
                            .description("매칭 상태 (REQUESTED, COMPLETED 등)"),
                        fieldWithPath("result.content[].receiverReadStatus")
                            .type(JsonFieldType.STRING)
                            .description("수신자 읽음 상태"),

                        // 페이지네이션 필드
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
            ).andReturn();
    }

    @DisplayName("매칭 관리 수신함에서 매칭을 읽음 처리할 수 있다.")
    @Test
    void updateReceivedMatchingRequestedStateRead() throws Exception {
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
                    .receiverReadStatus(ReceiverReadStatus.READ_COMPLETED_MATCHING)
                    .build()
            ))
            .build();

        // when
        when(receiveMatchingService.updateReceivedMatchingStateToRead(any())).thenReturn(updateReceivedMatchingCompletedStateReadItems);

        final ResultActions resultActions = performUpdateReceivedMatchingStateRead(request);

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
                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems")
                            .type(JsonFieldType.ARRAY)
                            .description("업데이트된 매칭 항목 목록"),
                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems[].matchingId")
                            .type(JsonFieldType.NUMBER)
                            .description("업데이트된 매칭 ID"),
                        fieldWithPath("result.updateReceivedMatchingCompletedStateReadItems[].receiverReadStatus")
                            .type(JsonFieldType.STRING)
                            .description("업데이트된 매칭의 읽음 상태")
                    )
                )
            )
            .andReturn();
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
        when(receiveMatchingService.deleteReceivedMatchingItems(any())).thenReturn(deleteReceivedMatchingItems);

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
        when(sendMatchingService.deleteRequestedMatchingItems(any())).thenReturn(deleteRequestedMatchingItems);

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
