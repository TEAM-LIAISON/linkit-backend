package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnnouncementPosition is a Querydsl query type for AnnouncementPosition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnnouncementPosition extends EntityPathBase<AnnouncementPosition> {

    private static final long serialVersionUID = 260758134L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnnouncementPosition announcementPosition = new QAnnouncementPosition("announcementPosition");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.common.domain.QPosition position;

    public final QTeamMemberAnnouncement teamMemberAnnouncement;

    public QAnnouncementPosition(String variable) {
        this(AnnouncementPosition.class, forVariable(variable), INITS);
    }

    public QAnnouncementPosition(Path<? extends AnnouncementPosition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnnouncementPosition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnnouncementPosition(PathMetadata metadata, PathInits inits) {
        this(AnnouncementPosition.class, metadata, inits);
    }

    public QAnnouncementPosition(Class<? extends AnnouncementPosition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.position = inits.isInitialized("position") ? new liaison.linkit.common.domain.QPosition(forProperty("position")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement"), inits.get("teamMemberAnnouncement")) : null;
    }

}

