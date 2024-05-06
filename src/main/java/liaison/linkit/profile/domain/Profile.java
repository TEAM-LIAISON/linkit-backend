package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.type.ProfileType;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
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

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ProfileType profileType;

    // 전체 프로필 완성도 관련 항목
    @Column(nullable = false)
    private int perfection;

    @Column(name = "introduction")
    private String introduction;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final ProfileType profileType,
            final int perfection,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.profileType = profileType;
        this.perfection = perfection;
        this.introduction = introduction;
    }

    public Profile(
            final Member member,
            final ProfileType profileType,
            final int perfection,
            final String introduction
    ) {
        this(null, member, profileType, perfection, introduction);
    }

    public void update(final ProfileUpdateRequest updateRequest) {
        this.introduction = updateRequest.getIntroduction();
    }

    public void openPermission(final Boolean isOpen) {
        this.profileType = ProfileType.openPermission(isOpen);
    }

    public void changePermission(final Boolean isMatching) {
        this.profileType = ProfileType.changePermission(isMatching);
    }
}
