package liaison.linkit.team.domain.attach;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamAttachUrl is a Querydsl query type for TeamAttachUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamAttachUrl extends EntityPathBase<TeamAttachUrl> {

    private static final long serialVersionUID = 925297701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamAttachUrl teamAttachUrl = new QTeamAttachUrl("teamAttachUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamAttachUrlName = createString("teamAttachUrlName");

    public final StringPath teamAttachUrlPath = createString("teamAttachUrlPath");

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QTeamAttachUrl(String variable) {
        this(TeamAttachUrl.class, forVariable(variable), INITS);
    }

    public QTeamAttachUrl(Path<? extends TeamAttachUrl> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamAttachUrl(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamAttachUrl(PathMetadata metadata, PathInits inits) {
        this(TeamAttachUrl.class, metadata, inits);
    }

    public QTeamAttachUrl(Class<? extends TeamAttachUrl> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

