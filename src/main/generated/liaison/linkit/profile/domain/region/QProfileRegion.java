package liaison.linkit.profile.domain.region;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileRegion is a Querydsl query type for ProfileRegion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileRegion extends EntityPathBase<ProfileRegion> {

    private static final long serialVersionUID = 1881715008L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileRegion profileRegion = new QProfileRegion("profileRegion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public final QRegion region;

    public QProfileRegion(String variable) {
        this(ProfileRegion.class, forVariable(variable), INITS);
    }

    public QProfileRegion(Path<? extends ProfileRegion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileRegion(PathMetadata metadata, PathInits inits) {
        this(ProfileRegion.class, metadata, inits);
    }

    public QProfileRegion(Class<? extends ProfileRegion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
        this.region = inits.isInitialized("region") ? new QRegion(forProperty("region")) : null;
    }

}

