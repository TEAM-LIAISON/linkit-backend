package liaison.linkit.team.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamProduct is a Querydsl query type for TeamProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamProduct extends EntityPathBase<TeamProduct> {

    private static final long serialVersionUID = 1478133746L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamProduct teamProduct = new QTeamProduct("teamProduct");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isProductInProgress = createBoolean("isProductInProgress");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath productDescription = createString("productDescription");

    public final StringPath productEndDate = createString("productEndDate");

    public final NumberPath<Integer> productHeadCount = createNumber("productHeadCount", Integer.class);

    public final StringPath productLineDescription = createString("productLineDescription");

    public final ListPath<ProductLink, QProductLink> productLinks = this.<ProductLink, QProductLink>createList("productLinks", ProductLink.class, QProductLink.class, PathInits.DIRECT2);

    public final StringPath productName = createString("productName");

    public final StringPath productRepresentImagePath = createString("productRepresentImagePath");

    public final StringPath productStartDate = createString("productStartDate");

    public final StringPath productTeamComposition = createString("productTeamComposition");

    public final EnumPath<liaison.linkit.profile.domain.portfolio.ProjectSize> projectSize = createEnum("projectSize", liaison.linkit.profile.domain.portfolio.ProjectSize.class);

    public final liaison.linkit.team.domain.QTeam team;

    public QTeamProduct(String variable) {
        this(TeamProduct.class, forVariable(variable), INITS);
    }

    public QTeamProduct(Path<? extends TeamProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamProduct(PathMetadata metadata, PathInits inits) {
        this(TeamProduct.class, metadata, inits);
    }

    public QTeamProduct(Class<? extends TeamProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team")) : null;
    }

}

