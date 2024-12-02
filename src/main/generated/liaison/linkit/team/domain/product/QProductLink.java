package liaison.linkit.team.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductLink is a Querydsl query type for ProductLink
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductLink extends EntityPathBase<ProductLink> {

    private static final long serialVersionUID = 1828665769L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductLink productLink = new QProductLink("productLink");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath productLinkName = createString("productLinkName");

    public final StringPath productLinkPath = createString("productLinkPath");

    public final QTeamProduct teamProduct;

    public QProductLink(String variable) {
        this(ProductLink.class, forVariable(variable), INITS);
    }

    public QProductLink(Path<? extends ProductLink> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductLink(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductLink(PathMetadata metadata, PathInits inits) {
        this(ProductLink.class, metadata, inits);
    }

    public QProductLink(Class<? extends ProductLink> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProduct = inits.isInitialized("teamProduct") ? new QTeamProduct(forProperty("teamProduct"), inits.get("teamProduct")) : null;
    }

}

