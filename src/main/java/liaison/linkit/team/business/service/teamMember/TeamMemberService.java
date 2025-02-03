package liaison.linkit.team.business.service.teamMember;

import jakarta.mail.MessagingException;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.DeleteUtil;
import liaison.linkit.mail.service.TeamMemberInvitationMailService;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import liaison.linkit.team.domain.teamMember.type.TeamMemberRegisterType;
import liaison.linkit.team.exception.teamMember.ManagingBadRequestException;
import liaison.linkit.team.exception.teamMember.OwnerTeamMemberOutBadRequestException;
import liaison.linkit.team.exception.teamMember.RemoveTeamMemberBadRequestException;
import liaison.linkit.team.exception.teamMember.TeamAdminNotRegisteredException;
import liaison.linkit.team.exception.teamMember.TeamMemberForbiddenException;
import liaison.linkit.team.exception.teamMember.TeamMemberInvitationDuplicateException;
import liaison.linkit.team.exception.teamMember.TeamMemberInvitationNotFoundException;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.UpdateManagingTeamStateRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.AddTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.RemoveTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.TeamJoinRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.UpdateTeamMemberTypeRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AcceptedTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.PendingTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.UpdateManagingTeamStateResponse;
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
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final HeaderNotificationService headerNotificationService;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final TeamCommandAdapter teamCommandAdapter;
    private final DeleteUtil deleteUtil;

    public TeamMemberViewItems getTeamMemberViewItems(final String teamCode) {
        // 1. 팀 조회
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);

        // 2. 팀 멤버 조회 (TeamMember)에 등록된 팀원만 조회된다. (수락한 팀원)
        final List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(team.getId());

        // 초대 수락 완료된 팀 멤버들을 AcceptedTeamMemberItem 리스트로 매핑
        final List<AcceptedTeamMemberItem> acceptedTeamMemberItems = getAcceptedTeamMemberItems(teamMembers);

        // 5. 응답 DTO 생성 및 반환
        return teamMemberMapper.toTeamMemberItems(acceptedTeamMemberItems);
    }


    public TeamMemberResponseDTO.AddTeamMemberResponse addTeamMember(
            final Long memberId,
            final String teamCode,
            final AddTeamMemberRequest addTeamMemberRequest
    ) throws MessagingException {
        final String teamMemberInvitationEmail = addTeamMemberRequest.getTeamMemberInvitationEmail();

        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(team.getId(), memberId)) {
            throw TeamAdminNotRegisteredException.EXCEPTION;
        }

        if (teamMemberInvitationQueryAdapter.existsByEmailAndTeam(teamMemberInvitationEmail, team)) {
            throw TeamMemberInvitationDuplicateException.EXCEPTION;
        }

        log.info("teamMemberInvitationEmail = {}", teamMemberInvitationEmail);

        // 해당 회원에 대해 이메일 발송
        teamMemberInvitationMailService.sendMailTeamMemberInvitation(teamMemberInvitationEmail, team.getTeamLogoImagePath(), team.getTeamName(), team.getTeamCode());

        // 새로운 팀원 초대 객체 생성
        final TeamMemberInvitation teamMemberInvitation = new TeamMemberInvitation(null, teamMemberInvitationEmail, team, addTeamMemberRequest.getTeamMemberType(), TeamMemberInviteState.PENDING);

        teamMemberInvitationCommandAdapter.addTeamMemberInvitation(teamMemberInvitation);

        // (상대방님의 뫄뫄팀 삭제 요청)
        NotificationDetails teamInvitationNotificationDetails = NotificationDetails.teamInvitationRequested(
                teamCode,
                team.getTeamLogoImagePath(),
                team.getTeamName(),
                false
        );

        // 링킷의 회원이라면
        if (memberQueryAdapter.existsByEmail(teamMemberInvitationEmail)) {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            memberQueryAdapter.findByEmail(teamMemberInvitationEmail).getId(),
                            NotificationType.TEAM_INVITATION,
                            SubNotificationType.TEAM_INVITATION_REQUESTED,
                            teamInvitationNotificationDetails
                    )
            );
        }

        return teamMemberMapper.toAddTeamMemberInvitation(teamMemberInvitation);
    }

    public TeamMemberResponseDTO.UpdateTeamMemberTypeResponse updateTeamMemberType(
            final Long memberId,
            final String teamCode,
            final String emailId,
            final UpdateTeamMemberTypeRequest updateTeamMemberTypeRequest
    ) {
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        final TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(teamCode, emailId);
        final TeamMemberType requestedTeamMemberType = updateTeamMemberTypeRequest.getTeamMemberType();
        if (requestedTeamMemberType == teamMember.getTeamMemberType()) {
            throw TeamMemberForbiddenException.EXCEPTION;
        }

        if (requestedTeamMemberType.equals(TeamMemberType.TEAM_OWNER)) {
            final Member ownerMember = memberQueryAdapter.findById(teamMemberQueryAdapter.getTeamOwnerMemberId(targetTeam));

            if (!ownerMember.getId().equals(memberId)) {
                throw TeamMemberForbiddenException.EXCEPTION;
            }

            teamMember.setTeamMemberType(requestedTeamMemberType);
            teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(teamCode, ownerMember.getEmailId());
        } else {
            teamMember.setTeamMemberType(requestedTeamMemberType);
        }

        return teamMemberMapper.toUpdateTeamMemberTypeResponse(teamMember);
    }

    public TeamMemberResponseDTO.TeamMemberItems getTeamMemberItems(final Long memberId, final String teamCode) {
        // 팀 이름으로 팀 정보 조회
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);

        // 해당 팀의 모든 팀 멤버 조회
        final List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(team.getId());

        // isTeamOwner 계산
        boolean isTeamOwner = teamMembers.stream()
                .filter(teamMember -> teamMember.getTeamMemberType() == TeamMemberType.TEAM_OWNER)
                .anyMatch(teamMember -> teamMember.getMember().getId().equals(memberId));

        // isTeamManager 계산
        boolean isTeamManager = teamMembers.stream()
                .anyMatch(teamMember -> teamMember.getMember().getId().equals(memberId)
                        && (teamMember.getTeamMemberType() == TeamMemberType.TEAM_MANAGER
                        || teamMember.getTeamMemberType() == TeamMemberType.TEAM_OWNER));

        // 초대 수락 완료된 팀 멤버들을 AcceptedTeamMemberItem 리스트로 매핑
        final List<AcceptedTeamMemberItem> acceptedTeamMemberItems = getAcceptedTeamMemberItems(teamMembers);

        // 초대 수락 대기 중인 멤버들을 PendingTeamMemberItem 리스트로 매핑
        final List<TeamMemberInvitation> teamMemberInvitations = teamMemberInvitationQueryAdapter.getTeamMemberInvitationsInPending(team.getId());

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
                .isTeamOwner(isTeamOwner)
                .isTeamManager(isTeamManager)
                .acceptedTeamMemberItems(acceptedTeamMemberItems)
                .pendingTeamMemberItems(pendingTeamMemberItems)
                .build();
    }

    public TeamMemberResponseDTO.TeamOutResponse getOutTeam(final Long memberId, final String teamCode) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        final String emailId = memberQueryAdapter.findEmailIdById(memberId);

        // 오너의 팀 나가기 요청은 반려된다.
        if (teamMemberQueryAdapter.getTeamOwnerMemberId(team).equals(memberId)) {
            throw OwnerTeamMemberOutBadRequestException.EXCEPTION;
        }

        final TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(teamCode, emailId);
        teamMemberCommandAdapter.removeTeamMemberInTeam(teamMember);

        return teamMemberMapper.toTeamOutResponse(team.getTeamCode(), emailId);
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

                    return teamMemberMapper.toAcceptedTeamMemberItem(profile, profilePositionDetail, regionDetail, teamMember, TeamMemberInviteState.ACCEPTED);
                })
                .toList();
    }

    public TeamMemberResponseDTO.TeamJoinResponse joinTeam(final Long memberId, final String teamCode, final TeamJoinRequest teamJoinRequest) {

        final Member member = memberQueryAdapter.findById(memberId);
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        if (!teamMemberInvitationQueryAdapter.existsByEmailAndTeam(member.getEmail(), targetTeam)) {
            throw TeamMemberInvitationNotFoundException.EXCEPTION;
        } else {
            final TeamMemberInvitation teamMemberInvitation = teamMemberInvitationQueryAdapter.getTeamMemberInvitationInPendingState(member.getEmail(), targetTeam);
            if (teamJoinRequest.getIsTeamJoin()) {
                // 수락
                final TeamMember teamMember = teamMemberMapper.toTeamMember(member, targetTeam, teamMemberInvitation.getTeamMemberType());
                // 팀원 초대 등록
                teamMemberCommandAdapter.addTeamMember(teamMember);
                teamMemberInvitationCommandAdapter.removeTeamMemberInvitation(teamMemberInvitation);

                // 팀원 초대 수락 시 모든 팀원들한테 이 사람이 구성원으로 들어왔다고 알림 발송
                // (상대방님의 뫄뫄팀 구성원 수락 완료)
                NotificationDetails teamMemberJoinedNotificationDetails = NotificationDetails.teamMemberJoined(
                        teamCode,
                        targetTeam.getTeamLogoImagePath(),
                        member.getMemberBasicInform().getMemberName(),
                        targetTeam.getTeamName(),
                        false
                );

                final List<TeamMember> teamMembers = teamMemberQueryAdapter.getAllTeamManagers(targetTeam);
                for (TeamMember currentMember : teamMembers) {
                    if (!currentMember.getMember().getId().equals(memberId)) { // 본인 제외
                        notificationService.alertNewNotification(
                                notificationMapper.toNotification(
                                        currentMember.getMember().getId(),
                                        NotificationType.TEAM_INVITATION,
                                        SubNotificationType.TEAM_MEMBER_JOINED,
                                        teamMemberJoinedNotificationDetails
                                )
                        );
                    }

                    headerNotificationService.publishNotificationCount(currentMember.getMember().getId());
                }
            } else {
                // 거절
                teamMemberInvitationCommandAdapter.removeTeamMemberInvitation(teamMemberInvitation);
            }
        }

        return teamMemberMapper.toTeamJoinResponse(teamCode, member.getEmailId());
    }

    public UpdateManagingTeamStateResponse updateManagingTeamState(final Long memberId, final String teamCode, final UpdateManagingTeamStateRequest updateManagingTeamStateRequest) {
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), memberId)) {
            throw ManagingBadRequestException.EXCEPTION;
        }

        final Member member = memberQueryAdapter.findById(memberId);
        final TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(teamCode, member.getEmailId());

        teamMemberCommandAdapter.updateTeamMemberManagingTeamState(teamMember, updateManagingTeamStateRequest.getTeamMemberManagingTeamState());

        // 삭제 허용
        if (updateManagingTeamStateRequest.getTeamMemberManagingTeamState().equals(TeamMemberManagingTeamState.ALLOW_DELETE)) {
            // 모든 TeamMember가 허용을 했는지 확인
            if (teamMemberQueryAdapter.isTeamMembersAllowDelete(targetTeam)) {
                final String targetTeamName = targetTeam.getTeamName();
                final String targetTeamLogoImagePath = targetTeam.getTeamLogoImagePath();

                // 팀에 대한 모든 데이터 삭제
                deleteUtil.deleteTeam(teamCode);
                log.info("Deleted team " + teamCode);

                // 삭제 완료 알림 발송
                NotificationDetails removeTeamNotificationDetails = NotificationDetails.removeTeamCompleted(
                        teamCode,
                        targetTeamLogoImagePath,
                        targetTeamName,
                        false
                );

                notificationService.alertNewNotification(
                        notificationMapper.toNotification(
                                memberId,
                                NotificationType.TEAM,
                                SubNotificationType.REMOVE_TEAM_COMPLETED,
                                removeTeamNotificationDetails
                        )
                );

                headerNotificationService.publishNotificationCount(memberId);
            }
        } else if (updateManagingTeamStateRequest.getTeamMemberManagingTeamState().equals(TeamMemberManagingTeamState.DENY_DELETE)) {
            // 삭제 거절 -> 아무 일도 없었던 것처럼
            teamCommandAdapter.updateTeamStatus(TeamStatus.ACTIVE, targetTeam.getTeamCode());

            // 모든 팀원들의 상태 변경
            final List<TeamMember> teamMembers = teamMemberQueryAdapter.getTeamMembers(targetTeam.getId());
            for (TeamMember currentMember : teamMembers) {
                currentMember.setTeamMemberManagingTeamState(TeamMemberManagingTeamState.ACTIVE);
            }

            // 알림 발송
            NotificationDetails rejectRemoveTeamNotificationDetails = NotificationDetails.removeTeamRejected(
                    targetTeam.getTeamCode(),
                    targetTeam.getTeamLogoImagePath(),
                    member.getMemberBasicInform().getMemberName(),
                    targetTeam.getTeamName(),
                    false
            );

            // 관리자에게 개별 발송
            final List<TeamMember> managingTeamMembers = teamMemberQueryAdapter.getAllTeamManagers(targetTeam);
            for (TeamMember currentManagingMember : managingTeamMembers) {
                notificationService.alertNewNotification(
                        notificationMapper.toNotification(
                                currentManagingMember.getMember().getId(),
                                NotificationType.TEAM,
                                SubNotificationType.REMOVE_TEAM_REJECTED,
                                rejectRemoveTeamNotificationDetails
                        )
                );

                headerNotificationService.publishNotificationCount(currentManagingMember.getMember().getId());
            }

        }

        return teamMemberMapper.toUpdateManagingTeamStateResponse(teamCode);
    }

    // 오너 / 관리자가 팀원을 삭제할 수 있다.
    public TeamMemberResponseDTO.RemoveTeamMemberResponse removeTeamMember(final String teamCode, final Long memberId, final RemoveTeamMemberRequest removeTeamMemberRequest) {
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), memberId)) {
            throw RemoveTeamMemberBadRequestException.EXCEPTION;
        }

        if (removeTeamMemberRequest.getTeamMemberRegisterType().equals(TeamMemberRegisterType.ACCEPTED)) {
            final TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(teamCode, removeTeamMemberRequest.getRemoveIdentifier());
            teamMemberCommandAdapter.removeTeamMemberInTeam(teamMember);
        }

        if (removeTeamMemberRequest.getTeamMemberRegisterType().equals(TeamMemberRegisterType.PENDING)) {
            final TeamMemberInvitation teamMemberInvitation = teamMemberInvitationQueryAdapter.getTeamMemberInvitationByTeamCodeAndEmail(teamCode, removeTeamMemberRequest.getRemoveIdentifier());
            teamMemberInvitationCommandAdapter.removeTeamMemberInvitation(teamMemberInvitation);
        }

        return teamMemberMapper.toRemoveTeamMemberResponse(teamCode, removeTeamMemberRequest.getRemoveIdentifier());
    }
}
