package liaison.linkit.team.domain.scale;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamScale is a Querydsl query type for TeamScale
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamScale extends EntityPathBase<TeamScale> {

    private static final long serialVersionUID = 178914152L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamScale teamScale = new QTeamScale("teamScale");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QScale scale;

    public final liaison.linkit.team.domain.QTeam team;

    public QTeamScale(String variable) {
        this(TeamScale.class, forVariable(variable), INITS);
    }

    public QTeamScale(Path<? extends TeamScale> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamScale(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamScale(PathMetadata metadata, PathInits inits) {
        this(TeamScale.class, metadata, inits);
    }

    public QTeamScale(Class<? extends TeamScale> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.scale = inits.isInitialized("scale") ? new QScale(forProperty("scale")) : null;
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

