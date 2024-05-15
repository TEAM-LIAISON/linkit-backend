package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
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
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(length = 40)
    private String oneLineIntroduction;

    private String interests;

    private String firstFreeText;

    private String secondFreeText;

//    private String profileImageName;

    public static MiniProfile of(
            final Profile profile,
            final String oneLineIntroduction,
            final String interests,
            final String firstFreeText,
            final String secondFreeText
//            final String profileImageName
    ) {
        return new MiniProfile(
                null,
                profile,
                oneLineIntroduction,
                interests,
                firstFreeText,
                secondFreeText
//                profileImageName
        );
    }

    public void update(final MiniProfileUpdateRequest miniProfileUpdateRequest) {
        this.oneLineIntroduction = miniProfileUpdateRequest.getOneLineIntroduction();
        this.interests = miniProfileUpdateRequest.getInterests();
        this.firstFreeText = miniProfileUpdateRequest.getFirstFreeText();
        this.secondFreeText = miniProfileUpdateRequest.getSecondFreeText();
    }
}
