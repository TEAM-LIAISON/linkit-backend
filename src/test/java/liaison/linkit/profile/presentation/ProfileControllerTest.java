//package liaison.linkit.profile.presentation;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import groovy.util.logging.Slf4j;
//import jakarta.servlet.http.Cookie;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
//import liaison.linkit.profile.presentation.profile.ProfileController;
//import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
//import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
//import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
//import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
//import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
//import liaison.linkit.profile.service.ProfileService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.test.web.servlet.ResultActions;
//
//@WebMvcTest(ProfileController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//@Slf4j
//class ProfileControllerTest extends ControllerTest {
//
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private ProfileService profileService;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//    }
//
//    private ResultActions performGetProfileLeftMenu() throws Exception {
//        return mockMvc.perform(
//                get("/api/v1/profile/left/menu")
//                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                        .cookie(COOKIE)
//        );
//    }
//
//    @DisplayName("회원이 나의 프로필 왼쪽 메뉴를 조회할 수 있다.")
//    @Test
//    void getProfileLeftMenu() throws Exception {
//        // given
//
//        final ProfileCompletionMenu profileCompletionMenu =
//                new ProfileCompletionMenu(100);
//
//        final ProfileCurrentStateItem firstProfileCurrentStateItem
//                = new ProfileCurrentStateItem("팀 찾는 중");
//
//        final ProfileCurrentStateItem secondProfileCurrentStateItem
//                = new ProfileCurrentStateItem("공모전 준비 중");
//
//        final List<ProfileCurrentStateItem> profileCurrentStates = Arrays.asList(firstProfileCurrentStateItem, secondProfileCurrentStateItem);
//
//        final RegionDetail regionDetail =
//                new RegionDetail("서울특별시", "강남구");
//
//        final ProfileInformMenu profileInformMenu =
//                new ProfileInformMenu(
//                        new ArrayList<>(),
//                        "프로필 이미지 경로",
//                        "권동민",
//                        true,
//                        "포지션 대분류",
//
//                )
//
//        final ProfileBooleanMenu profileBooleanMenu =
//                new ProfileBooleanMenu(
//                        false,
//                        false,
//                        false,
//                        false,
//                        false,
//                        false,
//                        false,
//                        false);
//
//        final ProfileLeftMenu profileLeftMenu =
//                new ProfileLeftMenu(
//                        profileCompletionMenu,
//                        profileInformMenu,
//                        profileBooleanMenu
//                );
//
//        // when
//        when(profileService.getProfileLeftMenu(anyLong())).thenReturn(profileLeftMenu);
//        // then
//    }
//}
