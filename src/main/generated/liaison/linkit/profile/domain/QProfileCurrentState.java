package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileCurrentState is a Querydsl query type for ProfileCurrentState
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileCurrentState extends EntityPathBase<ProfileCurrentState> {

    private static final long serialVersionUID = 1472961092L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileCurrentState profileCurrentState = new QProfileCurrentState("profileCurrentState");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public final liaison.linkit.common.domain.QProfileState profileState;

    public QProfileCurrentState(String variable) {
        this(ProfileCurrentState.class, forVariable(variable), INITS);
    }

    public QProfileCurrentState(Path<? extends ProfileCurrentState> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileCurrentState(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileCurrentState(PathMetadata metadata, PathInits inits) {
        this(ProfileCurrentState.class, metadata, inits);
    }

    public QProfileCurrentState(Class<? extends ProfileCurrentState> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
        this.profileState = inits.isInitialized("profileState") ? new liaison.linkit.common.domain.QProfileState(forProperty("profileState")) : null;
    }

}

