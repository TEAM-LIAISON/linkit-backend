package liaison.linkit.team.domain.activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QActivityMethod is a Querydsl query type for ActivityMethod
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QActivityMethod extends EntityPathBase<ActivityMethod> {

    private static final long serialVersionUID = 1183784174L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QActivityMethod activityMethod = new QActivityMethod("activityMethod");

    public final QActivityMethodTag activityMethodTag;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QActivityMethod(String variable) {
        this(ActivityMethod.class, forVariable(variable), INITS);
    }

    public QActivityMethod(Path<? extends ActivityMethod> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QActivityMethod(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QActivityMethod(PathMetadata metadata, PathInits inits) {
        this(ActivityMethod.class, metadata, inits);
    }

    public QActivityMethod(Class<? extends ActivityMethod> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.activityMethodTag = inits.isInitialized("activityMethodTag") ? new QActivityMethodTag(forProperty("activityMethodTag")) : null;
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

