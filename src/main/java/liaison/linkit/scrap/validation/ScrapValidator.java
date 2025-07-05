package liaison.linkit.scrap.validation;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.exception.announcementScrap.AnnouncementScrapManyRequestException;
import liaison.linkit.scrap.exception.announcementScrap.MyAnnouncementBadRequestException;
import liaison.linkit.scrap.exception.profileScrap.MyProfileBadRequestException;
import liaison.linkit.scrap.exception.profileScrap.ProfileScrapManyRequestException;
import liaison.linkit.scrap.exception.teamScrap.MyTeamBadRequestException;
import liaison.linkit.scrap.exception.teamScrap.TeamScrapManyRequestException;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ScrapValidator {

    private final MemberQueryAdapter memberQueryAdapter;

    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

    // 프로필 스크랩 최대 개수 판단 메서드
    public void validateMemberMaxProfileScrap(final Long memberId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final int memberProfileScrapCount = member.getProfileScrapCount();

        // 프로필 스크랩 최대 개수 에러코드 반환
        if (memberProfileScrapCount > 8) {
            throw ProfileScrapManyRequestException.EXCEPTION;
        }
    }

    // 팀 스크랩 최대 개수 판단 메서드
    public void validateMemberMaxTeamScrap(final Long memberId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final int memberTeamScrapCount = member.getTeamScrapCount();

        // 팀 스크랩 최대 개수 에러코드 반환
        if (memberTeamScrapCount >= 8) {
            throw TeamScrapManyRequestException.EXCEPTION;
        }
    }

    // 팀원 공고 스크랩 최대 개수 판단 메서드
    public void validateMemberMaxTeamMemberAnnouncementScrap(final Long memberId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final int memberTeamMemberAnnouncementScrapCount =
                member.getTeamMemberAnnouncementScrapCount();

        // 팀원 공고 스크랩 최대 개수 에러코드 반환
        if (memberTeamMemberAnnouncementScrapCount >= 8) {
            throw AnnouncementScrapManyRequestException.EXCEPTION;
        }
    }

    // 자신의 프로필에 대한 스크랩을 하지 못한다.
    public void validateSelfProfileScrap(final Long memberId, final String emailId) {
        if (memberQueryAdapter
                .findById(memberId)
                .equals(memberQueryAdapter.findByEmailId(emailId))) {
            throw MyProfileBadRequestException.EXCEPTION;
        }
    }

    // 자신이 속한 팀이라면 스크랩을 하지 못한다.
    public void validateSelfTeamScrap(final Long memberId, final String teamCode) {
        if (teamMemberQueryAdapter
                .findMembersByTeamCode(teamCode)
                .contains(memberQueryAdapter.findById(memberId))) {
            throw MyTeamBadRequestException.EXCEPTION;
        }
    }

    // 자신이 속한 팀에서 올린 공고라면 스크랩을 하지 못한다.
    public void validateSelfTeamMemberAnnouncementScrap(
            final Long memberId, final Long teamMemberAnnouncementId) {
        final Team team =
                teamMemberAnnouncementQueryAdapter
                        .getTeamMemberAnnouncement(teamMemberAnnouncementId)
                        .getTeam();

        if (teamMemberQueryAdapter
                .findMembersByTeamCode(team.getTeamCode())
                .contains(memberQueryAdapter.findById(memberId))) {
            throw MyAnnouncementBadRequestException.EXCEPTION;
        }
    }
}
