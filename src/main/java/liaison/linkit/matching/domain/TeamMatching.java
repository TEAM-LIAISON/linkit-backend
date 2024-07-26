package liaison.linkit.matching.domain;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@SQLRestriction("status = 'USABLE'")
public class TeamMatching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_matching_id")
    private Long id;

    // 발신자의 ID
    // 매칭 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_member_announcement_id")
    private TeamMemberAnnouncement teamMemberAnnouncement;

    @Column(name = "sender_type")
    @Enumerated(value = STRING)
    private SenderType senderType;

    // 어떤 소개서에 요청 보낸 것인지 type 필요
    @Column(name = "matching_type")
    @Enumerated(value = STRING)
    private MatchingType matchingType;

    // 요청할 때 보내는 메시지
    @Column(name = "request_message")
    private String requestMessage;

    // 해당 매칭의 상태 관리 필요
    @Column(name = "matching_status_type")
    @Enumerated(value = STRING)
    private MatchingStatusType matchingStatusType;

    public void updateMatchingStatus(final boolean isAllow) {
        if (isAllow) {
            this.matchingStatusType = MatchingStatusType.SUCCESSFUL;
        } else {
            this.matchingStatusType = MatchingStatusType.DENIED;
        }
    }
}
