package liaison.linkit.scrap.validation;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.exception.privateScrap.DuplicatePrivateScrapException;
import liaison.linkit.scrap.exception.privateScrap.PrivateScrapManyRequestException;
import liaison.linkit.scrap.exception.teamMemberAnnouncementScrap.DuplicateTeamMemberAnnouncementScrapException;
import liaison.linkit.scrap.exception.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapManyRequestException;
import liaison.linkit.scrap.exception.teamScrap.DuplicateTeamScrapException;
import liaison.linkit.scrap.exception.teamScrap.TeamScrapManyRequestException;
import liaison.linkit.scrap.implement.privateScrap.PrivateScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamMemberAnnouncement.TeamMemberAnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ScrapValidator {

    private final MemberQueryAdapter memberQueryAdapter;

    private final PrivateScrapQueryAdapter privateScrapQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamMemberAnnouncementScrapQueryAdapter teamMemberAnnouncementScrapQueryAdapter;

    // 프로필 스크랩 최대 개수 판단 메서드
    public void validateMemberMaxPrivateScrap(final Long memberId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final int memberPrivateScrapCount = member.getPrivateScrapCount();

        // 프로필 스크랩 최대 개수 에러코드 반환
        if (memberPrivateScrapCount >= 8) {
            throw PrivateScrapManyRequestException.EXCEPTION;
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
        final int memberTeamMemberAnnouncementScrapCount = member.getTeamMemberAnnouncementScrapCount();

        // 팀원 공고 스크랩 최대 개수 에러코드 반환
        if (memberTeamMemberAnnouncementScrapCount >= 8) {
            throw TeamMemberAnnouncementScrapManyRequestException.EXCEPTION;
        }

    }

    public void validateSelfPrivateScrap(final Long memberId, final Long profileId) {
        if (privateScrapQueryAdapter.existsByMemberIdAndProfileId(memberId, profileId)) {
            throw DuplicatePrivateScrapException.EXCEPTION;
        }
    }

    public void validateSelfTeamScrap(final Long memberId, final Long teamId) {
        if (teamScrapQueryAdapter.existsByMemberIdAndTeamId(memberId, teamId)) {
            throw DuplicateTeamScrapException.EXCEPTION;
        }
    }

    public void validateSelfTeamMemberAnnouncementScrap(final Long memberId, final Long teamMemberAnnouncementId) {
        if (teamMemberAnnouncementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId)) {
            throw DuplicateTeamMemberAnnouncementScrapException.EXCEPTION;
        }
    }
}
