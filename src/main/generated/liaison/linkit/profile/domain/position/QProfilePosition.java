package liaison.linkit.profile.domain.position;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfilePosition is a Querydsl query type for ProfilePosition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfilePosition extends EntityPathBase<ProfilePosition> {

    private static final long serialVersionUID = 1037710698L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfilePosition profilePosition = new QProfilePosition("profilePosition");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.common.domain.QPosition position;

    public final liaison.linkit.profile.domain.profile.QProfile profile;

    public QProfilePosition(String variable) {
        this(ProfilePosition.class, forVariable(variable), INITS);
    }

    public QProfilePosition(Path<? extends ProfilePosition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfilePosition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfilePosition(PathMetadata metadata, PathInits inits) {
        this(ProfilePosition.class, metadata, inits);
    }

    public QProfilePosition(Class<? extends ProfilePosition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.position = inits.isInitialized("position") ? new liaison.linkit.common.domain.QPosition(forProperty("position")) : null;
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.profile.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

