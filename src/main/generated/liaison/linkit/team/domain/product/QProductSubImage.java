package liaison.linkit.team.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductSubImage is a Querydsl query type for ProductSubImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSubImage extends EntityPathBase<ProductSubImage> {

    private static final long serialVersionUID = 1037368618L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductSubImage productSubImage = new QProductSubImage("productSubImage");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath productSubImagePath = createString("productSubImagePath");

    public final QTeamProduct teamProduct;

    public QProductSubImage(String variable) {
        this(ProductSubImage.class, forVariable(variable), INITS);
    }

    public QProductSubImage(Path<? extends ProductSubImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductSubImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductSubImage(PathMetadata metadata, PathInits inits) {
        this(ProductSubImage.class, metadata, inits);
    }

    public QProductSubImage(Class<? extends ProductSubImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProduct = inits.isInitialized("teamProduct") ? new QTeamProduct(forProperty("teamProduct"), inits.get("teamProduct")) : null;
    }

}

