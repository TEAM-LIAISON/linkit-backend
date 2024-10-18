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

    public final StringPath applicationProcess = createString("applicationProcess");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mainBusiness = createString("mainBusiness");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

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

