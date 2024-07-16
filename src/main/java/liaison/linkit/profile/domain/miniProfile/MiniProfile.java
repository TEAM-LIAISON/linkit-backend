package liaison.linkit.profile.domain.miniProfile;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MiniProfile{
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

    // 미니 프로필 이미지 경로
    @Column(name = "mini_profile_img")
    private String miniProfileImg;

    @Column(name = "is_activate")
    private boolean isActivate;

    // 생성 날짜
    @Column(updatable = false)
    private LocalDateTime createdDate;

    // 엔티티가 처음 저장될 때 createdDate를 현재 시간으로 설정
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public static MiniProfile of(
            final Profile profile,
            final String profileTitle,
            final String miniProfileImg,
            final boolean isActivate
    ) {
        return new MiniProfile(
                null,
                profile,
                profileTitle,
                miniProfileImg,
                isActivate,
                null
        );
    }
}
