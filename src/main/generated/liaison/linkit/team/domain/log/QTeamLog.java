package liaison.linkit.team.domain.log;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamLog is a Querydsl query type for TeamLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamLog extends EntityPathBase<TeamLog> {

    private static final long serialVersionUID = -1022230372L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamLog teamLog = new QTeamLog("teamLog");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLogPublic = createBoolean("isLogPublic");

    public final StringPath logContent = createString("logContent");

    public final StringPath logTitle = createString("logTitle");

    public final EnumPath<liaison.linkit.profile.domain.type.LogType> logType = createEnum("logType", liaison.linkit.profile.domain.type.LogType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.team.domain.QTeam team;

    public QTeamLog(String variable) {
        this(TeamLog.class, forVariable(variable), INITS);
    }

    public QTeamLog(Path<? extends TeamLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamLog(PathMetadata metadata, PathInits inits) {
        this(TeamLog.class, metadata, inits);
    }

    public QTeamLog(Class<? extends TeamLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team")) : null;
    }

}

