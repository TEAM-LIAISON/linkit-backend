package liaison.linkit.profile.domain.attach;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAttachUrl is a Querydsl query type for AttachUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttachUrl extends EntityPathBase<AttachUrl> {

    private static final long serialVersionUID = 1845519294L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAttachUrl attachUrl = new QAttachUrl("attachUrl");

    public final StringPath attachUrlName = createString("attachUrlName");

    public final StringPath attachUrlPath = createString("attachUrlPath");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public QAttachUrl(String variable) {
        this(AttachUrl.class, forVariable(variable), INITS);
    }

    public QAttachUrl(Path<? extends AttachUrl> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAttachUrl(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAttachUrl(PathMetadata metadata, PathInits inits) {
        this(AttachUrl.class, metadata, inits);
    }

    public QAttachUrl(Class<? extends AttachUrl> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

