package liaison.linkit.scrap.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.business.mapper.TeamScrapMapper;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.exception.teamScrap.BadRequestTeamScrapException;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapRequestDTO.UpdateTeamScrapRequest;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.scrap.validation.ScrapValidator;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.implement.TeamQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamScrapService {
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final ScrapValidator scrapValidator;

    private final TeamScrapCommandAdapter teamScrapCommandAdapter;

    private final TeamScrapMapper teamScrapMapper;

    // 회원이 팀 스크랩 버튼을 눌렀을 떄의 메서드
    public TeamScrapResponseDTO.UpdateTeamScrap updateTeamScrap(
            final Long memberId,
            final String teamName,
            final UpdateTeamScrapRequest updateTeamScrapRequest
    ) {

        boolean shouldAddScrap = updateTeamScrapRequest.isChangeScrapValue();

        scrapValidator.validateSelfTeamScrap(memberId, teamName); // 자기 자신이 속한 팀 스크랩에 대한 예외 처리
        scrapValidator.validateMemberMaxTeamScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = teamScrapQueryAdapter.existsByMemberIdAndTeamName(memberId, teamName);

        if (scrapExists) {
            handleExistingScrap(memberId, teamName, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, teamName, shouldAddScrap);
        }

        return teamScrapMapper.toUpdateTeamScrap(teamName, shouldAddScrap);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, String teamName, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            teamScrapCommandAdapter.deleteByMemberIdAndTeamName(memberId, teamName);
        } else {
            throw BadRequestTeamScrapException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, String teamName, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            Team team = teamQueryAdapter.findByTeamName(teamName);
            TeamScrap teamScrap = new TeamScrap(null, member, team);
            teamScrapCommandAdapter.addTeamScrap(teamScrap);
        } else {
            throw BadRequestTeamScrapException.EXCEPTION;
        }
    }

}
