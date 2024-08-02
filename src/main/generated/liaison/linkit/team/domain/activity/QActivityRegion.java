package liaison.linkit.team.domain.activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QActivityRegion is a Querydsl query type for ActivityRegion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QActivityRegion extends EntityPathBase<ActivityRegion> {

    private static final long serialVersionUID = 1326543617L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QActivityRegion activityRegion = new QActivityRegion("activityRegion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.region.QRegion region;

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QActivityRegion(String variable) {
        this(ActivityRegion.class, forVariable(variable), INITS);
    }

    public QActivityRegion(Path<? extends ActivityRegion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QActivityRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QActivityRegion(PathMetadata metadata, PathInits inits) {
        this(ActivityRegion.class, metadata, inits);
    }

    public QActivityRegion(Class<? extends ActivityRegion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.region = inits.isInitialized("region") ? new liaison.linkit.profile.domain.region.QRegion(forProperty("region")) : null;
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

