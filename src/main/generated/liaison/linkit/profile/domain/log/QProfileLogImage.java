package liaison.linkit.profile.domain.log;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileLogImage is a Querydsl query type for ProfileLogImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileLogImage extends EntityPathBase<ProfileLogImage> {

    private static final long serialVersionUID = 1273853433L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileLogImage profileLogImage = new QProfileLogImage("profileLogImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.image.domain.QImage image;

    public final QProfileLog profileLog;

    public QProfileLogImage(String variable) {
        this(ProfileLogImage.class, forVariable(variable), INITS);
    }

    public QProfileLogImage(Path<? extends ProfileLogImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileLogImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileLogImage(PathMetadata metadata, PathInits inits) {
        this(ProfileLogImage.class, metadata, inits);
    }

    public QProfileLogImage(Class<? extends ProfileLogImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.image = inits.isInitialized("image") ? new liaison.linkit.image.domain.QImage(forProperty("image")) : null;
        this.profileLog = inits.isInitialized("profileLog") ? new QProfileLog(forProperty("profileLog"), inits.get("profileLog")) : null;
    }

}

