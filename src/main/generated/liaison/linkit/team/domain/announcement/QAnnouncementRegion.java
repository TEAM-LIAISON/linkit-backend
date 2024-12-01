package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnnouncementRegion is a Querydsl query type for AnnouncementRegion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnnouncementRegion extends EntityPathBase<AnnouncementRegion> {

    private static final long serialVersionUID = 1393186689L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnnouncementRegion announcementRegion = new QAnnouncementRegion("announcementRegion");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.region.QRegion region;

    public final QTeamMemberAnnouncement teamMemberAnnouncement;

    public QAnnouncementRegion(String variable) {
        this(AnnouncementRegion.class, forVariable(variable), INITS);
    }

    public QAnnouncementRegion(Path<? extends AnnouncementRegion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnnouncementRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnnouncementRegion(PathMetadata metadata, PathInits inits) {
        this(AnnouncementRegion.class, metadata, inits);
    }

    public QAnnouncementRegion(Class<? extends AnnouncementRegion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.region = inits.isInitialized("region") ? new liaison.linkit.profile.domain.region.QRegion(forProperty("region")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement")) : null;
    }

}

