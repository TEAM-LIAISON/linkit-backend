package liaison.linkit.team.domain.repository.teamMember;

public interface TeamMemberRepositoryCustom {
    boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId);
}
