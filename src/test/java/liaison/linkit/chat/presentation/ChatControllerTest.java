package liaison.linkit.chat.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import liaison.linkit.chat.domain.ChatRoom.ParticipantType;
import liaison.linkit.chat.domain.type.CreateChatLocation;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.CreateChatRoomRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatPartnerInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerProfileDetailInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerTeamDetailInformation;
import liaison.linkit.chat.service.ChatService;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(ChatController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ChatControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performCreateChatRoom(final CreateChatRoomRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/chat/room")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions performGetChatLeftMenu() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/chat/left/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 수신/발신함에서 새로운 채팅방을 생성할 수 있다.")
    @Test
    void createChatRoom() throws Exception {
        // given
        final CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
                .matchingId(1L)
                .createChatLocation(CreateChatLocation.REQUESTED)
                .senderType(SenderType.PROFILE)
                .senderEmailId("발신자 유저 아이디")
                .senderTeamCode("발신자 팀 아이디 (팀 코드)")
                .receiverType(ReceiverType.TEAM)
                .receiverEmailId("수신자 유저 아이디")
                .receiverTeamCode("수신자 팀 아이디")
                .receiverAnnouncementId(1L)
                .build();

        final CreateChatRoomResponse createChatRoomResponse = CreateChatRoomResponse.builder()
                .chatRoomId(1L)
                .participantAId("참여자 A의 ID (Profile - emailId, Team - teamCode)")
                .participantAType(ParticipantType.PROFILE)
                .participantAName("참여자 A의 이름 (Profile - memberName, Team - teamName)")
                .participantBId("참여자 B의 ID (Profile - emailId, Team - teamCode)")
                .participantBType(ParticipantType.TEAM)
                .participantBName("참여자 B의 이름 (Profile - memberName, Team - teamName)")
                .lastMessage("채팅방 마지막 메시지")
                .lastMessageTime("마지막 메시지 발송 시간")
                .unreadCount(0L)
                .build();

        // when
        when(chatService.createChatRoom(any(), anyLong())).thenReturn(createChatRoomResponse);

        final ResultActions resultActions = performCreateChatRoom(createChatRoomRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("matchingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅을 생성하기 위한 매칭 ID"),
                                        fieldWithPath("createChatLocation")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 생성 위치 (REQUESTED: 발신함, RECEIVED: 수신함)"),
                                        fieldWithPath("senderType")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 타입 (PROFILE 또는 TEAM)"),
                                        fieldWithPath("senderEmailId")
                                                .type(JsonFieldType.STRING)
                                                .description("발신자 유저 아이디 (PROFILE 타입일 경우)"),
                                        fieldWithPath("senderTeamCode")
                                                .type(JsonFieldType.STRING)
                                                .optional()
                                                .description("발신자 팀 코드 (TEAM 타입일 경우)"),
                                        fieldWithPath("receiverType")
                                                .type(JsonFieldType.STRING)
                                                .description("수신자 타입 (PROFILE, TEAM 또는 ANNOUNCEMENT)"),
                                        fieldWithPath("receiverEmailId")
                                                .type(JsonFieldType.STRING)
                                                .optional()
                                                .description("수신자 유저 아이디 (PROFILE 타입일 경우)"),
                                        fieldWithPath("receiverTeamCode")
                                                .type(JsonFieldType.STRING)
                                                .optional()
                                                .description("수신자 팀 코드 (TEAM 타입일 경우)"),
                                        fieldWithPath("receiverAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("수신자 공고 ID (ANNOUNCEMENT 타입일 경우)")
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
                                        fieldWithPath("result.chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("생성된 채팅방 ID"),
                                        fieldWithPath("result.participantAId")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 A의 ID (Profile - emailId, Team - teamCode)"),
                                        fieldWithPath("result.participantAType")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 A의 타입 (PROFILE 또는 TEAM)"),
                                        fieldWithPath("result.participantAName")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 A의 이름 (Profile - memberName, Team - teamName)"),
                                        fieldWithPath("result.participantBId")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 B의 ID (Profile - emailId, Team - teamCode)"),
                                        fieldWithPath("result.participantBType")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 B의 타입 (PROFILE 또는 TEAM)"),
                                        fieldWithPath("result.participantBName")
                                                .type(JsonFieldType.STRING)
                                                .description("참여자 B의 이름 (Profile - memberName, Team - teamName)"),
                                        fieldWithPath("result.lastMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅 마지막 메시지 내용"),
                                        fieldWithPath("result.lastMessageTime")
                                                .type(JsonFieldType.STRING)
                                                .description("마지막 메시지 전송 시간"),
                                        fieldWithPath("result.unreadCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("읽지 않은 메시지 개수")
                                )
                        )
                ).andReturn();
    }

    @DisplayName("내가 참여하고 있는 채팅방 목록을 조회할 수 있다.")
    @Test
    void getChatLeftMenu() throws Exception {

        // given
        final ChatLeftMenu chatLeftMenu = ChatLeftMenu.builder()
                .chatRoomSummaries(
                        Arrays.asList(
                                ChatRoomSummary.builder()
                                        .chatRoomId(1L)
                                        .chatPartnerInformation(
                                                ChatPartnerInformation.builder()
                                                        .chatPartnerName("채팅 상대방 이름")
                                                        .chatPartnerImageUrl("채팅 상대방의 프로필 이미지")
                                                        .partnerProfileDetailInformation(
                                                                PartnerProfileDetailInformation.builder()
                                                                        .profilePositionDetail(
                                                                                ProfilePositionDetail.builder()
                                                                                        .majorPosition("프로필 포지션 대분류")
                                                                                        .subPosition("프로필 포지션 소분류")
                                                                                        .build()
                                                                        )
                                                                        .regionDetail(
                                                                                RegionDetail.builder()
                                                                                        .cityName("프로필 지역 시/도")
                                                                                        .divisionName("프로필 지역 시/군/구")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .partnerTeamDetailInformation(
                                                                PartnerTeamDetailInformation.builder()
                                                                        .teamScaleItem(
                                                                                TeamScaleItem.builder()
                                                                                        .teamScaleName("팀 규모 (1인, 5인, ...)")
                                                                                        .build()
                                                                        )
                                                                        .regionDetail(
                                                                                RegionDetail.builder()
                                                                                        .cityName("팀 활동 지역 시/도")
                                                                                        .divisionName("팀 활동 지역 시/군/구")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .lastMessage("해당 채팅방에서의 마지막 메시지")
                                                        .lastMessageTime(LocalDateTime.now())
                                                        .build()
                                        )
                                        .build(),
                                ChatRoomSummary.builder()
                                        .chatRoomId(2L)
                                        .chatPartnerInformation(
                                                ChatPartnerInformation.builder()
                                                        .chatPartnerName("채팅 상대방 이름")
                                                        .chatPartnerImageUrl("채팅 상대방의 프로필 이미지")
                                                        .partnerProfileDetailInformation(
                                                                PartnerProfileDetailInformation.builder()
                                                                        .profilePositionDetail(
                                                                                ProfilePositionDetail.builder()
                                                                                        .majorPosition("프로필 포지션 대분류")
                                                                                        .subPosition("프로필 포지션 소분류")
                                                                                        .build()
                                                                        )
                                                                        .regionDetail(
                                                                                RegionDetail.builder()
                                                                                        .cityName("프로필 지역 시/도")
                                                                                        .divisionName("프로필 지역 시/군/구")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .partnerTeamDetailInformation(
                                                                PartnerTeamDetailInformation.builder()
                                                                        .teamScaleItem(
                                                                                TeamScaleItem.builder()
                                                                                        .teamScaleName("팀 규모 (1인, 5인, ...)")
                                                                                        .build()
                                                                        )
                                                                        .regionDetail(
                                                                                RegionDetail.builder()
                                                                                        .cityName("팀 활동 지역 시/도")
                                                                                        .divisionName("팀 활동 지역 시/군/구")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .lastMessage("해당 채팅방에서의 마지막 메시지")
                                                        .lastMessageTime(LocalDateTime.now())
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .build();

        // when
        when(chatService.getChatLeftMenu(anyLong())).thenReturn(chatLeftMenu);

        final ResultActions resultActions = performGetChatLeftMenu();
        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
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
                                        fieldWithPath("result.chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("생성된 채팅방 ID")
                                )
                        )
                ).andReturn();
    }
}
