package liaison.linkit.profile.domain.awards;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.awards.AwardsUpdateRequest;
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

    public void update(final AwardsUpdateRequest awardsUpdateRequest) {
        this.awardsName = awardsUpdateRequest.getAwardsName();
        this.ranking = awardsUpdateRequest.getRanking();
        this.organizer = awardsUpdateRequest.getOrganizer();
        this.awardsYear = awardsUpdateRequest.getAwardsYear();
        this.awardsMonth = awardsUpdateRequest.getAwardsMonth();
        this.awardsDescription = awardsUpdateRequest.getAwardsDescription();
    }
}
