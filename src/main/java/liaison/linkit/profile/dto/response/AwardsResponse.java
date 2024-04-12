package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Awards;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AwardsResponse {

    private final Long id;
    private final String awardsName;
    private final String ranking;
    private final String organizer;
    private final int awardsYear;
    private final int awardsMonth;
    private final String awardsDescription;

    public static AwardsResponse of(final Awards awards){
        return new AwardsResponse(
                awards.getId(),
                awards.getAwardsName(),
                awards.getRanking(),
                awards.getOrganizer(),
                awards.getAwardsYear(),
                awards.getAwardsMonth(),
                awards.getAwardsDescription()
        );
    }

    public static AwardsResponse personalAwards(final Awards awards) {
        return new AwardsResponse(
                awards.getId(),
                awards.getAwardsName(),
                awards.getRanking(),
                awards.getOrganizer(),
                awards.getAwardsYear(),
                awards.getAwardsMonth(),
                awards.getAwardsDescription()
        );
    }
}
