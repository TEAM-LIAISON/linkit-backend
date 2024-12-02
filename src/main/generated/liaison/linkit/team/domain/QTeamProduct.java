package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamProduct is a Querydsl query type for TeamProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamProduct extends EntityPathBase<TeamProduct> {

    private static final long serialVersionUID = -1662377615L;

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

    public final StringPath productName = createString("productName");

    public final StringPath productRepresentImagePath = createString("productRepresentImagePath");

    public final StringPath productStartDate = createString("productStartDate");

    public final StringPath productTeamComposition = createString("productTeamComposition");

    public final EnumPath<liaison.linkit.profile.domain.portfolio.ProjectSize> projectSize = createEnum("projectSize", liaison.linkit.profile.domain.portfolio.ProjectSize.class);

    public QTeamProduct(String variable) {
        super(TeamProduct.class, forVariable(variable));
    }

    public QTeamProduct(Path<? extends TeamProduct> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamProduct(PathMetadata metadata) {
        super(TeamProduct.class, metadata);
    }

}

