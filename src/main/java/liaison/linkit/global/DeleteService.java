package liaison.linkit.global;

import java.util.List;
import java.util.Set;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.exception.QuitBadRequestException;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DeleteService {
    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;
    private final TeamScrapCommandAdapter teamScrapCommandAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;
    private final TeamMemberInvitationCommandAdapter teamMemberInvitationCommandAdapter;
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TeamCommandAdapter teamCommandAdapter;
    private final TeamLogCommandAdapter teamLogCommandAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final ProfileCommandAdapter profileCommandAdapter;
    private final MemberCommandAdapter memberCommandAdapter;
    // 회원 탈퇴 케이스

    public void quitAccount(final Long memberId, final String refreshToken) {
        // 1. 회원 정보 조회
        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        log.info("Deleting account {}", memberId);

        // 1. 내가 오너로 속한 팀이 1개라도 있는가? 그 팀에 다른 관리자가 있는가? (통과했다면 팀 관련된 모든 정보를 삭제 가능)
        if (teamMemberQueryAdapter.existsTeamOwnerAndOtherManagerByMemberId(memberId)) {
            throw QuitBadRequestException.EXCEPTION;
        }

        // 탈퇴하려는 회원이 속한 삭제 가능한 팀들
        Set<Team> deletableTeams = teamMemberQueryAdapter.getAllDeletableTeamsByMemberId(member.getId());
        log.info("Deleting teams {}", deletableTeams);

        // 삭제 가능한 팀의 ID 리스트
        List<Long> deletableTeamIds = deletableTeams.stream()
                .map(Team::getId)
                .toList();
        log.info("Deleting teams {}", deletableTeamIds);

        Set<TeamMemberAnnouncement> deletableTeamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getAllDeletableTeamMemberAnnouncementsByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeamMemberAnnouncements);

        List<Long> deletableAnnouncementIds = deletableTeamMemberAnnouncements.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();
        log.info("Deleting teams {}", deletableAnnouncementIds);

        List<String> deletableTeamCodes = deletableTeams.stream()
                .map(Team::getTeamCode)
                .toList();

        log.info("Deleting teams {}", deletableTeamCodes);
        // [2. 매칭 데이터 삭제]
        matchingCommandAdapter.deleteAllBySenderProfile(member.getEmailId());
        matchingCommandAdapter.deleteAllBySenderTeamCodes(deletableTeamCodes);
        log.info("Deleting teams {}", deletableTeams);
        matchingCommandAdapter.deleteAllByReceiverProfile(member.getEmailId());
        matchingCommandAdapter.deleteAllByReceiverTeamCodes(deletableTeamCodes);
        matchingCommandAdapter.deleteAllByReceiverAnnouncements(deletableAnnouncementIds);

        log.info("Deleting teams {}", deletableTeams);

        // [3. 스크랩 데이터 삭제] (내가 스크랩을 한 데이터 + 나에게 스크랩을 한 데이터)
        profileScrapCommandAdapter.deleteAllByMemberId(member.getId());
        profileScrapCommandAdapter.deleteAllByProfileId(profile.getId());
        log.info("Deleting teams {}", deletableTeams);

        teamScrapCommandAdapter.deleteAllByMemberId(member.getId());
        teamScrapCommandAdapter.deleteAllByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeams);

        announcementScrapCommandAdapter.deleteAllByMemberId(member.getId());
        announcementScrapCommandAdapter.deleteAllByAnnouncementIds(deletableAnnouncementIds);
        log.info("Deleting teams {}", deletableTeams);

        // [4. 팀 초대 데이터 삭제]
        teamMemberInvitationCommandAdapter.deleteAllByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeams);

        teamMemberCommandAdapter.deleteAllTeamMember(memberId);
        // [5. 채팅방 데이터 삭제]

        // 4. 알림 데이터 삭제
        notificationCommandAdapter.deleteAllByReceiverMemberId(memberId);
        log.info("Deleting teams {}", deletableTeams);

        // 5. 토큰 데이터 삭제
        refreshTokenRepository.deleteById(refreshToken);
        log.info("Deleting teams {}", deletableTeams);

        for (Long deletableTeamId : deletableTeamIds) {
            teamLogCommandAdapter.deleteAllTeamLogs(deletableTeamId);
        }

        // 팀 삭제
        for (Team team : deletableTeams) {
            team.changeStatusToDeleted();
            teamCommandAdapter.updateTeam(team); // 변경된 상태를 DB에 반영
        }

        profileLogCommandAdapter.deleteAllProfileLogs(profile.getId());

        // 모든 공고 삭제
        if (!deletableAnnouncementIds.isEmpty()) {
            teamMemberAnnouncementCommandAdapter.deleteAllByIds(deletableAnnouncementIds);
        }

        // 6. 프로필 삭제
        profileCommandAdapter.deleteByMemberId(memberId);

        // 7. 회원 삭제
        memberCommandAdapter.deleteByMemberId(memberId);
    }

    public void deleteTeam() {
        // 팀 로그 삭제
        
        // 팀 구성원 삭제

        // 팀 초대 구성원 삭제

        // 모집 공고 삭제

        // 팀 스크랩 데이터 삭제

        // 매칭 요청 데이터 삭제

        // 프로덕트 데이터 삭제

        // 연혁 데이터 삭제
    }

    private void deleteTeamLogs(final List<Long> deletableTeamIds) {
        for (Long deletableTeamId : deletableTeamIds) {
            deleteTeamLog(deletableTeamId);
        }
    }

    private void deleteTeamLog(final Long deletableTeamId) {
        teamLogCommandAdapter.deleteAllTeamLogs(deletableTeamId);
    }
}
