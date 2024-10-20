package liaison.linkit.team.domain.repository.teamMember;

public interface TeamMemberRepositoryCustom {
    public boolean existsTeamMemberByMemberId(final Long memberId, final Long teamId);
}
