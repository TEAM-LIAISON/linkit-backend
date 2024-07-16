package liaison.linkit.profile.domain.attach;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.attach.AttachUrlUpdateRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attach_url")
public class AttachUrl {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "attach_url_id")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // 웹 링크 이름
    @Column(nullable = false)
    private String attachUrlName;

    @Column(nullable = false)
    private String attachUrlPath;

    public static AttachUrl of(
            final Profile profile,
            final String attachUrlName,
            final String attachUrlPath
    ) {
        return new AttachUrl(
                null,
                profile,
                attachUrlName,
                attachUrlPath
        );
    }

    public void update(final AttachUrlUpdateRequest updateRequest) {
        this.attachUrlName = updateRequest.getAttachUrlName();
        this.attachUrlPath = updateRequest.getAttachUrlPath();
    }
}
