package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfile is a Querydsl query type for Profile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfile extends EntityPathBase<Profile> {

    private static final long serialVersionUID = -419979892L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfile profile = new QProfile("profile");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isProfileActivity = createBoolean("isProfileActivity");

    public final BooleanPath isProfileAwards = createBoolean("isProfileAwards");

    public final BooleanPath isProfileEducation = createBoolean("isProfileEducation");

    public final BooleanPath isProfileLicense = createBoolean("isProfileLicense");

    public final BooleanPath isProfileLink = createBoolean("isProfileLink");

    public final BooleanPath isProfilePortfolio = createBoolean("isProfilePortfolio");

    public final BooleanPath isProfilePublic = createBoolean("isProfilePublic");

    public final BooleanPath isProfileSkill = createBoolean("isProfileSkill");

    public final liaison.linkit.member.domain.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath profileImagePath = createString("profileImagePath");

    public final liaison.linkit.profile.domain.region.QRegion region;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public QProfile(String variable) {
        this(Profile.class, forVariable(variable), INITS);
    }

    public QProfile(Path<? extends Profile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfile(PathMetadata metadata, PathInits inits) {
        this(Profile.class, metadata, inits);
    }

    public QProfile(Class<? extends Profile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.region = inits.isInitialized("region") ? new liaison.linkit.profile.domain.region.QRegion(forProperty("region")) : null;
    }

}

