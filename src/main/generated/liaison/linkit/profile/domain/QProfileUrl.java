package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileUrl is a Querydsl query type for ProfileUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileUrl extends EntityPathBase<ProfileUrl> {

    private static final long serialVersionUID = -381143997L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileUrl profileUrl = new QProfileUrl("profileUrl");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public final EnumPath<liaison.linkit.global.type.ProfileUrlType> profileUrlType = createEnum("profileUrlType", liaison.linkit.global.type.ProfileUrlType.class);

    public final StringPath urlIconImagePath = createString("urlIconImagePath");

    public final StringPath urlName = createString("urlName");

    public final StringPath urlPath = createString("urlPath");

    public QProfileUrl(String variable) {
        this(ProfileUrl.class, forVariable(variable), INITS);
    }

    public QProfileUrl(Path<? extends ProfileUrl> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileUrl(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileUrl(PathMetadata metadata, PathInits inits) {
        this(ProfileUrl.class, metadata, inits);
    }

    public QProfileUrl(Class<? extends ProfileUrl> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

