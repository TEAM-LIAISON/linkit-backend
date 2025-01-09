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
import java.time.LocalDateTime;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
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
                        NotificationItem.builder()
                                .id("알림 ID 1")
                                .notificationType(NotificationType.TEAM_INVITATION)
                                .notificationStatus(NotificationStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .modifiedAt(LocalDateTime.now())
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.builder()
                                                .teamName("테스트 팀")
                                                .build()
                                )
                                .build(),
                        NotificationItem.builder()
                                .id("알림 ID 2")
                                .notificationType(NotificationType.CHATTING)
                                .notificationStatus(NotificationStatus.READ)
                                .createdAt(LocalDateTime.now())
                                .modifiedAt(LocalDateTime.now())
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.builder()
                                                .senderName("보낸 사람 이름")
                                                .receiverName("받는 사람 이름")
                                                .lastMessage("마지막 메시지 내용")
                                                .build()
                                )
                                .build(),
                        NotificationItem.builder()
                                .id("알림 ID 3")
                                .notificationType(NotificationType.MATCHING)
                                .notificationStatus(NotificationStatus.SENT)
                                .createdAt(LocalDateTime.now())
                                .modifiedAt(LocalDateTime.now())
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.builder()
                                                .matchingSenderName("매칭 발신자 이름")
                                                .matchingReceiverName("매칭 수신자 이름")
                                                .matchingStatus("매칭 완료")
                                                .build()
                                )
                                .build(),
                        NotificationItem.builder()
                                .id("알림 ID 4")
                                .notificationType(NotificationType.SYSTEM)
                                .notificationStatus(NotificationStatus.READ)
                                .createdAt(LocalDateTime.now())
                                .modifiedAt(LocalDateTime.now())
                                .notificationDetails(
                                        NotificationResponseDTO.NotificationDetails.builder()
                                                .systemMessage("시스템 메시지 내용")
                                                .build()
                                )
                                .build()
                ))
                .build();

        // when
        when(notificationService.getNotificationItems(any())).thenReturn(notificationItems);

        final ResultActions resultActions = performGetNotificationItems();

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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("결과 데이터 객체")
                                                .attributes(field("constraint", "object")),
                                        fieldWithPath("result.notificationItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("알림 목록 배열")
                                                .attributes(field("constraint", "배열")),
                                        fieldWithPath("result.notificationItems[].id")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 ID"),
                                        fieldWithPath("result.notificationItems[].notificationType")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 유형 - 변동 가능성 있음 (MATCHING, CHATTING, TEAM_INVITATION, SYSTEM)"),
                                        fieldWithPath("result.notificationItems[].notificationStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 상태 - 변동 가능성 있음 (PENDING, SENT, FAILED, READ)"),
                                        fieldWithPath("result.notificationItems[].createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 생성 시간"),
                                        fieldWithPath("result.notificationItems[].modifiedAt")
                                                .type(JsonFieldType.STRING)
                                                .description("알림 수정 시간"),
                                        fieldWithPath("result.notificationItems[].notificationDetails")
                                                .type(JsonFieldType.OBJECT)
                                                .description("알림 타입별 상세 정보"),
                                        // TEAM_INVITATION 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름 (팀 초대 알림인 경우)")
                                                .optional(),
                                        // CHATTING 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.senderName")
                                                .type(JsonFieldType.STRING)
                                                .description("메시지 발신자 이름 (채팅 알림인 경우)")
                                                .optional(),
                                        fieldWithPath("result.notificationItems[].notificationDetails.receiverName")
                                                .type(JsonFieldType.STRING)
                                                .description("메시지 수신자 이름 (채팅 알림인 경우)")
                                                .optional(),
                                        fieldWithPath("result.notificationItems[].notificationDetails.lastMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("마지막 메시지 내용 (채팅 알림인 경우)")
                                                .optional(),
                                        // MATCHING 알림 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.matchingSenderName")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 발신자 이름 (매칭 알림인 경우)")
                                                .optional(),
                                        fieldWithPath("result.notificationItems[].notificationDetails.matchingReceiverName")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 수신자 이름 (매칭 알림인 경우)")
                                                .optional(),
                                        fieldWithPath("result.notificationItems[].notificationDetails.matchingStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("매칭 상태 (매칭 알림인 경우)")
                                                .optional(),
                                        // SYSTEM 알림 관련 필드
                                        fieldWithPath("result.notificationItems[].notificationDetails.systemMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("시스템 메시지 (시스템 알림인 경우)")
                                                .optional()
                                )
                        )
                ).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<NotificationItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<NotificationItems>>() {
                }
        );

        final CommonResponse<NotificationItems> expected = CommonResponse.onSuccess(notificationItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
