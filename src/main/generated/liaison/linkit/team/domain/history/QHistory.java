package liaison.linkit.team.domain.history;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHistory is a Querydsl query type for History
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHistory extends EntityPathBase<History> {

    private static final long serialVersionUID = 497217369L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHistory history = new QHistory("history");

    public final NumberPath<Integer> endYear = createNumber("endYear", Integer.class);

    public final StringPath historyIntroduction = createString("historyIntroduction");

    public final StringPath historyOneLineIntroduction = createString("historyOneLineIntroduction");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath inProgress = createBoolean("inProgress");

    public final NumberPath<Integer> startYear = createNumber("startYear", Integer.class);

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QHistory(String variable) {
        this(History.class, forVariable(variable), INITS);
    }

    public QHistory(Path<? extends History> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHistory(PathMetadata metadata, PathInits inits) {
        this(History.class, metadata, inits);
    }

    public QHistory(Class<? extends History> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

