package liaison.linkit.team.domain.repository.teamMember;

public interface TeamMemberCustomRepository {
    boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId);
}
