package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileActivity is a Querydsl query type for ProfileActivity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileActivity extends EntityPathBase<ProfileActivity> {

    private static final long serialVersionUID = -1365399365L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileActivity profileActivity = new QProfileActivity("profileActivity");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    public final StringPath activityCertificationAttachFileName = createString("activityCertificationAttachFileName");

    public final StringPath activityCertificationAttachFilePath = createString("activityCertificationAttachFilePath");

    public final StringPath activityDescription = createString("activityDescription");

    public final StringPath activityEndDate = createString("activityEndDate");

    public final StringPath activityName = createString("activityName");

    public final StringPath activityRole = createString("activityRole");

    public final StringPath activityStartDate = createString("activityStartDate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActivityCertified = createBoolean("isActivityCertified");

    public final BooleanPath isActivityInProgress = createBoolean("isActivityInProgress");

    public final BooleanPath isActivityVerified = createBoolean("isActivityVerified");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public QProfileActivity(String variable) {
        this(ProfileActivity.class, forVariable(variable), INITS);
    }

    public QProfileActivity(Path<? extends ProfileActivity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileActivity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileActivity(PathMetadata metadata, PathInits inits) {
        this(ProfileActivity.class, metadata, inits);
    }

    public QProfileActivity(Class<? extends ProfileActivity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

