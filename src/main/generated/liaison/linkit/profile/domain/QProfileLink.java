package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileLink is a Querydsl query type for ProfileLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileLink extends EntityPathBase<ProfileLink> {

    private static final long serialVersionUID = 1069161382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileLink profileLink = new QProfileLink("profileLink");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkName = createString("linkName");

    public final StringPath linkPath = createString("linkPath");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public QProfileLink(String variable) {
        this(ProfileLink.class, forVariable(variable), INITS);
    }

    public QProfileLink(Path<? extends ProfileLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileLink(PathMetadata metadata, PathInits inits) {
        this(ProfileLink.class, metadata, inits);
    }

    public QProfileLink(Class<? extends ProfileLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

