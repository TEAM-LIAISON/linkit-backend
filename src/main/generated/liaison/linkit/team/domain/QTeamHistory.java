package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamHistory is a Querydsl query type for TeamHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamHistory extends EntityPathBase<TeamHistory> {

    private static final long serialVersionUID = -425969386L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamHistory teamHistory = new QTeamHistory("teamHistory");

    public final StringPath historyDescription = createString("historyDescription");

    public final StringPath historyEndDate = createString("historyEndDate");

    public final StringPath historyName = createString("historyName");

    public final StringPath historyStartDate = createString("historyStartDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isHistoryInProgress = createBoolean("isHistoryInProgress");

    public final QTeam team;

    public QTeamHistory(String variable) {
        this(TeamHistory.class, forVariable(variable), INITS);
    }

    public QTeamHistory(Path<? extends TeamHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamHistory(PathMetadata metadata, PathInits inits) {
        this(TeamHistory.class, metadata, inits);
    }

    public QTeamHistory(Class<? extends TeamHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

