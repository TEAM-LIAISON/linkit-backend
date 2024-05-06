package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // 전체 프로필 완성도 값 (%) 직결
    @Column(nullable = false)
    private int perfection;

    @Column(name = "introduction")
    private String introduction;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final int perfection,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.perfection = perfection;
        this.introduction = introduction;
    }

    public Profile(
            final Member member,
            final int perfection,
            final String introduction
    ) {
        this(null, member, perfection, introduction);
    }

    public void update(final ProfileUpdateRequest updateRequest) {
        this.introduction = updateRequest.getIntroduction();
    }
}
