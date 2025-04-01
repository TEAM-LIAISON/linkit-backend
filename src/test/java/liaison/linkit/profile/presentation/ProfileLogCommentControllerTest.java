package liaison.linkit.profile.presentation;

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
import liaison.linkit.profile.business.service.ProfileLogCommentService;
import liaison.linkit.profile.presentation.log.ProfileLogCommentController;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
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

@WebMvcTest(ProfileLogCommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileLogCommentControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileLogCommentService profileLogCommentService;

    @BeforeEach
    void setUp() {
        // Setup JWT provider mocks
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(jwtProvider).validateAccessToken(any());
    }

    @Test
    @DisplayName("프로필 로그 댓글 작성 API 테스트")
    void addProfileLogCommentTest() throws Exception {
        // Given
        final long profileLogId = 1L;
        final ProfileLogCommentRequestDTO.AddProfileLogCommentRequest request =
                new ProfileLogCommentRequestDTO.AddProfileLogCommentRequest("댓글 내용입니다.", null);

        final ProfileLogCommentResponseDTO.AddProfileLogCommentResponse response =
                ProfileLogCommentResponseDTO.AddProfileLogCommentResponse.builder()
                        .commentId(1L)
                        .profileLogId(profileLogId)
                        .authorName("테스터")
                        .authorProfileImagePath("profile/image/path.jpg")
                        .emailId("tester@example.com")
                        .content("댓글 내용입니다.")
                        .createdAt("방금 전")
                        .isParentComment(true)
                        .parentCommentId(null)
                        .build();

        given(profileLogCommentService.addProfileLogComment(anyLong(), anyLong(), any()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/profile/log/{profileLogId}/comment", profileLogId)
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
                                        parameterWithName("profileLogId")
                                                .description("댓글을 작성할 프로필 로그 ID")
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
                                        fieldWithPath("result.profileLogId")
                                                .description("댓글이 작성된 프로필 로그 ID"),
                                        fieldWithPath("result.authorProfileId")
                                                .description("댓글 작성자 프로필 ID"),
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
    @DisplayName("프로필 로그 댓글 수정 API 테스트")
    void updateProfileLogCommentTest() throws Exception {
        // Given
        final long commentId = 1L;
        final ProfileLogCommentRequestDTO.UpdateProfileLogCommentRequest request =
                new ProfileLogCommentRequestDTO.UpdateProfileLogCommentRequest(
                        "수정된 댓글 내용입니다.", null);

        final ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse response =
                ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse.builder()
                        .commentId(commentId)
                        .profileLogId(1L)
                        .authorName("테스터")
                        .authorProfileImagePath("profile/image/path.jpg")
                        .emailId("tester@example.com")
                        .content("수정된 댓글 내용입니다.")
                        .createdAt("1시간 전")
                        .isParentComment(true)
                        .parentCommentId(null)
                        .build();

        given(profileLogCommentService.updateProfileLogComment(anyLong(), anyLong(), any()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/profile/log/comment/{profileLogCommentId}", commentId)
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
                                        parameterWithName("profileLogCommentId")
                                                .description("수정할 프로필 로그 댓글 ID")
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
                                        fieldWithPath("result.profileLogId")
                                                .description("댓글이 작성된 프로필 로그 ID"),
                                        fieldWithPath("result.authorProfileId")
                                                .description("댓글 작성자 프로필 ID"),
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
    @DisplayName("프로필 로그 댓글 조회 API 테스트")
    void getPageProfileLogCommentsTest() throws Exception {
        // Given
        final long profileLogId = 1L;

        // Mock 응답 데이터 생성
        List<ProfileLogCommentResponseDTO.ReplyResponse> replies = new ArrayList<>();
        replies.add(
                ProfileLogCommentResponseDTO.ReplyResponse.builder()
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

        List<ProfileLogCommentResponseDTO.ParentCommentResponse> comments = new ArrayList<>();
        comments.add(
                ProfileLogCommentResponseDTO.ParentCommentResponse.builder()
                        .id(1L)
                        .authorName("테스터")
                        .emailId("tester@example.com")
                        .authorProfileImagePath("profile/image/path.jpg")
                        .content("부모 댓글입니다.")
                        .createdAt("1시간 전")
                        .isUpdated("false")
                        .isDeleted(false)
                        .isQuitAccount(false)
                        .isAuthor(true)
                        .replies(replies)
                        .build());

        ProfileLogCommentResponseDTO.PageResponse response =
                ProfileLogCommentResponseDTO.PageResponse.builder()
                        .comments(comments)
                        .totalElements(1)
                        .totalPages(1)
                        .currentPage(0)
                        .hasNext(false)
                        .build();

        given(
                        profileLogCommentService.getPageProfileLogComments(
                                Optional.of(1L), profileLogId, 0, 10))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        get("/api/v1/profile/log/{profileLogId}/comments", profileLogId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE)
                                .param("page", "0")
                                .param("size", "10"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("profileLogId")
                                                .description("조회할 프로필 로그 ID")),
                                queryParameters(
                                        parameterWithName("page")
                                                .description("페이지 번호 (0부터 시작)")
                                                .optional(),
                                        parameterWithName("size").description("페이지 크기").optional()),
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
                                        fieldWithPath("result.comments").description("댓글 목록"),
                                        fieldWithPath("result.comments[].id").description("댓글 ID"),
                                        fieldWithPath("result.comments[].authorName")
                                                .description("댓글 작성자 이름"),
                                        fieldWithPath("result.comments[].emailId")
                                                .description("댓글 작성자 이메일 ID"),
                                        fieldWithPath("result.comments[].authorProfileImagePath")
                                                .description("댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.comments[].content")
                                                .description("댓글 내용"),
                                        fieldWithPath("result.comments[].createdAt")
                                                .description("댓글 작성 시간"),
                                        fieldWithPath("result.comments[].isUpdated")
                                                .description("댓글 수정 여부"),
                                        fieldWithPath("result.comments[].isDeleted")
                                                .description("댓글 삭제 여부"),
                                        fieldWithPath("result.comments[].isQuitAccount")
                                                .description("탈퇴한 계정 여부"),
                                        fieldWithPath("result.comments[].isAuthor")
                                                .description("현재 사용자가 댓글 작성자인지 여부"),
                                        fieldWithPath("result.comments[].replies")
                                                .description("대댓글 목록"),
                                        fieldWithPath("result.comments[].replies[].id")
                                                .description("대댓글 ID"),
                                        fieldWithPath("result.comments[].replies[].authorName")
                                                .description("대댓글 작성자 이름"),
                                        fieldWithPath("result.comments[].replies[].emailId")
                                                .description("대댓글 작성자 이메일 ID"),
                                        fieldWithPath(
                                                        "result.comments[].replies[].authorProfileImagePath")
                                                .description("대댓글 작성자 프로필 이미지 경로"),
                                        fieldWithPath("result.comments[].replies[].content")
                                                .description("대댓글 내용"),
                                        fieldWithPath("result.comments[].replies[].createdAt")
                                                .description("대댓글 작성 시간"),
                                        fieldWithPath("result.comments[].replies[].isUpdated")
                                                .description("대댓글 수정 여부"),
                                        fieldWithPath("result.comments[].replies[].isDeleted")
                                                .description("대댓글 삭제 여부"),
                                        fieldWithPath("result.comments[].replies[].isQuitAccount")
                                                .description("탈퇴한 계정 여부"),
                                        fieldWithPath("result.comments[].replies[].isAuthor")
                                                .description("현재 사용자가 대댓글 작성자인지 여부"),
                                        fieldWithPath("result.totalElements")
                                                .description("전체 댓글 수"),
                                        fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("result.currentPage")
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("프로필 로그 댓글 삭제 API 테스트")
    void deleteProfileLogCommentTest() throws Exception {
        // Given
        final long commentId = 1L;

        final ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse response =
                ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse.builder()
                        .commentId(commentId)
                        .profileLogId(1L)
                        .build();

        given(profileLogCommentService.deleteProfileLogComment(anyLong(), anyLong()))
                .willReturn(response);

        // When
        final ResultActions resultActions =
                mockMvc.perform(
                        post("/api/v1/profile/log/comment/{profileLogCommentId}/delete", commentId)
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
                                        parameterWithName("profileLogCommentId")
                                                .description("삭제할 프로필 로그 댓글 ID")),
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
                                        fieldWithPath("result.profileLogId")
                                                .description("댓글이 작성된 프로필 로그 ID"))));
    }
}
