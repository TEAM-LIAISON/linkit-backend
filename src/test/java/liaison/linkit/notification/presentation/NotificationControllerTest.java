package liaison.linkit.notification.presentation;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.notification.domain.type.NotificationReadStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItem;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationItems;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.ReadNotificationResponse;
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

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private NotificationService notificationService;

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
                        .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performUpdateNotificationReadStatus(final String notificationId)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/notification/read/{notificationId}", notificationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 내가 수신한 알림을 전체 조회할 수 있다.")
    @Test
    void getNotificationItems() throws Exception {
        // given
        final NotificationItems notificationItems =
                NotificationItems.builder()
                        .notificationItems(
                                Arrays.asList(
                                        // 1) TEAM_INVITATION_REQUESTED (팀 이름만)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 1")
                                                .notificationType(NotificationType.TEAM_INVITATION)
                                                .subNotificationType(
                                                        SubNotificationType
                                                                .TEAM_INVITATION_REQUESTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("방금 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .teamInvitationRequested(
                                                                        "팀 코드",
                                                                        "팀원으로 초대된 팀의 로고 이미지 경로",
                                                                        "팀원으로 초대한 팀의 이름",
                                                                        false))
                                                .build(),
                                        // 2) TEAM_MEMBER_JOINED (팀 이름 + 팀원 이름)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 2")
                                                .notificationType(NotificationType.TEAM_INVITATION)
                                                .subNotificationType(
                                                        SubNotificationType.TEAM_MEMBER_JOINED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("방금 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .teamMemberJoined(
                                                                        "팀 코드",
                                                                        "팀원으로 들어간 팀의 로고 이미지 경로",
                                                                        "팀원으로 들어온 회원의 이름",
                                                                        "팀원으로 초대한 팀의 이름",
                                                                        false))
                                                .build(),

                                        // 3) CHATTING: NEW_CHAT
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 3")
                                                .notificationType(NotificationType.CHATTING)
                                                .subNotificationType(SubNotificationType.NEW_CHAT)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("방금 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .chat(
                                                                        "채팅방 아이디",
                                                                        "채팅 발신자의 로고 이미지 경로",
                                                                        "채팅 발신자 이름"))
                                                .build(),

                                        // 4) MATCHING_REQUESTED
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 4")
                                                .notificationType(NotificationType.MATCHING)
                                                .subNotificationType(
                                                        SubNotificationType.MATCHING_REQUESTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .matchingRequested(
                                                                        1L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름"))
                                                .build(),
                                        // 5) MATCHING_ACCEPTED
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 5")
                                                .notificationType(NotificationType.MATCHING)
                                                .subNotificationType(
                                                        SubNotificationType.MATCHING_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .matchingAccepted(
                                                                        2L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름"))
                                                .build(),
                                        // 6) MATCHING_REJECTED
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 6")
                                                .notificationType(NotificationType.MATCHING)
                                                .subNotificationType(
                                                        SubNotificationType.MATCHING_REJECTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .matchingRejected(
                                                                        3L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 7")
                                                .notificationType(NotificationType.ANNOUNCEMENT)
                                                .subNotificationType(
                                                        SubNotificationType.ANNOUNCEMENT_REQUESTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .announcementRequested(
                                                                        1L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름",
                                                                        "포지션 대분류"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 8")
                                                .notificationType(NotificationType.ANNOUNCEMENT)
                                                .subNotificationType(
                                                        SubNotificationType.ANNOUNCEMENT_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .announcementAccepted(
                                                                        1L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름",
                                                                        "포지션 대분류"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 9")
                                                .notificationType(NotificationType.ANNOUNCEMENT)
                                                .subNotificationType(
                                                        SubNotificationType.ANNOUNCEMENT_REJECTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .announcementRejected(
                                                                        1L,
                                                                        "매칭 상대방의 로고 이미지 경로",
                                                                        "매칭 상대방의 이름",
                                                                        "포지션 대분류"))
                                                .build(),

                                        // 7) TEAM: REMOVE_TEAM_REQUESTED (팀 + 팀원)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 10")
                                                .notificationType(NotificationType.TEAM)
                                                .subNotificationType(
                                                        SubNotificationType.REMOVE_TEAM_REQUESTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .removeTeamRequested(
                                                                        "팀 코드",
                                                                        "삭제 요청된 팀 로고 이미지 경로",
                                                                        "팀 삭제 요청을 진행한 팀원(오너/관리자)의 이름",
                                                                        "팀 이름",
                                                                        false))
                                                .build(),

                                        // 8) TEAM: REMOVE_TEAM_REJECTED (팀 + 팀원)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 11")
                                                .notificationType(NotificationType.TEAM)
                                                .subNotificationType(
                                                        SubNotificationType.REMOVE_TEAM_REJECTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .removeTeamRejected(
                                                                        "팀 코드",
                                                                        "삭제 거절된 팀 로고 이미지 경로",
                                                                        "팀 삭제 요청을 거절한 팀원(오너/관리자)의 이름",
                                                                        "삭제 완료된 팀 이름",
                                                                        false))
                                                .build(),

                                        // 9) TEAM: REMOVE_TEAM_COMPLETED (팀 이름만)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 12")
                                                .notificationType(NotificationType.TEAM)
                                                .subNotificationType(
                                                        SubNotificationType.REMOVE_TEAM_COMPLETED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .removeTeamCompleted(
                                                                        "팀 코드",
                                                                        "삭제 완료된 팀 로고 이미지 경로",
                                                                        "삭제 완료된 팀 이름",
                                                                        false))
                                                .build(),

                                        // 10)
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 13")
                                                .notificationType(NotificationType.CERTIFICATION)
                                                .subNotificationType(
                                                        SubNotificationType
                                                                .ACTIVITY_CERTIFICATION_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .certificationAccepted(
                                                                        1L, "ACTIVITY"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 14")
                                                .notificationType(NotificationType.CERTIFICATION)
                                                .subNotificationType(
                                                        SubNotificationType
                                                                .AWARDS_CERTIFICATION_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .certificationAccepted(
                                                                        1L, "AWARDS"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 15")
                                                .notificationType(NotificationType.CERTIFICATION)
                                                .subNotificationType(
                                                        SubNotificationType
                                                                .EDUCATION_CERTIFICATION_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .certificationAccepted(
                                                                        1L, "EDUCATION"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 16")
                                                .notificationType(NotificationType.CERTIFICATION)
                                                .subNotificationType(
                                                        SubNotificationType
                                                                .LICENSE_CERTIFICATION_ACCEPTED)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .certificationAccepted(
                                                                        1L, "LICENSE"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 17")
                                                .notificationType(NotificationType.VISITOR)
                                                .subNotificationType(
                                                        SubNotificationType.PROFILE_VISITOR)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .profileVisitorCount(
                                                                        "emailId",
                                                                        1L,
                                                                        "PROFILE_VISITOR"))
                                                .build(),
                                        NotificationItem.builder()
                                                .notificationId("알림 ID 18")
                                                .notificationType(NotificationType.VISITOR)
                                                .subNotificationType(
                                                        SubNotificationType.TEAM_VISITOR)
                                                .notificationReadStatus(NotificationReadStatus.READ)
                                                .notificationOccurTime("1일 전")
                                                .notificationDetails(
                                                        NotificationResponseDTO.NotificationDetails
                                                                .teamVisitorCount(
                                                                        "팀이름",
                                                                        "teamCode",
                                                                        2L,
                                                                        "TEAM_VISITOR"))
                                                .build()))
                        .build();

        // when
        when(notificationService.getNotificationItems(any())).thenReturn(notificationItems);

        final ResultActions resultActions = performGetNotificationItems();

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

                                                // result
                                                fieldWithPath("result")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("결과 데이터 객체")
                                                        .attributes(field("constraint", "object")),
                                                fieldWithPath("result.notificationItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("알림 목록 배열")
                                                        .attributes(field("constraint", "배열")),

                                                // 공통 필드
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("알림 ID (DB 식별자, null일 수 있음)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "알림 유형 (MATCHING, ANNOUNCEMENT, CHATTING, TEAM_INVITATION, TEAM, CERTIFICATION, VISITOR, SYSTEM)"),
                                                fieldWithPath(
                                                                "result.notificationItems[].subNotificationType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                """
                                                                            알림 상세 유형:
                                                                            - MATCHING_REQUESTED: 매칭 요청
                                                                            - MATCHING_ACCEPTED: 매칭 수락
                                                                            - MATCHING_REJECTED: 매칭 거절
                                                                            - ANNOUNCEMENT_REQUESTED: 공고 지원 요청
                                                                            - ANNOUNCEMENT_ACCEPTED: 공고 지원 수락
                                                                            - ANNOUNCEMENT_REJECTED: 공고 지원 거절
                                                                            - NEW_CHAT: 새로운 채팅 메시지 수신
                                                                            - TEAM_INVITATION_REQUESTED: 팀 초대 요청
                                                                            - TEAM_MEMBER_JOINED: 팀에 새로운 멤버 추가
                                                                            - REMOVE_TEAM_REQUESTED: 팀 삭제 요청
                                                                            - REMOVE_TEAM_REJECTED: 팀 삭제 요청 거절
                                                                            - REMOVE_TEAM_COMPLETED: 팀 삭제 완료
                                                                            - ACTIVITY_CERTIFICATION_ACCEPTED: 이력 증명서 인증 완료
                                                                            - ACTIVITY_CERTIFICATION_REJECTED: 이력 증명서 인증 거절
                                                                            - EDUCATION_CERTIFICATION_ACCEPTED: 학력 증명서 인증 완료
                                                                            - EDUCATION_CERTIFICATION_REJECTED: 학력 증명서 인증 거절
                                                                            - AWARDS_CERTIFICATION_ACCEPTED: 수상 증명서 인증 완료
                                                                            - AWARDS_CERTIFICATION_REJECTED: 수상 증명서 인증 거절
                                                                            - LICENSE_CERTIFICATION_ACCEPTED: 자격증 증명서 인증 완료
                                                                            - LICENSE_CERTIFICATION_REJECTED: 자격증 증명서 인증 거절
                                                                            - PROFILE_VISITOR: 프로필 방문자에 대한 알림
                                                                            - TEAM_VISITOR: 팀 방문자에 대한 알림
                                                                        """),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationReadStatus")
                                                        .type(JsonFieldType.STRING)
                                                        .description("알림 읽음 여부 (UNREAD, READ)"),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationOccurTime")
                                                        .type(JsonFieldType.STRING)
                                                        .description("알림 발생 시간 (STRING)"),

                                                // notificationDetails (object)
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("알림 타입별 상세 정보"),

                                                // [TEAM] 관련 추가 필드
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 코드 (팀 초대/삭제 알림 등)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로 (팀 관련 알림)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.isTeamDeleted")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description(
                                                                "조회 시점에서 팀 삭제 여부 (팀 삭제 알림인 경우)")
                                                        .optional(),

                                                // [TEAM_INVITATION] 관련 (이미 documented: teamName,
                                                // teamMemberName),
                                                // [CHATTING] 관련 추가 필드
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.chatRoomId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("채팅방 아이디 (채팅 알림일 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.chatSenderLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "채팅 발신자의 로고 이미지 경로 (채팅 알림일 경우)")
                                                        .optional(),

                                                // [MATCHING] 관련 추가 필드
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.matchingId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("매칭 ID (매칭 알림/공고 지원 알림일 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.matchingTargetLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "매칭/공고 지원 상대방의 로고 이미지 경로 (매칭 알림/공고 지원일 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.matchingTargetName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("매칭 상대방의 이름 (매칭 알림인 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("공고 포지션 대분류 이름 (공고 알림인 경우)")
                                                        .optional(),

                                                // 이미 정의된 필드 (기존)
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름 (팀 초대, 팀 삭제 알림 등)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.teamMemberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "팀 관련 알림에서 팀원의 이름 (팀 초대, 팀 삭제 요청 등)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.chatSenderName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("메시지 발신자 이름 (채팅 알림인 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.itemId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("인증 처리된 객체의 PK")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.itemType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "인증 처리된 객체의 타입 (ACTIVITY, EDUCATION, AWARDS, LICENSE)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "방문자의 유저 아이디 (PROFILE_VISITOR)인 경우에")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.visitorCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description(
                                                                "프로필/팀 방문자 수 (프로필 방문자/팀 방문자 알림인 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.notificationItems[].notificationDetails.visitedType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "프로필/팀 방문자 타입 (PROFILE_VISITOR, TEAM_VISITOR)")
                                                        .optional())))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<NotificationItems> actual =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        final CommonResponse<NotificationItems> expected =
                CommonResponse.onSuccess(notificationItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 내가 수신한 알림을 읽음 상태로 처리할 수 있다.")
    @Test
    void updateNotificationReadStatus() throws Exception {
        // given
        final NotificationResponseDTO.ReadNotificationResponse readNotificationResponse =
                ReadNotificationResponse.builder()
                        .notificationId("알림 ID")
                        .notificationReadStatus(NotificationReadStatus.READ)
                        .build();

        // when
        when(notificationService.readNotification(anyLong(), any()))
                .thenReturn(readNotificationResponse);

        final ResultActions resultActions = performUpdateNotificationReadStatus("random_id");

        // then
        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("notificationId")
                                                        .description("알림 ID (문자열)")),
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
                                                fieldWithPath("result")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("결과 데이터 객체")
                                                        .attributes(field("constraint", "object")),
                                                fieldWithPath("result.notificationId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("알림 아이디")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.notificationReadStatus")
                                                        .type(JsonFieldType.STRING)
                                                        .description("알림 읽음 상태")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();
    }
}
