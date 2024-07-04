package liaison.linkit.profile.domain.miniProfile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MiniProfileKeyword {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mini_profile_keyword_id")
    private Long id;

    // 일대다 관계
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mini_profile_id")
    private MiniProfile miniProfile;

    @Column(name = "my_keyword_names")
    private String myKeywordNames;
}
