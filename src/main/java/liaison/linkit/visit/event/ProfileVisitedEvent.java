package liaison.linkit.visit.event;

import java.util.Optional;

import lombok.Getter;

@Getter
public class ProfileVisitedEvent {
    private final Long visitedProfileId;
    private final Long visitorProfileId;
    private final Optional<Long> optionalMemberId;
    private final String entityType;

    public ProfileVisitedEvent(
            Long visitedProfileId,
            Long visitorProfileId,
            Optional<Long> optionalMemberId,
            String entityType) {
        this.visitedProfileId = visitedProfileId;
        this.visitorProfileId = visitorProfileId;
        this.optionalMemberId = optionalMemberId;
        this.entityType = entityType;
    }
}
