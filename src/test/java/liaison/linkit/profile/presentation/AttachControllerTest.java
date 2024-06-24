package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.attach.AttachFileCreateRequest;
import liaison.linkit.profile.dto.request.attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.request.attach.AttachUrlUpdateRequest;
import liaison.linkit.profile.service.AttachService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AttachControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttachService attachService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(attachService).validateAttachUrlByMember(anyLong());
        doNothing().when(attachService).validateAttachFileByMember(anyLong());
    }

    private void makeAttachUrl() throws Exception {
        final AttachUrlCreateRequest attachUrlCreateRequest1 = new AttachUrlCreateRequest(
                "깃허브",
                "https://github.com/TEAM-LIAISON"
        );

        final AttachUrlCreateRequest attachUrlCreateRequest2 = new AttachUrlCreateRequest(
                "노션",
                "https://www.notion.no"
        );

        final List<AttachUrlCreateRequest> attachUrlCreateRequestList = Arrays.asList(attachUrlCreateRequest1, attachUrlCreateRequest2);

        doNothing().when(attachService).saveUrl(1L, attachUrlCreateRequestList);
        performPostUrlRequest(attachUrlCreateRequestList);
    }

    private ResultActions performPostUrlRequest(final List<AttachUrlCreateRequest> attachUrlCreateRequests) throws Exception {
        return mockMvc.perform(
                post("/attach/url")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attachUrlCreateRequests))
        );
    }

    private ResultActions performPutUrlRequest(
            final int attachUrlId,
            final AttachUrlUpdateRequest attachUrlUpdateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.put("/attach/url/{attachUrlId}", attachUrlId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attachUrlUpdateRequest))
        );
    }

    private ResultActions performDeleteUrlRequest(
            final int attachUrlId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/attach/url/{attachUrlId}", attachUrlId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performPostFileRequest(final AttachFileCreateRequest attachFileCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/attach/file")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attachFileCreateRequest))
        );
    }

    @DisplayName("단일 첨부(웹 링크)를 생성할 수 있다.")
    @Test
    void createAttachUrl() throws Exception {
        // given
        final AttachUrlCreateRequest attachUrlCreateRequest1 = new AttachUrlCreateRequest(
                "깃허브",
                "https://github.com/TEAM-LIAISON"
        );

        final AttachUrlCreateRequest attachUrlCreateRequest2 = new AttachUrlCreateRequest(
                "노션",
                "https://www.notion.no"
        );

        final List<AttachUrlCreateRequest> attachUrlCreateRequestList = Arrays.asList(attachUrlCreateRequest1, attachUrlCreateRequest2);

//        doNothing().when(attachService).saveUrl(anyLong(), attachUrlCreateRequestList);
        // when
        final ResultActions resultActions = performPostUrlRequest(attachUrlCreateRequestList);

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("refresh-token")
                                                .description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization")
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                ),
                                requestFields(
                                        fieldWithPath("[].attachUrlName")
                                                .type(JsonFieldType.STRING)
                                                .description("웹 링크 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].attachUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("웹 링크")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("단일 첨부(웹 링크)를 수정할 수 있다.")
    @Test
    void updateAttachUrl() throws Exception {
        // given
        final AttachUrlUpdateRequest attachUrlUpdateRequest = new AttachUrlUpdateRequest(
                "웹페이지",
                "https://knowhow.ceo/"
        );

        // when
        final ResultActions resultActions = performPutUrlRequest(1, attachUrlUpdateRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("attachUrlId")
                                                .description("첨부 URL ID")
                                ),
                                requestFields(
                                        fieldWithPath("attachUrlName")
                                                .type(JsonFieldType.STRING)
                                                .description("웹 링크 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("attachUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("웹 링크")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("단일 첨부(웹 링크)를 삭제할 수 있다.")
    @Test
    void deleteAttachUrl() throws Exception {
        // given
        makeAttachUrl();
        doNothing().when(attachService).validateAttachUrlByMember(anyLong());

        // when
        final ResultActions resultActions = performDeleteUrlRequest(1);

        // then
        verify(attachService).deleteUrl(1L, 1L);

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("attachUrlId")
                                        .description("첨부 URL ID")
                        )
                ));
    }

    @DisplayName("첨부(웹 파일)를 생성할 수 있다.")
    @Test
    void createAttachFile() throws Exception {
        // given
        final MockMultipartFile attachFile = new MockMultipartFile(
                "attachFile",
                "poster.pdf",
                "multipart/form-data",
                "PDF file content".getBytes()
        );

        MockMultipartHttpServletRequestBuilder customRestDocumentationRequestBuilder =
                RestDocumentationRequestBuilders.multipart("/attach/file", attachFile);
        // when

        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST, "/attach/file")
                .file(attachFile)
                .accept(APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("인증 토큰").attributes(field("constraint", "문자열(jwt)"))
                                ),
                                requestParts(
                                        partWithName("attachFile").description("첨부 파일. 지원되는 형식은 .pdf, .docx 등이 있습니다.")
                                )
                        )
                );
    }

}
