package liaison.linkit.scrap.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrivateScrap is a Querydsl query type for PrivateScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrivateScrap extends EntityPathBase<PrivateScrap> {

    private static final long serialVersionUID = -487131597L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrivateScrap privateScrap = new QPrivateScrap("privateScrap");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.profile.domain.QProfile profile;

    public QPrivateScrap(String variable) {
        this(PrivateScrap.class, forVariable(variable), INITS);
    }

    public QPrivateScrap(Path<? extends PrivateScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrivateScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrivateScrap(PathMetadata metadata, PathInits inits) {
        this(PrivateScrap.class, metadata, inits);
    }

    public QPrivateScrap(Class<? extends PrivateScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

