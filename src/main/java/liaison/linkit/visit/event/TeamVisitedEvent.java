package liaison.linkit.visit.event;

import java.util.Optional;

import lombok.Getter;

@Getter
public class TeamVisitedEvent {
    private final Long visitedTeamId;
    private final Long visitorProfileId;
    private final Optional<Long> optionalMemberId;
    private final String entityType;

    public TeamVisitedEvent(
            Long visitedTeamId,
            Long visitorProfileId,
            Optional<Long> optionalMemberId,
            String entityType) {
        this.visitedTeamId = visitedTeamId;
        this.visitorProfileId = visitorProfileId;
        this.optionalMemberId = optionalMemberId;
        this.entityType = entityType;
    }
}
