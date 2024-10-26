package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileLicense is a Querydsl query type for ProfileLicense
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileLicense extends EntityPathBase<ProfileLicense> {

    private static final long serialVersionUID = -100964075L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileLicense profileLicense = new QProfileLicense("profileLicense");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLicenseCertified = createBoolean("isLicenseCertified");

    public final BooleanPath isLicenseVerified = createBoolean("isLicenseVerified");

    public final StringPath licenseAcquisitionDate = createString("licenseAcquisitionDate");

    public final StringPath licenseCertificationAttachFileName = createString("licenseCertificationAttachFileName");

    public final StringPath licenseCertificationAttachFilePath = createString("licenseCertificationAttachFilePath");

    public final StringPath licenseCertificationDescription = createString("licenseCertificationDescription");

    public final StringPath licenseDescription = createString("licenseDescription");

    public final StringPath licenseInstitution = createString("licenseInstitution");

    public final StringPath licenseName = createString("licenseName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public QProfileLicense(String variable) {
        this(ProfileLicense.class, forVariable(variable), INITS);
    }

    public QProfileLicense(Path<? extends ProfileLicense> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileLicense(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileLicense(PathMetadata metadata, PathInits inits) {
        this(ProfileLicense.class, metadata, inits);
    }

    public QProfileLicense(Class<? extends ProfileLicense> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

