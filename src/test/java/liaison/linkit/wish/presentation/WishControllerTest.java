package liaison.linkit.wish.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.wish.service.WishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(WishController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class WishControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishService wishService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 내 이력서 찜하기 메서드
    private ResultActions performCreateWishToPrivateProfile(
            final int miniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/wish/private/profile/{miniProfileId}", miniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀 소개서 찜하기 메서드
    private ResultActions performCreateWishToTeamProfile(
            final int teamMiniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/wish/team/profile/{teamMiniProfileId}", teamMiniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 내 이력서 찜하기 취소 메서드
    private ResultActions performDeleteWishToPrivateProfile(
            final int miniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/wish/private/profile/{miniProfileId}", miniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀 소개서 찜하기 취소 메서드
    private ResultActions performDeleteWishToTeamProfile(
            final int teamMiniProfileId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/wish/team/profile/{teamMiniProfileId}", teamMiniProfileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 내 이력서 찜한 리스트 조회
    private ResultActions performGetPrivateProfileWishList() throws Exception {
        return mockMvc.perform(
                get("/wish/private/profile/list")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)

        );
    }

    // 팀 소개서 찜한 리스트 조회
    private ResultActions performGetTeamProfileWishList() throws Exception {
        return mockMvc.perform(
                get("/wish/team/profile/list")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("상대방의 내 이력서를 찜할 수 있다.")
    @Test
    void createWishToPrivateProfile() throws Exception {
        // given

        // when

        // then
    }

    @DisplayName("상대방의 팀 소개서를 찜할 수 있다.")
    @Test
    void createWishToTeamProfile() throws Exception {
        // given

        // when

        // then
    }

    @DisplayName("상대방의 내 이력서를 찜 취소할 수 있다.")
    @Test
    void cancelWishToPrivateProfile() throws Exception {
        // given

        // when

        // then
    }

    @DisplayName("상대방의 팀 소개서를 찜 취소할 수 있다.")
    @Test
    void cancelWishToTeamProfile() throws Exception {
        // given

        // when

        // then
    }

    @DisplayName("내가 찜한 내 이력서를 조회할 수 있다.")
    @Test
    void getPrivateProfileWishList() throws Exception {
        // given

        // when

        // then
    }

    @DisplayName("내가 찜한 팀 소개서를 조회할 수 있다.")
    @Test
    void getTeamProfileWishList() throws Exception {
        // given

        // when

        // then
    }
}
