package liaison.linkit.notification.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItem;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItems;
import liaison.linkit.notification.service.NotificationService;
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

@WebMvcTest(NotificationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class NotificationControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetNotificationItems() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/notifications") // 실제 엔드포인트로 변경 필요
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    @DisplayName("회원이 내가 수신한 알림을 전체 조회할 수 있다.")
    @Test
    void getNotificationItems() throws Exception {
        // given
        final NotificationItems notificationItems = NotificationItems.builder()
                .notificationItems(Arrays.asList(
                        // 1) TEAM_INVITATION_REQUESTED (팀 이름만)
                        NotificationItem.builder()
                                .notificationType(NotificationType.TEAM_INVITATION)
                                .subNotificationType(SubNotificationType.TEAM_INVITATION_REQUESTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("방금 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.teamInvitationRequested(
                                                "팀 코드",
                                                "팀원으로 초대한 팀의 이름"
                                        )
                                )
                                .build(),
                        // 2) TEAM_MEMBER_JOINED (팀 이름 + 팀원 이름)
                        NotificationItem.builder()
                                .notificationType(NotificationType.TEAM_INVITATION)
                                .subNotificationType(SubNotificationType.TEAM_MEMBER_JOINED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("방금 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.teamMemberJoined(
                                                "팀 코드",
                                                "팀원으로 들어온 회원의 이름",
                                                "팀원으로 초대한 팀의 이름"
                                        )
                                )
                                .build(),

                        // 3) CHATTING: NEW_CHAT
                        NotificationItem.builder()
                                .notificationType(NotificationType.CHATTING)
                                .subNotificationType(SubNotificationType.NEW_CHAT)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("방금 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.chat(
                                                "채팅 발신자 이름"
                                        )
                                )
                                .build(),

                        // 4) MATCHING_REQUESTED
                        NotificationItem.builder()
                                .notificationType(NotificationType.MATCHING)
                                .subNotificationType(SubNotificationType.MATCHING_REQUESTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.matchingRequested(
                                                "매칭 상대방의 이름"
                                        )
                                )
                                .build(),
                        // 5) MATCHING_ACCEPTED
                        NotificationItem.builder()
                                .notificationType(NotificationType.MATCHING)
                                .subNotificationType(SubNotificationType.MATCHING_ACCEPTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.matchingAccepted(
                                                "매칭 상대방의 이름"
                                        )
                                )
                                .build(),
                        // 6) MATCHING_REJECTED
                        NotificationItem.builder()
                                .notificationType(NotificationType.MATCHING)
                                .subNotificationType(SubNotificationType.MATCHING_REJECTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.matchingRejected(
                                                "매칭 상대방의 이름"
                                        )
                                )
                                .build(),

                        // 7) TEAM: REMOVE_TEAM_REQUESTED (팀 + 팀원)
                        NotificationItem.builder()
                                .notificationType(NotificationType.TEAM)
                                .subNotificationType(SubNotificationType.REMOVE_TEAM_REQUESTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.removeTeamRequested(
                                                "팀 코드",
                                                "팀 삭제 요청을 진행한 팀원(오너/관리자)의 이름",
                                                "팀 이름"
                                        )
                                )
                                .build(),

                        // 8) TEAM: REMOVE_TEAM_REJECTED (팀 + 팀원)
                        NotificationItem.builder()
                                .notificationType(NotificationType.TEAM)
                                .subNotificationType(SubNotificationType.REMOVE_TEAM_REJECTED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.removeTeamRejected(
                                                "팀 코드",
                                                "팀 삭제 요청을 거절한 팀원(오너/관리자)의 이름",
                                                "삭제 완료된 팀 이름"
                                        )
                                )
                                .build(),

                        // 9) TEAM: REMOVE_TEAM_COMPLETED (팀 이름만)
                        NotificationItem.builder()
                                .notificationType(NotificationType.TEAM)
                                .subNotificationType(SubNotificationType.REMOVE_TEAM_COMPLETED)
                                .notificationReadStatus(NotificationReadStatus.READ)
                                .notificationOccurTime("1일 전")
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.removeTeamCompleted(
                                                "팀 코드",
                                                "삭제 완료된 팀 이름"
                                        )
                                )
                                .build()
                ))
                .build();

        // when
        when(notificationService.getNotificationItems(any())).thenReturn(notificationItems);

        final ResultActions resultActions = performGetNotificationItems();

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
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
                                                .description("결과 데이터 객체")
                                                .attributes(field("constraint", "object")),
                                        fieldWithPath("result.notificationItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("알림 목록 배열")
                                                .attributes(field("constraint", "배열")),

                                        fieldWithPath("result.notificationItems[].notificationType")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 유형 - 변동 가능성 있음 (MATCHING, CHATTING, TEAM_INVITATION, TEAM)"),

                                        fieldWithPath("result.notificationItems[].subNotificationType")
                                                .type(JsonFieldType.STRING)
                                                .description("""
                                                            알림 상세 유형:
                                                            - MATCHING_REQUESTED: 매칭 요청
                                                            - MATCHING_ACCEPTED: 매칭 수락
                                                            - MATCHING_REJECTED: 매칭 거절
                                                            - NEW_CHAT: 새로운 채팅 메시지 수신
                                                            - TEAM_INVITATION_REQUESTED: 팀 초대 요청
                                                            - TEAM_MEMBER_JOINED: 팀에 새로운 멤버 추가
                                                            - REMOVE_TEAM_REQUESTED: 팀 삭제 요청
                                                            - REMOVE_TEAM_REJECTED: 팀 삭제 요청 거절
                                                            - REMOVE_TEAM_COMPLETED: 팀 삭제 완료
                                                        """),

                                        fieldWithPath("result.notificationItems[].notificationReadStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 읽음 여부 (UNREAD, READ)"),

                                        fieldWithPath("result.notificationItems[].notificationOccurTime")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 발생 시간 (STRING)"),

                                        fieldWithPath("result.notificationItems[].notificationDetails")
                                                .type(JsonFieldType.OBJECT)
                                                .description("알림 타입별 상세 정보"),

                                        // TEAM/TEAM_INVITATION 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름 (팀 초대, 팀 삭제 알림 등)")
                                                .optional(),
                                        fieldWithPath("result.notificationItems[].notificationDetails.teamMemberName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 관련 알림에서 팀원의 이름 (팀 초대, 팀 삭제 요청 등)")
                                                .optional(),

                                        // CHATTING 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.chatSenderName")
                                                .type(JsonFieldType.STRING)
                                                .description("메시지 발신자 이름 (채팅 알림인 경우)")
                                                .optional(),

                                        // MATCHING 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.matchingTargetName")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 상대방의 이름 (매칭 알림인 경우)")
                                                .optional()
                                )
                        )
                )
                .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<NotificationItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {
                }
        );

        final CommonResponse<NotificationItems> expected = CommonResponse.onSuccess(notificationItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
