package liaison.linkit.team.service.teamMember;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.mail.service.TeamMemberInvitationMailService;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.exception.teamMember.TeamMemberInvitationDuplicateException;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.AddTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.ProfileInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMemberService {
    private final TeamQueryAdapter teamQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final TeamMemberInvitationQueryAdapter teamMemberInvitationQueryAdapter;

    private final TeamMemberMapper teamMemberMapper;
    private final ProfilePositionMapper profilePositionMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final TeamMemberInvitationCommandAdapter teamMemberInvitationCommandAdapter;

    private final RegionMapper regionMapper;

    private final TeamMemberInvitationMailService teamMemberInvitationMailService;

    public TeamMemberResponseDTO.TeamMemberItems getTeamMemberItems(final Long memberId, final String teamName) {
        // 1. 팀 조회
        final Team team = teamQueryAdapter.findByTeamName(teamName);

        // 2. 팀 멤버 조회
        List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(team.getId());

        // 3. 프로필 정보 수집 및 DTO 매핑
        List<TeamMemberResponseDTO.ProfileInformMenu> profileInformMenus = teamMembers.stream()
                .map(teamMember -> {
                    // 각 팀 멤버의 프로필 정보 조회
                    Profile profile = profileQueryAdapter.findByMemberId(teamMember.getMember().getId());

                    // 지역 정보 조회
                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
                        final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                        regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                    }

                    // 상태 정보 조회
                    final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
                    final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);
                    log.info("상태 정보 조회 성공 for profileId = {}", profile.getId());

                    // 대분류 포지션 정보 조회
                    ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                    if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                        final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                        profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
                    }
                    log.info("대분류 포지션 정보 조회 성공 for profileId = {}", profile.getId());

                    final ProfileInformMenu profileInformMenu = teamMemberMapper.toProfileInformMenu(
                            profileCurrentStateItems,
                            profile,
                            profilePositionDetail,
                            regionDetail
                    );
                    log.info("profileInformMenu = {}", profileInformMenu);

                    return profileInformMenu;
                })
                .collect(Collectors.toList());

        log.info("팀 '{}'의 멤버 목록을 성공적으로 매핑하였습니다.", teamName);

        // 5. 응답 DTO 생성 및 반환
        return teamMemberMapper.toTeamMemberItems(profileInformMenus);
    }

    public TeamMemberResponseDTO.AddTeamMemberResponse addTeamMember(
            final Long memberId,
            final String teamName,
            final AddTeamMemberRequest addTeamMemberRequest
    ) throws MessagingException {
        final String teamMemberInvitationEmail = addTeamMemberRequest.getTeamMemberInvitationEmail();

        final Team team = teamQueryAdapter.findByTeamName(teamName);

        if (teamMemberInvitationQueryAdapter.existsByEmailAndTeam(teamMemberInvitationEmail, team)) {
            throw TeamMemberInvitationDuplicateException.EXCEPTION;
        }

        // 해당 회원에 대해 이메일 발송
        teamMemberInvitationMailService.sendMailTeamMemberInvitation(teamMemberInvitationEmail, team.getTeamLogoImagePath(), team.getTeamName());
        // 새로운 팀원 초대 객체 생성
        final TeamMemberInvitation teamMemberInvitation = new TeamMemberInvitation(null, teamMemberInvitationEmail, team, TeamMemberInviteState.PENDING);

        teamMemberInvitationCommandAdapter.addTeamMemberInvitation(teamMemberInvitation);

        return teamMemberMapper.toAddTeamMemberInvitation(teamMemberInvitation);
    }
}
