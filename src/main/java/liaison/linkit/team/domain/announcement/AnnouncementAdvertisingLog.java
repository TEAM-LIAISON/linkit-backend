package liaison.linkit.team.domain.announcement;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "announcement_advertising_logs")
@CompoundIndex(
        name = "uniq_announcement_id",
        def = "{'team_member_announcement_id': 1}",
        unique = true)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementAdvertisingLog {
    @Id private String id;

    @Field("team_member_announcement_id")
    private Long teamMemberAnnouncementId;

    @Field("sent_at")
    private LocalDateTime sentAt;

    @Field("status")
    private String status; // SUCCESS, FAILED

    @Field("fail_reason")
    private String failReason;

    @Field("batch_id")
    private String batchId;
}
