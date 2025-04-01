package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.business.service.log.TeamLogCommentService;
import liaison.linkit.team.presentation.log.TeamLogCommentController;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamLogCommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class TeamLogCommentControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private TeamLogCommentService teamLogCommentService;

    @BeforeEach
    void setUp() {
        // Setup JWT provider mocks
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(jwtProvider).validateAccessToken(any());
    }

    @Test
    @DisplayName("팀 로그 댓글 작성 API 테스트")
    void addTeamLogCommentTest() throws Exception {
        // Given
        final long teamLogId = 1L;
        final TeamLogCommentRequestDTO.AddTeamLogCommentRequest request =
                new TeamLogCommentRequestDTO.AddTeamLogCommentRequest("댓글 내용입니다.", null);

        final TeamLogCommentResponseDTO.AddTeamLogCommentResponse response =
                TeamLogCommentResponseDTO.AddTeamLogCommentResponse.builder()
                        .commentId(1L)
                        .teamLogId(teamLogId)
                        .authorName("테스터")
                        .authorProfileImagePath("profile/image/path.jpg")
                        .emailId("tester@example.com")
                        .content("댓글 내용입니다.")
                        .createdAt("방금 전")
                        .isParentComment(true)
                        .parentCommentId(null)
                        .build();

        given(teamLogCommentService.addTeamLogComment(anyLong(), anyLong(), any()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/team/log/{teamLogId}/comment", teamLogId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamLogId")
                                                .description("댓글을 작성할 팀 로그 ID")
                                                .attributes(field("constraint", "숫자 (NOT NULL)"))),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer 인증 토큰")
                                                .attributes(
                                                        field(
                                                                "constraint",
                                                                "Bearer Access Token"))),
                                requestFields(
                                        fieldWithPath("content").description("댓글 내용"),
                                        fieldWithPath("parentCommentId")
                                                .description(
                                                        "부모 댓글 ID (대댓글인 경우에만 필요, null이면 부모 댓글)")),
                                responseFields(
                                        fieldWithPath("isSuccess").description("요청 성공 여부"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.commentId").description("생성된 댓글 ID"),
                                        fieldWithPath("result.teamLogId")
                                                .description("댓글이 작성된 팀 로그 ID"),
                                        fieldWithPath("result.authorName").description("댓글 작성자 이름"),
                                        fieldWithPath("result.authorProfileImagePath")
                                                .description("댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.emailId")
                                                .description("댓글 작성자 이메일 ID"),
                                        fieldWithPath("result.content").description("댓글 내용"),
                                        fieldWithPath("result.createdAt").description("댓글 작성 시간"),
                                        fieldWithPath("result.isParentComment")
                                                .description("부모 댓글 여부"),
                                        fieldWithPath("result.parentCommentId")
                                                .description("부모 댓글 ID (대댓글인 경우)"))));
    }

    @Test
    @DisplayName("팀 로그 댓글 수정 API 테스트")
    void updateTeamLogCommentTest() throws Exception {
        // Given
        final long commentId = 1L;
        final TeamLogCommentRequestDTO.UpdateTeamLogCommentRequest request =
                new TeamLogCommentRequestDTO.UpdateTeamLogCommentRequest("수정된 댓글 내용입니다.", null);

        final TeamLogCommentResponseDTO.UpdateTeamLogCommentResponse response =
                TeamLogCommentResponseDTO.UpdateTeamLogCommentResponse.builder()
                        .commentId(commentId)
                        .teamLogId(1L)
                        .authorName("테스터")
                        .authorProfileImagePath("profile/image/path.jpg")
                        .emailId("tester@example.com")
                        .content("수정된 댓글 내용입니다.")
                        .createdAt("1시간 전")
                        .isParentComment(true)
                        .parentCommentId(null)
                        .build();

        given(teamLogCommentService.updateTeamLogComment(anyLong(), anyLong(), any()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/team/log/comment/{teamLogCommentId}", commentId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamLogCommentId")
                                                .description("수정할 팀 로그 댓글 ID")
                                                .attributes(field("constraint", "숫자 (NOT NULL)"))),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer 인증 토큰")
                                                .attributes(
                                                        field(
                                                                "constraint",
                                                                "Bearer Access Token"))),
                                requestFields(
                                        fieldWithPath("content").description("수정할 댓글 내용"),
                                        fieldWithPath("parentCommentId")
                                                .description(
                                                        "부모 댓글 ID (대댓글인 경우에만 필요, null이면 부모 댓글)")),
                                responseFields(
                                        fieldWithPath("isSuccess").description("요청 성공 여부"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.commentId").description("수정된 댓글 ID"),
                                        fieldWithPath("result.teamLogId")
                                                .description("댓글이 작성된 팀 로그 ID"),
                                        fieldWithPath("result.authorName").description("댓글 작성자 이름"),
                                        fieldWithPath("result.authorProfileImagePath")
                                                .description("댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.emailId")
                                                .description("댓글 작성자 이메일 ID"),
                                        fieldWithPath("result.content").description("수정된 댓글 내용"),
                                        fieldWithPath("result.createdAt").description("댓글 작성 시간"),
                                        fieldWithPath("result.isParentComment")
                                                .description("부모 댓글 여부"),
                                        fieldWithPath("result.parentCommentId")
                                                .description("부모 댓글 ID (대댓글인 경우)"))));
    }

    @Test
    @DisplayName("팀 로그 댓글 조회 API 테스트")
    void getPageTeamLogCommentsTest() throws Exception {
        // given
        final long teamLogId = 1L;

        // Mock 응답 데이터 생성
        List<TeamLogCommentResponseDTO.ReplyResponse> replies = new ArrayList<>();
        replies.add(
                TeamLogCommentResponseDTO.ReplyResponse.builder()
                        .id(2L)
                        .authorProfileImagePath("profile/image/path2.jpg")
                        .authorName("댓글작성자")
                        .emailId("유저 아이디")
                        .createdAt("30분 전")
                        .content("대댓글입니다.")
                        .isUpdated("false")
                        .isDeleted(false)
                        .isQuitAccount(false)
                        .isAuthor(false)
                        .build());

        List<TeamLogCommentResponseDTO.ParentCommentResponse> comments = new ArrayList<>();
        comments.add(
                TeamLogCommentResponseDTO.ParentCommentResponse.builder()
                        .id(1L)
                        .authorProfileImagePath("profile/image/path.jpg")
                        .authorName("테스터")
                        .emailId("tester")
                        .createdAt("1시간 전")
                        .content("부모 댓글입니다.")
                        .isUpdated("false")
                        .isDeleted(false)
                        .isQuitAccount(false)
                        .isAuthor(true)
                        .replies(replies)
                        .build());

        // 커서 기반 응답으로 변경
        CursorResponse<TeamLogCommentResponseDTO.ParentCommentResponse> cursorResponse =
                CursorResponse.<TeamLogCommentResponseDTO.ParentCommentResponse>builder()
                        .content(comments)
                        .nextCursor("1") // 다음 페이지의 커서 값 (마지막 댓글 ID)
                        .hasNext(true) // 다음 페이지 존재 여부
                        .build();

        given(teamLogCommentService.getPageTeamLogComments(Optional.of(1L), teamLogId, null, 10))
                .willReturn(cursorResponse);

        // when
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/team/log/{teamLogId}/comments", teamLogId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE)
                                .param("size", "10")); // page 파라미터를 제거하고 cursor가 없는 첫 요청으로 변경

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamLogId").description("조회할 팀 로그 ID")),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("커서 값 (다음 페이지 조회 시 사용)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("조회할 댓글 개수")
                                                .optional()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer 인증 토큰")
                                                .attributes(
                                                        field(
                                                                "constraint",
                                                                "Bearer Access Token"))),
                                responseFields(
                                        fieldWithPath("isSuccess").description("요청 성공 여부"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.content").description("댓글 목록"),
                                        fieldWithPath("result.content[].id").description("댓글 ID"),
                                        fieldWithPath("result.content[].authorProfileImagePath")
                                                .description("댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.content[].authorName")
                                                .description("댓글 작성자 이름"),
                                        fieldWithPath("result.content[].emailId")
                                                .description("댓글 작성자 유저 아이디"),
                                        fieldWithPath("result.content[].createdAt")
                                                .description("댓글 작성 시간"),
                                        fieldWithPath("result.content[].content")
                                                .description("댓글 내용"),
                                        fieldWithPath("result.content[].isUpdated")
                                                .description("댓글 수정 여부"),
                                        fieldWithPath("result.content[].isDeleted")
                                                .description("댓글 삭제 여부"),
                                        fieldWithPath("result.content[].isQuitAccount")
                                                .description("탈퇴한 계정 여부"),
                                        fieldWithPath("result.content[].isAuthor")
                                                .description("현재 사용자가 댓글 작성자인지 여부"),
                                        fieldWithPath("result.content[].replies")
                                                .description("대댓글 목록"),
                                        fieldWithPath("result.content[].replies[].id")
                                                .description("대댓글 ID"),
                                        fieldWithPath("result.content[].replies[].authorName")
                                                .description("대댓글 작성자 이름"),
                                        fieldWithPath("result.content[].replies[].emailId")
                                                .description("대댓글 작성자 이메일 ID"),
                                        fieldWithPath(
                                                        "result.content[].replies[].authorProfileImagePath")
                                                .description("대댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.content[].replies[].content")
                                                .description("대댓글 내용"),
                                        fieldWithPath("result.content[].replies[].createdAt")
                                                .description("대댓글 작성 시간"),
                                        fieldWithPath("result.content[].replies[].isUpdated")
                                                .description("대댓글 수정 여부"),
                                        fieldWithPath("result.content[].replies[].isDeleted")
                                                .description("대댓글 삭제 여부"),
                                        fieldWithPath("result.content[].replies[].isQuitAccount")
                                                .description("탈퇴한 계정 여부"),
                                        fieldWithPath("result.content[].replies[].isAuthor")
                                                .description("현재 사용자가 대댓글 작성자인지 여부"),
                                        fieldWithPath("result.nextCursor")
                                                .description("다음 페이지 조회 시 사용할 커서 값"),
                                        fieldWithPath("result.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("팀 로그 댓글 삭제 API 테스트")
    void deleteTeamLogCommentTest() throws Exception {
        // Given
        final long commentId = 1L;

        final TeamLogCommentResponseDTO.DeleteTeamLogCommentResponse response =
                TeamLogCommentResponseDTO.DeleteTeamLogCommentResponse.builder()
                        .commentId(commentId)
                        .teamLogId(1L)
                        .build();

        given(teamLogCommentService.deleteTeamLogComment(anyLong(), anyLong()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/team/log/comment/{teamLogCommentId}/delete", commentId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamLogCommentId")
                                                .description("삭제할 팀 로그 댓글 ID")),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer 인증 토큰")
                                                .attributes(
                                                        field(
                                                                "constraint",
                                                                "Bearer Access Token"))),
                                responseFields(
                                        fieldWithPath("isSuccess").description("요청 성공 여부"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result.commentId").description("삭제된 댓글 ID"),
                                        fieldWithPath("result.teamLogId")
                                                .description("댓글이 작성된 팀 로그 ID"))));
    }

    @Test
    @DisplayName("팀 로그 댓글 커서 기반 다음 페이지 조회 API 테스트")
    void getNextPageTeamLogCommentsWithCursorTest() throws Exception {
        // Given
        final long teamLogId = 1L;
        final String cursor = "1"; // 이전 페이지의 마지막 댓글 ID

        // Mock 응답 데이터 생성
        List<TeamLogCommentResponseDTO.ReplyResponse> replies = new ArrayList<>();
        replies.add(
                TeamLogCommentResponseDTO.ReplyResponse.builder()
                        .id(2L)
                        .authorName("댓글작성자")
                        .emailId("replier@example.com")
                        .authorProfileImagePath("profile/image/path2.jpg")
                        .content("대댓글입니다.")
                        .createdAt("30분 전")
                        .isUpdated("false")
                        .isDeleted(false)
                        .isQuitAccount(false)
                        .isAuthor(false)
                        .build());

        List<TeamLogCommentResponseDTO.ParentCommentResponse> comments = new ArrayList<>();
        comments.add(
                TeamLogCommentResponseDTO.ParentCommentResponse.builder()
                        .id(1L)
                        .authorName("댓글작성자")
                        .emailId("commenter@example.com")
                        .authorProfileImagePath("profile/image/path1.jpg")
                        .content("부모 댓글입니다.")
                        .createdAt("1시간 전")
                        .isUpdated("false")
                        .isDeleted(false)
                        .isQuitAccount(false)
                        .isAuthor(false)
                        .replies(replies)
                        .build());

        CursorResponse<TeamLogCommentResponseDTO.ParentCommentResponse> cursorResponse =
                CursorResponse.<TeamLogCommentResponseDTO.ParentCommentResponse>builder()
                        .content(comments)
                        .hasNext(false)
                        .nextCursor(null)
                        .build();

        given(teamLogCommentService.getPageTeamLogComments(Optional.of(1L), teamLogId, cursor, 10))
                .willReturn(cursorResponse);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/team/log/{teamLogId}/comments", teamLogId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE)
                                .param("cursor", "0")
                                .param("size", "10"));

        // Then
        resultActions.andExpect(status().isOk()).andDo(print());
    }
}
