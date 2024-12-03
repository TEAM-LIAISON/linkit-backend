package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamRegion is a Querydsl query type for TeamRegion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamRegion extends EntityPathBase<TeamRegion> {

    private static final long serialVersionUID = -978437582L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamRegion teamRegion = new QTeamRegion("teamRegion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.region.QRegion region;

    public final QTeam team;

    public QTeamRegion(String variable) {
        this(TeamRegion.class, forVariable(variable), INITS);
    }

    public QTeamRegion(Path<? extends TeamRegion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamRegion(PathMetadata metadata, PathInits inits) {
        this(TeamRegion.class, metadata, inits);
    }

    public QTeamRegion(Class<? extends TeamRegion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.region = inits.isInitialized("region") ? new liaison.linkit.profile.domain.region.QRegion(forProperty("region")) : null;
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team")) : null;
    }

}

