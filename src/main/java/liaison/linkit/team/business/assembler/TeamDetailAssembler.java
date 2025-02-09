package liaison.linkit.team.business.assembler;

import java.util.Optional;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.business.mapper.state.TeamCurrentStateMapper;
import liaison.linkit.team.business.mapper.team.TeamMapper;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamDetail;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamDetailAssembler {

    // Adapters
    private final RegionQueryAdapter regionQueryAdapter;
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberInvitationQueryAdapter teamMemberInvitationQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;

    // Mappers
    private final RegionMapper regionMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamCurrentStateMapper teamCurrentStateMapper;
    private final TeamScaleMapper teamScaleMapper;

    // Assembler
    private final TeamInformMenuAssembler teamInformMenuAssembler;

    /**
     * 팀 상세 정보를 조립합니다. 로그인한 사용자와 로그아웃 상태 모두 처리할 수 있습니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @param teamCode         조회할 팀의 코드.
     * @return 조립된 TeamDetail DTO.
     */
    public TeamDetail assembleTeamDetail(final Optional<Long> optionalMemberId, final String teamCode, final TeamInformMenu teamInformMenu) {
        // 1. 조회할 팀 정보
        Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);
        log.info("assembleTeamDetail - targetTeam: {}", targetTeam);

        // 2. 로그인 상태인 경우, 로그인 관련 플래그 조립; 로그아웃이면 기본값(false)
        LoginTeamFlags loginFlags = optionalMemberId.map(memberId ->
            assembleLoginTeamFlags(memberId, targetTeam)).orElse(new LoginTeamFlags());

        // 3. 최종적으로 팀 상세 DTO 조립 후 반환
        return teamMapper.toTeamDetail(
            loginFlags.isMyTeam,
            loginFlags.isTeamManager,
            loginFlags.isTeamInvitationInProgress,
            loginFlags.isTeamDeleteInProgress,
            loginFlags.isTeamDeleteRequester,
            teamInformMenu,
            targetTeam.isTeamPublic()
        );
    }

    /**
     * 로그인 상태일 때, 회원과 팀 정보를 기반으로 팀 관련 플래그들을 조립합니다.
     *
     * @param memberId   로그인한 회원의 ID.
     * @param targetTeam 조회할 팀 정보.
     * @return 조립된 LoginTeamFlags 객체.
     */
    private LoginTeamFlags assembleLoginTeamFlags(final Long memberId, final Team targetTeam) {
        LoginTeamFlags flags = new LoginTeamFlags();
        Member member = memberQueryAdapter.findById(memberId);
        log.info("assembleTeamDetail - 로그인 사용자: memberId={}, email={}", memberId, member.getEmail());

        // 팀 구성원 여부 및 관리자인지 판단
        if (teamMemberQueryAdapter.isMemberOfTeam(targetTeam.getTeamCode(), member.getEmailId())) {
            TeamMember teamMember = teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(targetTeam.getTeamCode(), member.getEmailId());
            log.info("assembleTeamDetail - 팀 구성원 정보: {}", teamMember);
            if (teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), memberId)) {
                flags.isTeamManager = true;
            }
        }
        flags.isMyTeam = teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), memberId);
        flags.isTeamInvitationInProgress = teamMemberInvitationQueryAdapter.existsByEmailAndTeam(member.getEmail(), targetTeam);
        flags.isTeamDeleteRequester = teamMemberQueryAdapter.isTeamDeleteRequester(member.getId(), targetTeam.getId());
        flags.isTeamDeleteInProgress = teamQueryAdapter.isTeamDeleteInProgress(targetTeam.getTeamCode()) && flags.isMyTeam;
        if (flags.isTeamDeleteRequester) {
            flags.isTeamDeleteInProgress = false;
        }
        return flags;
    }

    /**
     * 로그인 상태일 때, 팀 관련 플래그들을 담는 간단한 VO 클래스.
     */
    private static class LoginTeamFlags {

        boolean isMyTeam = false;
        boolean isTeamManager = false;
        boolean isTeamInvitationInProgress = false;
        boolean isTeamDeleteRequester = false;
        boolean isTeamDeleteInProgress = false;
    }
}
