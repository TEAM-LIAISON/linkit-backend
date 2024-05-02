package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "introduction")
    private String introduction;

    public Profile(
            final Long id,
            final Member member,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.introduction = introduction;
    }

    public static Profile of(
            final Member member,
            final String introduction
    ) {
        return new Profile(
                null,
                member,
                introduction
        );
    }

    public void update(final ProfileUpdateRequest profileUpdateRequest) {
        this.introduction = profileUpdateRequest.getIntroduction();
    }
}
