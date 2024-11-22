package liaison.linkit.profile.domain.portfolio;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectSubImage is a Querydsl query type for ProjectSubImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectSubImage extends EntityPathBase<ProjectSubImage> {

    private static final long serialVersionUID = -54782543L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectSubImage projectSubImage = new QProjectSubImage("projectSubImage");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfilePortfolio profilePortfolio;

    public final StringPath projectSubImagePath = createString("projectSubImagePath");

    public QProjectSubImage(String variable) {
        this(ProjectSubImage.class, forVariable(variable), INITS);
    }

    public QProjectSubImage(Path<? extends ProjectSubImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectSubImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectSubImage(PathMetadata metadata, PathInits inits) {
        this(ProjectSubImage.class, metadata, inits);
    }

    public QProjectSubImage(Class<? extends ProjectSubImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profilePortfolio = inits.isInitialized("profilePortfolio") ? new QProfilePortfolio(forProperty("profilePortfolio"), inits.get("profilePortfolio")) : null;
    }

}

