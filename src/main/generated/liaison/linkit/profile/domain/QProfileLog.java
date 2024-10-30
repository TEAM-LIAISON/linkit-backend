package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileLog is a Querydsl query type for ProfileLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileLog extends EntityPathBase<ProfileLog> {

    private static final long serialVersionUID = -381152744L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileLog profileLog = new QProfileLog("profileLog");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLogPublic = createBoolean("isLogPublic");

    public final StringPath logContent = createString("logContent");

    public final StringPath logTitle = createString("logTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public final EnumPath<liaison.linkit.global.type.ProfileLogType> profileLogType = createEnum("profileLogType", liaison.linkit.global.type.ProfileLogType.class);

    public QProfileLog(String variable) {
        this(ProfileLog.class, forVariable(variable), INITS);
    }

    public QProfileLog(Path<? extends ProfileLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileLog(PathMetadata metadata, PathInits inits) {
        this(ProfileLog.class, metadata, inits);
    }

    public QProfileLog(Class<? extends ProfileLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

