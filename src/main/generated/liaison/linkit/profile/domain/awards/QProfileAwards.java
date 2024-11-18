package liaison.linkit.profile.domain.awards;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileAwards is a Querydsl query type for ProfileAwards
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileAwards extends EntityPathBase<ProfileAwards> {

    private static final long serialVersionUID = -1292956860L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileAwards profileAwards = new QProfileAwards("profileAwards");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    public final StringPath awardsCertificationAttachFileName = createString("awardsCertificationAttachFileName");

    public final StringPath awardsCertificationAttachFilePath = createString("awardsCertificationAttachFilePath");

    public final StringPath awardsDate = createString("awardsDate");

    public final StringPath awardsDescription = createString("awardsDescription");

    public final StringPath awardsName = createString("awardsName");

    public final StringPath awardsOrganizer = createString("awardsOrganizer");

    public final StringPath awardsRanking = createString("awardsRanking");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAwardsCertified = createBoolean("isAwardsCertified");

    public final BooleanPath isAwardsVerified = createBoolean("isAwardsVerified");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.QProfile profile;

    public QProfileAwards(String variable) {
        this(ProfileAwards.class, forVariable(variable), INITS);
    }

    public QProfileAwards(Path<? extends ProfileAwards> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileAwards(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileAwards(PathMetadata metadata, PathInits inits) {
        this(ProfileAwards.class, metadata, inits);
    }

    public QProfileAwards(Class<? extends ProfileAwards> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

