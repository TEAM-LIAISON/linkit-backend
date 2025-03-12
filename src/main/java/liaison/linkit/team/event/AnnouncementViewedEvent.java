package liaison.linkit.team.event;

import java.util.Optional;

import lombok.Getter;

@Getter
public class AnnouncementViewedEvent {
    private final Long announcementId;
    private final Optional<Long> memberId;
    private final String entityType;

    public AnnouncementViewedEvent(
            Long announcementId, Optional<Long> memberId, String entityType) {
        this.announcementId = announcementId;
        this.memberId = memberId;
        this.entityType = entityType;
    }
}
