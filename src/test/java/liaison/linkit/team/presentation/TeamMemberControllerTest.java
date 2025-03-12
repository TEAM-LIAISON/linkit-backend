package liaison.linkit.team.presentation;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.business.service.teamMember.TeamMemberService;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import liaison.linkit.team.domain.teamMember.type.TeamMemberRegisterType;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.UpdateManagingTeamStateRequest;
import liaison.linkit.team.presentation.teamMember.TeamMemberController;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.AddTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.RemoveTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.TeamJoinRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.UpdateTeamMemberTypeRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AcceptedTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AddTeamMemberResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.PendingTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.RemoveTeamMemberResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamJoinResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamOutResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.UpdateManagingTeamStateResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.UpdateTeamMemberTypeResponse;
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

@WebMvcTest(TeamMemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamMemberControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private TeamMemberService teamMemberService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamMemberViewItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                        "/api/v1/team/{teamCode}/members/view", teamCode));
    }

    private ResultActions performGetTeamMemberEditItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                                "/api/v1/team/{teamCode}/members/edit", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performPostTeamMember(
            final String teamCode, final AddTeamMemberRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamCode}/member", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performSetTeamMemberType(
            final String teamCode, final String emailId, final UpdateTeamMemberTypeRequest request)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/member/type/{emailId}", teamCode, emailId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performGetOutTeam(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete(
                                "/api/v1/team/{teamCode}/member/out", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performManagingTeamState(
            final String teamCode, final UpdateManagingTeamStateRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/managing/teamState", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveTeamMember(
            final String teamCode, final RemoveTeamMemberRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/member/remove", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performJoinTeam(final String teamCode, final TeamJoinRequest request)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/member/join", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    @DisplayName("회원이 팀의 팀 구성원 뷰어를 전체 조회할 수 있다.")
    @Test
    void getTeamMemberViewItems() throws Exception {
        // given
        final TeamMemberViewItems teamMemberViewItems =
                TeamMemberViewItems.builder()
                        .isTeamManager(true)
                        .acceptedTeamMemberItems(
                                Arrays.asList(
                                        AcceptedTeamMemberItem.builder()
                                                .emailId("회원 유저 아이디")
                                                .profileImagePath("프로필 이미지 경로")
                                                .memberName("회원 이름")
                                                .majorPosition("포지션 대분류")
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("지역 시/도")
                                                                .divisionName("지역 시/군/구")
                                                                .build())
                                                .teamMemberType(TeamMemberType.TEAM_OWNER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.ACCEPTED)
                                                .build(),
                                        AcceptedTeamMemberItem.builder()
                                                .emailId("회원 유저 아이디")
                                                .profileImagePath("프로필 이미지 경로2")
                                                .memberName("회원 이름")
                                                .majorPosition("포지션 대분류")
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("지역 시/도")
                                                                .divisionName("지역 시/군/구")
                                                                .build())
                                                .teamMemberType(TeamMemberType.TEAM_VIEWER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.ACCEPTED)
                                                .build()))
                        .build();

        // when
        when(teamMemberService.getTeamMemberViewItems(any(), any()))
                .thenReturn(teamMemberViewItems);

        final ResultActions resultActions = performGetTeamMemberViewItems("liaison");

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // BOOLEAN 값으로 수정
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
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
                                                        .description("결과 데이터"),
                                                fieldWithPath("result.isTeamManager")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 오너/관리자 여부"),
                                                fieldWithPath("result.acceptedTeamMemberItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("수락된 팀 멤버 목록"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원의 유저 아이디"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 정보"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 정보 시/도"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 정보 시/군/구"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "팀 멤버 유형 (예: TEAM_OWNER, TEAM_VIEWER)"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].teamMemberInviteState")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 멤버 초대 상태 (예: ACCEPTED)"))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberViewItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<TeamMemberViewItems>>() {});

        final CommonResponse<TeamMemberViewItems> expected =
                CommonResponse.onSuccess(teamMemberViewItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀 구성원으로 등록, 초대된 회원 정보들을 조회할 수 있다.")
    @Test
    void getTeamMemberEditItems() throws Exception {
        // given
        final TeamMemberItems teamMemberItems =
                TeamMemberItems.builder()
                        .isTeamOwner(true)
                        .isTeamManager(true)
                        .acceptedTeamMemberItems(
                                Arrays.asList(
                                        AcceptedTeamMemberItem.builder()
                                                .emailId("회원 유저 아이디 1")
                                                .profileImagePath("프로필 이미지 경로 1")
                                                .memberName("회원 이름 1")
                                                .majorPosition("포지션 대분류 1")
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("지역 시/도")
                                                                .divisionName("지역 시/군/구")
                                                                .build())
                                                .teamMemberType(TeamMemberType.TEAM_MANAGER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.ACCEPTED)
                                                .build(),
                                        AcceptedTeamMemberItem.builder()
                                                .emailId("회원 유저 아이디 2")
                                                .profileImagePath("프로필 이미지 경로 2")
                                                .memberName("회원 이름 2")
                                                .majorPosition("포지션 대분류 2")
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("지역 시/도")
                                                                .divisionName("지역 시/군/구")
                                                                .build())
                                                .teamMemberType(TeamMemberType.TEAM_VIEWER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.ACCEPTED)
                                                .build()))
                        .pendingTeamMemberItems(
                                Arrays.asList(
                                        PendingTeamMemberItem.builder()
                                                .teamMemberInvitationEmail("초대 발송 완료된 상대방의 이메일 1")
                                                .teamMemberType(TeamMemberType.TEAM_MANAGER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.PENDING)
                                                .build(),
                                        PendingTeamMemberItem.builder()
                                                .teamMemberInvitationEmail("초대 발송 완료된 상대방의 이메일 2")
                                                .teamMemberType(TeamMemberType.TEAM_VIEWER)
                                                .teamMemberInviteState(
                                                        TeamMemberInviteState.PENDING)
                                                .build()))
                        .build();

        // when
        when(teamMemberService.getTeamMemberItems(anyLong(), any())).thenReturn(teamMemberItems);

        final ResultActions resultActions = performGetTeamMemberEditItems("liaison");

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // BOOLEAN 값으로 수정
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
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
                                                        .description("결과 데이터"),
                                                fieldWithPath("result.isTeamOwner")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀의 오너인지 여부"),
                                                fieldWithPath("result.isTeamManager")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀의 관리자인지 여부 (오너 포함)"),
                                                fieldWithPath("result.acceptedTeamMemberItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("초대 수락 완료된 팀 멤버 목록"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원의 유저 아이디"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("포지션 대분류"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 정보"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 정보 시/도"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING) // 수정:
                                                        // divisionName은
                                                        // STRING 타입임
                                                        .description("지역 정보 시/군/구"), // 수정된 설명
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "팀 멤버 타입 (예: TEAM_MANAGER, TEAM_VIEWER 등)"),
                                                fieldWithPath(
                                                                "result.acceptedTeamMemberItems[].teamMemberInviteState")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "초대 상태 (예: ACCEPTED, ADMIN 등)"),
                                                fieldWithPath("result.pendingTeamMemberItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("초대 발송 완료된 팀 멤버 목록 (수락 대기 중)"),
                                                fieldWithPath(
                                                                "result.pendingTeamMemberItems[].teamMemberInvitationEmail")
                                                        .type(JsonFieldType.STRING)
                                                        .description("초대 발송한 상대방의 이메일"),
                                                fieldWithPath(
                                                                "result.pendingTeamMemberItems[].teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "팀 멤버 타입 (예: TEAM_MANAGER, TEAM_VIEWER 등)"),
                                                fieldWithPath(
                                                                "result.pendingTeamMemberItems[].teamMemberInviteState")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "초대 상태 (예: PENDING, REJECTED 등)"))))
                        .andReturn();
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<TeamMemberItems>>() {});

        final CommonResponse<TeamMemberItems> expected = CommonResponse.onSuccess(teamMemberItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀원을 초대할 수 있다.")
    @Test
    void addTeamMember() throws Exception {
        // given
        final TeamMemberResponseDTO.AddTeamMemberResponse addTeamMemberResponse =
                AddTeamMemberResponse.builder()
                        .teamCode("팀 아이디 (팀 코드)")
                        .invitedTeamMemberEmail("liaison@liaison.liaison")
                        .build();

        final AddTeamMemberRequest addTeamMemberRequest =
                AddTeamMemberRequest.builder()
                        .teamMemberInvitationEmail("liaison@liaison.liaison")
                        .teamMemberType(TeamMemberType.TEAM_MANAGER)
                        .build();

        // when
        when(teamMemberService.addTeamMember(anyLong(), any(), any()))
                .thenReturn(addTeamMemberResponse);

        final ResultActions resultActions = performPostTeamMember("liaison", addTeamMemberRequest);

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // BOOLEAN 값으로 수정
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestFields(
                                                fieldWithPath("teamMemberInvitationEmail")
                                                        .type(JsonFieldType.STRING)
                                                        .description("초대할 팀원 이메일"),
                                                fieldWithPath("teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름")),
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
                                                        .description("결과 데이터"),
                                                fieldWithPath("result.invitedTeamMemberEmail")
                                                        .type(JsonFieldType.STRING)
                                                        .description("초대된 팀원 이메일"),
                                                fieldWithPath("result.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)"))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamMemberResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<AddTeamMemberResponse>>() {});

        final CommonResponse<AddTeamMemberResponse> expected =
                CommonResponse.onSuccess(addTeamMemberResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀원의 관리 권한을 수정할 수 있다.")
    @Test
    void updateTeamMemberType() throws Exception {
        // given
        final TeamMemberRequestDTO.UpdateTeamMemberTypeRequest updateTeamMemberTypeRequest =
                UpdateTeamMemberTypeRequest.builder()
                        .teamMemberType(TeamMemberType.TEAM_VIEWER)
                        .build();

        final TeamMemberResponseDTO.UpdateTeamMemberTypeResponse updateTeamMemberTypeResponse =
                UpdateTeamMemberTypeResponse.builder()
                        .emailId("liaison@liaison.liaison")
                        .teamMemberType(TeamMemberType.TEAM_VIEWER)
                        .build();

        // when
        when(teamMemberService.updateTeamMemberType(anyLong(), any(), any(), any()))
                .thenReturn(updateTeamMemberTypeResponse);

        final ResultActions resultActions =
                performSetTeamMemberType("liaison", "kwondm7", updateTeamMemberTypeRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // BOOLEAN 값으로 수정
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("emailId")
                                                        .description("팀원의 이메일 ID")),
                                        requestFields(
                                                fieldWithPath("teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "변경하려고하는 팀원의 권한 명 (TEAM_MANAGER, TEAM_VIEWER)")),
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
                                                        .description("결과 데이터"),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀원의 이메일 ID"),
                                                fieldWithPath("result.teamMemberType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "변경 완료된 팀원의 관리 권한 (TEAM_MANAGER, TEAM_VIEWER)"))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamMemberTypeResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateTeamMemberTypeResponse>>() {});

        final CommonResponse<UpdateTeamMemberTypeResponse> expected =
                CommonResponse.onSuccess(updateTeamMemberTypeResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀을 스스로 나갈 수 있다.")
    @Test
    void getOutTeam() throws Exception {
        // given
        final TeamOutResponse teamOutResponse =
                TeamOutResponse.builder().teamCode("liaison").emailId("kwondm7").build();

        // when
        when(teamMemberService.getOutTeam(anyLong(), any())).thenReturn(teamOutResponse);

        final ResultActions resultActions = performGetOutTeam("liaison");
        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
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

                                                // 누락된 필드 추가
                                                fieldWithPath("result.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀을 나간 회원의 유저 아이디")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();
    }

    @DisplayName("관리자가 팀 삭제 요청에 대해 수락/거절을 진행할 수 있다.")
    @Test
    void updateManagingTeamState() throws Exception {
        // given
        final UpdateManagingTeamStateResponse updateManagingTeamStateResponse =
                UpdateManagingTeamStateResponse.builder()
                        .teamCode("liaison")
                        .isTeamLastDeleteRequester(true)
                        .build();

        final UpdateManagingTeamStateRequest updateManagingTeamStateRequest =
                UpdateManagingTeamStateRequest.builder()
                        .teamMemberManagingTeamState(TeamMemberManagingTeamState.ALLOW_DELETE)
                        .build();

        // when
        when(teamMemberService.updateManagingTeamState(anyLong(), any(), any()))
                .thenReturn(updateManagingTeamStateResponse);

        final ResultActions resultActions =
                performManagingTeamState("liaison", updateManagingTeamStateRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestFields(
                                                fieldWithPath("teamMemberManagingTeamState")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "팀 수락/거절 여부 (DENY_DELETE / ALLOW_DELETE)")
                                                        .attributes(field("constraint", "문자열"))),
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

                                                // 누락된 필드 추가
                                                fieldWithPath("result.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.isTeamLastDeleteRequester")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 삭제 마지막 요청자 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")))))
                        .andReturn();
    }

    @DisplayName("오너/관리자가 팀원을 삭제할 수 있다.")
    @Test
    void removeTeamMember() throws Exception {
        // given
        final RemoveTeamMemberRequest removeTeamMemberRequest =
                RemoveTeamMemberRequest.builder()
                        .teamMemberRegisterType(TeamMemberRegisterType.ACCEPTED)
                        .removeIdentifier("liaison")
                        .build();

        final RemoveTeamMemberResponse removeTeamMemberResponse =
                RemoveTeamMemberResponse.builder()
                        .teamCode("liaison")
                        .removedIdentifier("kwondm7@naver.com")
                        .build();

        // when
        when(teamMemberService.removeTeamMember(any(), anyLong(), any()))
                .thenReturn(removeTeamMemberResponse);

        final ResultActions resultActions =
                performRemoveTeamMember("liaison", removeTeamMemberRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestFields(
                                                fieldWithPath("teamMemberRegisterType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "초대된 팀원의 현재 상태 (ACCEPTED / PENDING) 2가지 케이스")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("removeIdentifier")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "ACCEPTED인 경우 -> emailId / PENDING인 경우 -> email")
                                                        .attributes(field("constraint", "문자열"))),
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

                                                // 누락된 필드 추가
                                                fieldWithPath("result.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.removedIdentifier")
                                                        .type(JsonFieldType.STRING)
                                                        .description("삭제된 회원의 식별자")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();
    }

    @DisplayName("회원이 팀 초대 수락을 할 수 있다.")
    @Test
    void joinTeam() throws Exception {
        // given
        final TeamJoinRequest teamJoinRequest = TeamJoinRequest.builder().isTeamJoin(true).build();

        final TeamJoinResponse teamJoinResponse =
                TeamJoinResponse.builder().teamCode("liaison").emailId("kwondm7").build();

        // when
        when(teamMemberService.joinTeam(anyLong(), any(), any())).thenReturn(teamJoinResponse);

        final ResultActions resultActions = performJoinTeam("liaison", teamJoinRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true)) // boolean으로 변경
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestFields(
                                                fieldWithPath("isTeamJoin")
                                                        .description("팀 초대 수락 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값"))),
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

                                                // 누락된 필드 추가
                                                fieldWithPath("result.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀을 나간 회원의 유저 아이디")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();
    }
}
