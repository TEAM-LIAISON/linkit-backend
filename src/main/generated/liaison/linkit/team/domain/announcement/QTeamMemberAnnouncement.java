package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberAnnouncement is a Querydsl query type for TeamMemberAnnouncement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncement extends EntityPathBase<TeamMemberAnnouncement> {

    private static final long serialVersionUID = 1264434084L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberAnnouncement teamMemberAnnouncement = new QTeamMemberAnnouncement("teamMemberAnnouncement");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    public final StringPath applicationProcess = createString("applicationProcess");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mainBusiness = createString("mainBusiness");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QTeamMemberAnnouncement(String variable) {
        this(TeamMemberAnnouncement.class, forVariable(variable), INITS);
    }

    public QTeamMemberAnnouncement(Path<? extends TeamMemberAnnouncement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberAnnouncement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberAnnouncement(PathMetadata metadata, PathInits inits) {
        this(TeamMemberAnnouncement.class, metadata, inits);
    }

    public QTeamMemberAnnouncement(Class<? extends TeamMemberAnnouncement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

