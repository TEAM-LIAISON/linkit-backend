package liaison.linkit.profile.domain.miniProfile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMiniProfile is a Querydsl query type for MiniProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMiniProfile extends EntityPathBase<MiniProfile> {

    private static final long serialVersionUID = -2090629991L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMiniProfile miniProfile = new QMiniProfile("miniProfile");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActivate = createBoolean("isActivate");

    public final StringPath miniProfileImg = createString("miniProfileImg");

    public final liaison.linkit.profile.domain.QProfile profile;

    public final StringPath profileTitle = createString("profileTitle");

    public QMiniProfile(String variable) {
        this(MiniProfile.class, forVariable(variable), INITS);
    }

    public QMiniProfile(Path<? extends MiniProfile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMiniProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMiniProfile(PathMetadata metadata, PathInits inits) {
        this(MiniProfile.class, metadata, inits);
    }

    public QMiniProfile(Class<? extends MiniProfile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

