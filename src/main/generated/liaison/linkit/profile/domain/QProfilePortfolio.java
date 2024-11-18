package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfilePortfolio is a Querydsl query type for ProfilePortfolio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfilePortfolio extends EntityPathBase<ProfilePortfolio> {

    private static final long serialVersionUID = 1539441948L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfilePortfolio profilePortfolio = new QProfilePortfolio("profilePortfolio");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfile profile;

    public QProfilePortfolio(String variable) {
        this(ProfilePortfolio.class, forVariable(variable), INITS);
    }

    public QProfilePortfolio(Path<? extends ProfilePortfolio> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfilePortfolio(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfilePortfolio(PathMetadata metadata, PathInits inits) {
        this(ProfilePortfolio.class, metadata, inits);
    }

    public QProfilePortfolio(Class<? extends ProfilePortfolio> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

