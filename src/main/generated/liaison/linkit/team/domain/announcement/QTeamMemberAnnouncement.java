package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamMemberAnnouncement is a Querydsl query type for TeamMemberAnnouncement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncement extends EntityPathBase<TeamMemberAnnouncement> {

    private static final long serialVersionUID = 1264434084L;

    public static final QTeamMemberAnnouncement teamMemberAnnouncement = new QTeamMemberAnnouncement("teamMemberAnnouncement");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    public final StringPath announcementEndDate = createString("announcementEndDate");

    public final StringPath announcementStartDate = createString("announcementStartDate");

    public final StringPath announcementTitle = createString("announcementTitle");

    public final StringPath benefits = createString("benefits");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath detailedAnnouncement = createString("detailedAnnouncement");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath idealCandidate = createString("idealCandidate");

    public final BooleanPath isRegionFlexible = createBoolean("isRegionFlexible");

    public final StringPath joiningProcess = createString("joiningProcess");

    public final StringPath mainTasks = createString("mainTasks");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath preferredQualifications = createString("preferredQualifications");

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final StringPath workMethod = createString("workMethod");

    public QTeamMemberAnnouncement(String variable) {
        super(TeamMemberAnnouncement.class, forVariable(variable));
    }

    public QTeamMemberAnnouncement(Path<? extends TeamMemberAnnouncement> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamMemberAnnouncement(PathMetadata metadata) {
        super(TeamMemberAnnouncement.class, metadata);
    }

}

