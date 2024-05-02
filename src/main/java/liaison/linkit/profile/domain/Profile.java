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

    @Column(name = "introduction")
    private String introduction;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.introduction = introduction;
    }

    public Profile(
            final Member member,
            final String introduction
    ) {
        this(null, member, introduction);
    }

    public void update(final ProfileUpdateRequest updateRequest) {
        this.introduction = updateRequest.getIntroduction();
    }
}
