package liaison.linkit.profile.domain.education;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEducation is a Querydsl query type for Education
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEducation extends EntityPathBase<Education> {

    private static final long serialVersionUID = -350527419L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEducation education = new QEducation("education");

    public final NumberPath<Integer> admissionYear = createNumber("admissionYear", Integer.class);

    public final QDegree degree;

    public final NumberPath<Integer> graduationYear = createNumber("graduationYear", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath majorName = createString("majorName");

    public final liaison.linkit.profile.domain.QProfile profile;

    public final StringPath universityName = createString("universityName");

    public QEducation(String variable) {
        this(Education.class, forVariable(variable), INITS);
    }

    public QEducation(Path<? extends Education> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEducation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEducation(PathMetadata metadata, PathInits inits) {
        this(Education.class, metadata, inits);
    }

    public QEducation(Class<? extends Education> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.degree = inits.isInitialized("degree") ? new QDegree(forProperty("degree")) : null;
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

