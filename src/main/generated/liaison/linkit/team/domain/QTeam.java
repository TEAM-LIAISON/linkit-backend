package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeam is a Querydsl query type for Team
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeam extends EntityPathBase<Team> {

    private static final long serialVersionUID = -113486434L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeam team = new QTeam("team");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.region.QRegion region;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final StringPath teamLogoImagePath = createString("teamLogoImagePath");

    public final StringPath teamName = createString("teamName");

    public final liaison.linkit.team.domain.scale.QTeamScale teamScale;

    public final StringPath teamShortDescription = createString("teamShortDescription");

    public QTeam(String variable) {
        this(Team.class, forVariable(variable), INITS);
    }

    public QTeam(Path<? extends Team> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeam(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeam(PathMetadata metadata, PathInits inits) {
        this(Team.class, metadata, inits);
    }

    public QTeam(Class<? extends Team> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.region = inits.isInitialized("region") ? new liaison.linkit.profile.domain.region.QRegion(forProperty("region")) : null;
        this.teamScale = inits.isInitialized("teamScale") ? new liaison.linkit.team.domain.scale.QTeamScale(forProperty("teamScale")) : null;
    }

}

