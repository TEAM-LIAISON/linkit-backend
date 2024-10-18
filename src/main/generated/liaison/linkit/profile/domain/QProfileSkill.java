package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileSkill is a Querydsl query type for ProfileSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileSkill extends EntityPathBase<ProfileSkill> {

    private static final long serialVersionUID = -1209215963L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileSkill profileSkill = new QProfileSkill("profileSkill");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public final StringPath skillIconImagePath = createString("skillIconImagePath");

    public final StringPath skillLevel = createString("skillLevel");

    public final StringPath skillName = createString("skillName");

    public QProfileSkill(String variable) {
        this(ProfileSkill.class, forVariable(variable), INITS);
    }

    public QProfileSkill(Path<? extends ProfileSkill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileSkill(PathMetadata metadata, PathInits inits) {
        this(ProfileSkill.class, metadata, inits);
    }

    public QProfileSkill(Class<? extends ProfileSkill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

