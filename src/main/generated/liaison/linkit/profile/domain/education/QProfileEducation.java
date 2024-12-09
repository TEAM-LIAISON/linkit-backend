package liaison.linkit.profile.domain.education;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileEducation is a Querydsl query type for ProfileEducation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileEducation extends EntityPathBase<ProfileEducation> {

    private static final long serialVersionUID = 999708866L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileEducation profileEducation = new QProfileEducation("profileEducation");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    public final StringPath admissionYear = createString("admissionYear");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath educationCertificationAttachFileName = createString("educationCertificationAttachFileName");

    public final StringPath educationCertificationAttachFilePath = createString("educationCertificationAttachFilePath");

    public final StringPath educationDescription = createString("educationDescription");

    public final StringPath graduationYear = createString("graduationYear");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAttendUniversity = createBoolean("isAttendUniversity");

    public final BooleanPath isEducationCertified = createBoolean("isEducationCertified");

    public final BooleanPath isEducationVerified = createBoolean("isEducationVerified");

    public final StringPath majorName = createString("majorName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.profile.QProfile profile;

    public final liaison.linkit.common.domain.QUniversity university;

    public QProfileEducation(String variable) {
        this(ProfileEducation.class, forVariable(variable), INITS);
    }

    public QProfileEducation(Path<? extends ProfileEducation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileEducation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileEducation(PathMetadata metadata, PathInits inits) {
        this(ProfileEducation.class, metadata, inits);
    }

    public QProfileEducation(Class<? extends ProfileEducation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.profile.QProfile(forProperty("profile"), inits.get("profile")) : null;
        this.university = inits.isInitialized("university") ? new liaison.linkit.common.domain.QUniversity(forProperty("university")) : null;
    }

}

