package liaison.linkit.resume.domain;

import jakarta.persistence.*;

import liaison.linkit.global.BaseEntity;

import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private String introduction;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    public Resume(
            final Long id,
            final String introduction,
            final Member member
    ){
        this.id = id;
        this.introduction = introduction;
        this.member = member;
    }

    public Resume(final String introduction, final Member member) {
        this(null, introduction, member);
    }

}
