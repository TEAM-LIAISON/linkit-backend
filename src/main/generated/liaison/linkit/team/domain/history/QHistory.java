package liaison.linkit.team.domain.history;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHistory is a Querydsl query type for History
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHistory extends EntityPathBase<History> {

    private static final long serialVersionUID = 497217369L;

    public static final QHistory history = new QHistory("history");

    public final NumberPath<Integer> endYear = createNumber("endYear", Integer.class);

    public final StringPath historyIntroduction = createString("historyIntroduction");

    public final StringPath historyOneLineIntroduction = createString("historyOneLineIntroduction");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath inProgress = createBoolean("inProgress");

    public final NumberPath<Integer> startYear = createNumber("startYear", Integer.class);

    public final NumberPath<TeamProfile> teamProfile = createNumber("teamProfile", TeamProfile.class);

    public QHistory(String variable) {
        super(History.class, forVariable(variable));
    }

    public QHistory(Path<? extends History> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHistory(PathMetadata metadata) {
        super(History.class, metadata);
    }

}

