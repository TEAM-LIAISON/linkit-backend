package liaison.linkit.team.service;

import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BY_MEMBER_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void createNewTeam(final Accessor accessor) {
        final Member member = memberRepository.findById(accessor.getMemberId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
        try {
            final Team savedTeam = teamRepository.save(new Team(null, null, null));
        } catch ()

        teamMemberRepository.save(new TeamMember(member, savedTeam));
    }
}
