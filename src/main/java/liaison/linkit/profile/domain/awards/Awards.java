package liaison.linkit.profile.domain.awards;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.awards.AwardsCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "awards")
public class Awards {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "awards_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(nullable = false)
    private String awardsName;

    @Column(nullable = false)
    private String ranking;

    @Column(nullable = false)
    private String organizer;

    @Column(nullable = false)
    private int awardsYear;

    @Column(nullable = false)
    private int awardsMonth;

    @Column(nullable = false)
    private String awardsDescription;

    public static Awards of(
            final Profile profile,
            final String awardsName,
            final String ranking,
            final String organizer,
            final int awardsYear,
            final int awardsMonth,
            final String awardsDescription) {
        return new Awards(
                null,
                profile,
                awardsName,
                ranking,
                organizer,
                awardsYear,
                awardsMonth,
                awardsDescription
        );
    }

    public void update(final AwardsCreateRequest awardsCreateRequest) {
        this.awardsName = awardsCreateRequest.getAwardsName();
        this.ranking = awardsCreateRequest.getRanking();
        this.organizer = awardsCreateRequest.getOrganizer();
        this.awardsYear = awardsCreateRequest.getAwardsYear();
        this.awardsMonth = awardsCreateRequest.getAwardsMonth();
        this.awardsDescription = awardsCreateRequest.getAwardsDescription();
    }
}
