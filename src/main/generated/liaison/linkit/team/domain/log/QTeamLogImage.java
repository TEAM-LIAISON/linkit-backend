package liaison.linkit.team.domain.log;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamLogImage is a Querydsl query type for TeamLogImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamLogImage extends EntityPathBase<TeamLogImage> {

    private static final long serialVersionUID = 1721350687L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamLogImage teamLogImage = new QTeamLogImage("teamLogImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.image.domain.QImage image;

    public final QTeamLog teamLog;

    public QTeamLogImage(String variable) {
        this(TeamLogImage.class, forVariable(variable), INITS);
    }

    public QTeamLogImage(Path<? extends TeamLogImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamLogImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamLogImage(PathMetadata metadata, PathInits inits) {
        this(TeamLogImage.class, metadata, inits);
    }

    public QTeamLogImage(Class<? extends TeamLogImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.image = inits.isInitialized("image") ? new liaison.linkit.image.domain.QImage(forProperty("image")) : null;
        this.teamLog = inits.isInitialized("teamLog") ? new QTeamLog(forProperty("teamLog"), inits.get("teamLog")) : null;
    }

}

