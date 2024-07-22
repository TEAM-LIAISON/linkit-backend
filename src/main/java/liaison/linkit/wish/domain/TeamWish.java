package liaison.linkit.wish.domain;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.wish.domain.type.WishType;
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
public class TeamWish extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_wish_id")
    private Long id;

    // 찜한 주체의 ID
    // 찜 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 찜하기 한 타겟 팀원 공고 ID
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_member_announcement_id")
    private TeamMemberAnnouncement teamMemberAnnouncement;

    @Column(name = "wish_type")
    @Enumerated(value = STRING)
    private WishType wishType;

}
