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
    private int completion;

    @Column(name = "introduction")
    private String introduction;

    // 기입 완료 시 참으로 변환
    @Column(nullable = false)
    private boolean isIntroduction;

    @Column(nullable = false)
    private boolean isSkill;

    @Column(nullable = false)
    private boolean isProfileTeamBuildingField;

    @Column(nullable = false)
    private boolean isAntecedents;

    @Column(nullable = false)
    private boolean isEducation;

    @Column(nullable = false)
    private boolean isAwards;

    @Column(nullable = false)
    private boolean isAttach;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final int completion,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.completion = completion;
        this.introduction = introduction;
        this.isIntroduction = false;
        this.isSkill = false;
        this.isProfileTeamBuildingField = false;
        this.isAntecedents = false;
        this.isEducation = false;
        this.isAwards = false;
        this.isAttach = false;
    }

    public Profile(
            final Member member,
            final int completion,
            final String introduction
    ) {
        this(null, member, completion, introduction);
    }

    public void update(final ProfileUpdateRequest updateRequest) {this.introduction = updateRequest.getIntroduction();}
    public void deleteIntroduction() {this.introduction = null;}
    public void addPerfectionSeven() {this.completion += 7;}
    public void cancelPerfectionSeven() {this.completion -= 7;}
    public void addPerfectionTwenty() { this.completion += 20; }
    public void cancelPerfectionTwenty() {this.completion -= 20;}

    // 등록 또는 삭제에서만 호출
    public void updateIsIntroduction(final Boolean isIntroduction) {
        this.isIntroduction = isIntroduction;
        if (isIntroduction) {
            addPerfectionTwenty();
        } else {
            cancelPerfectionSeven();
        }
    }

    public void updateIsProfileTeamBuildingField(final Boolean isProfileTeamBuildingField) {
        this.isProfileTeamBuildingField = isProfileTeamBuildingField;

    }



}
