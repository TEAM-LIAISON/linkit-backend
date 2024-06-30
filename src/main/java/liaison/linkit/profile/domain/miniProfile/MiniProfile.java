package liaison.linkit.profile.domain.miniProfile;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MiniProfile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mini_profile_id")
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private Profile profile;

    // 프로필 제목
    @Column(length = 40)
    private String profileTitle;

    // 프로필 업로드 기간
    private LocalDate uploadPeriod;

    // 프로필 마감 여부
    private boolean uploadDeadline;

    // 미니 프로필 이미지 경로
    private String miniProfileImg;

    // 나의 가치
    private String myValue;

    // 나의 스킬셋
    private String skillSets;

    public static MiniProfile of(
            final Profile profile,
            final String profileTitle,
            final LocalDate uploadPeriod,
            final boolean uploadDeadline,
            final String miniProfileImg,
            final String myValue,
            final String skillSets

    ) {
        return new MiniProfile(
                null,
                profile,
                profileTitle,
                uploadPeriod,
                uploadDeadline,
                miniProfileImg,
                myValue,
                skillSets
        );
    }
}
