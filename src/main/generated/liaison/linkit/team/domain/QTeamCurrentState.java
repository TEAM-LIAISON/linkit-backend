package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamCurrentState is a Querydsl query type for TeamCurrentState
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamCurrentState extends EntityPathBase<TeamCurrentState> {

    private static final long serialVersionUID = 368232278L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamCurrentState teamCurrentState = new QTeamCurrentState("teamCurrentState");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QTeam team;

    public final QTeamState teamState;

    public QTeamCurrentState(String variable) {
        this(TeamCurrentState.class, forVariable(variable), INITS);
    }

    public QTeamCurrentState(Path<? extends TeamCurrentState> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamCurrentState(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamCurrentState(PathMetadata metadata, PathInits inits) {
        this(TeamCurrentState.class, metadata, inits);
    }

    public QTeamCurrentState(Class<? extends TeamCurrentState> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team")) : null;
        this.teamState = inits.isInitialized("teamState") ? new QTeamState(forProperty("teamState")) : null;
    }

}

