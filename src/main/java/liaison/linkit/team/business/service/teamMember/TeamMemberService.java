package liaison.linkit.team.business.service.teamMember;

import jakarta.mail.MessagingException;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.mail.service.TeamMemberInvitationMailService;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.exception.teamMember.TeamMemberForbiddenException;
import liaison.linkit.team.exception.teamMember.TeamMemberInvitationDuplicateException;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.AddTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.UpdateTeamMemberTypeRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AcceptedTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.PendingTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
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
    private final TeamMemberInvitationCommandAdapter teamMemberInvitationCommandAdapter;

    private final TeamMemberInvitationMailService teamMemberInvitationMailService;
    private final RegionMapper regionMapper;

    public TeamMemberViewItems getTeamMemberViewItems(final String teamName) {
        // 1. 팀 조회
        final Team team = teamQueryAdapter.findByTeamName(teamName);

        // 2. 팀 멤버 조회 (TeamMember)에 등록된 팀원만 조회된다. (수락한 팀원)
        final List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(team.getId());

        // 초대 수락 완료된 팀 멤버들을 AcceptedTeamMemberItem 리스트로 매핑
        final List<AcceptedTeamMemberItem> acceptedTeamMemberItems = getAcceptedTeamMemberItems(teamMembers);

        // 5. 응답 DTO 생성 및 반환
        return teamMemberMapper.toTeamMemberItems(acceptedTeamMemberItems);
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

        log.info("teamMemberInvitationEmail = {}", teamMemberInvitationEmail);

        // 해당 회원에 대해 이메일 발송
        teamMemberInvitationMailService.sendMailTeamMemberInvitation(teamMemberInvitationEmail, team.getTeamLogoImagePath(), team.getTeamName());

        // 새로운 팀원 초대 객체 생성
        final TeamMemberInvitation teamMemberInvitation = new TeamMemberInvitation(null, teamMemberInvitationEmail, team, addTeamMemberRequest.getTeamMemberType(), TeamMemberInviteState.PENDING);

        teamMemberInvitationCommandAdapter.addTeamMemberInvitation(teamMemberInvitation);

        return teamMemberMapper.toAddTeamMemberInvitation(teamMemberInvitation);
    }

    public TeamMemberResponseDTO.UpdateTeamMemberTypeResponse updateTeamMemberType(
            final Long memberId,
            final String teamName,
            final String emailId,
            final UpdateTeamMemberTypeRequest updateTeamMemberTypeRequest
    ) {
        // 1. TeamMember 엔티티 조회
        final TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamNameAndEmailId(teamName, emailId);

        final TeamMemberType requestedTeamMemberType = updateTeamMemberTypeRequest.getTeamMemberType();
        if (requestedTeamMemberType == teamMember.getTeamMemberType()) {
            throw TeamMemberForbiddenException.EXCEPTION;
        }

        teamMember.updateTeamMemberType(requestedTeamMemberType);

        return teamMemberMapper.toUpdateTeamMemberTypeResponse(teamMember);
    }

    public TeamMemberResponseDTO.TeamMemberItems getTeamMemberItems(final Long memberId, final String teamName) {
        // 팀 이름으로 팀 정보 조회
        final Team team = teamQueryAdapter.findByTeamName(teamName);

        // 해당 팀의 모든 팀 멤버 조회
        final List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(team.getId());

        // 초대 수락 완료된 팀 멤버들을 AcceptedTeamMemberItem 리스트로 매핑
        final List<AcceptedTeamMemberItem> acceptedTeamMemberItems = getAcceptedTeamMemberItems(teamMembers);

        // 초대 수락 대기 중인 멤버들을 PendingTeamMemberItem 리스트로 매핑
        final List<TeamMemberInvitation> teamMemberInvitations = teamMemberInvitationQueryAdapter.getTeamMemberInvitations(team.getId());

        final List<PendingTeamMemberItem> pendingTeamMemberItems = teamMemberInvitations.stream()
                .map(teamMemberInvitation ->
                        PendingTeamMemberItem.builder()
                                .teamMemberInvitationEmail(teamMemberInvitation.getTeamMemberInvitationEmail())
                                .teamMemberType(teamMemberInvitation.getTeamMemberType())
                                .teamMemberInviteState(teamMemberInvitation.getTeamMemberInviteState())
                                .build()
                ).toList();

        // 결과 객체 생성 및 반환
        return TeamMemberResponseDTO.TeamMemberItems.builder()
                .acceptedTeamMemberItems(acceptedTeamMemberItems)
                .pendingTeamMemberItems(pendingTeamMemberItems)
                .build();
    }


    private List<AcceptedTeamMemberItem> getAcceptedTeamMemberItems(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(teamMember -> {
                    // 프로필 정보 조회
                    Profile profile = profileQueryAdapter.findByMemberId(teamMember.getMember().getId());

                    // 대분류 포지션 정보 조회
                    ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                    if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                        final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                        profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                        final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                        regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                    }

                    return AcceptedTeamMemberItem.builder()
                            .profileImagePath(profile.getProfileImagePath())
                            .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                            .majorPosition(profilePositionDetail.getMajorPosition())
                            .regionDetail(regionDetail)
                            .teamMemberType(teamMember.getTeamMemberType())
                            .teamMemberInviteState(TeamMemberInviteState.ACCEPTED)
                            .build();
                })
                .toList();
    }

}
