package liaison.linkit.wish.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.wish.domain.type.WishType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class TeamWish {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_wish_id")
    private Long id;

    // 찜한 주체의 ID
    // 찜 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 찜하기 한 타겟 팀 소개서 ID
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @Column(name = "wish_type")
    @Enumerated(value = STRING)
    private WishType wishType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
