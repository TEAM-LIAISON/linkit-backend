package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.dto.request.MiniProfileUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class MiniProfile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mini_profile_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(length = 40)
    private String oneLineIntroduction;
    private String interests;
    private String firstFreeText;
    private String secondFreeText;

    public static MiniProfile of(
            final Member member,
            final String oneLineIntroduction,
            final String interests,
            final String firstFreeText,
            final String secondFreeText) {
        return new MiniProfile(
                null,
                member,
                oneLineIntroduction,
                interests,
                firstFreeText,
                secondFreeText
        );
    }

    public void update(final MiniProfileUpdateRequest miniProfileUpdateRequest) {
        this.oneLineIntroduction = miniProfileUpdateRequest.getOneLineIntroduction();
        this.interests = miniProfileUpdateRequest.getInterests();
        this.firstFreeText = miniProfileUpdateRequest.getFirstFreeText();
        this.secondFreeText = miniProfileUpdateRequest.getSecondFreeText();
    }
}
