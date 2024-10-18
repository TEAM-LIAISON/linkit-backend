package liaison.linkit.profile.dto.response.awards;

import liaison.linkit.profile.domain.ProfileAwards;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AwardsResponse {

    private final Long id;
    private final String awardsName;
    private final String ranking;
    private final String organizer;
    private final int awardsYear;
    private final int awardsMonth;
    private final String awardsDescription;

    public static AwardsResponse of(final ProfileAwards profileAwards) {
        return new AwardsResponse(
                profileAwards.getId(),
                profileAwards.getAwardsName(),
                profileAwards.getRanking(),
                profileAwards.getOrganizer(),
                profileAwards.getAwardsYear(),
                profileAwards.getAwardsMonth(),
                profileAwards.getAwardsDescription()
        );
    }

    public static AwardsResponse personalAwards(final ProfileAwards profileAwards) {
        return new AwardsResponse(
                profileAwards.getId(),
                profileAwards.getAwardsName(),
                profileAwards.getRanking(),
                profileAwards.getOrganizer(),
                profileAwards.getAwardsYear(),
                profileAwards.getAwardsMonth(),
                profileAwards.getAwardsDescription()
        );
    }
}
