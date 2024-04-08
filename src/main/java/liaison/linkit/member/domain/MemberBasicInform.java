package liaison.linkit.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

// 이름 및 사용자 정보 기입 플로우 추가 구현 필요
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberBasicInform {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_inform_id")
    private Long id;

    @Column(length = 30)
    private String username;

    @Column(length = 30)
    private String contact;

    @Column(length = 30)
    private String major;

    @Column(length = 30)
    private String job;

    @Column(length = 50)
    private String teamBuildingStep;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    public MemberBasicInform(final Long id, final String username, final String contact, final String major, final String job, final String teamBuildingStep, final Member member) {
        this.id = id;
        this.username = username;
        this.contact = contact;
        this.major = major;
        this.job = job;
        this.teamBuildingStep = teamBuildingStep;
        this.member = member;
    }

    public MemberBasicInform(final String username, final String contact, final String major, final String job, final String teamBuildingStep, final Member member) {
        this(null, username, contact, major, job, teamBuildingStep, member);
    }
}
