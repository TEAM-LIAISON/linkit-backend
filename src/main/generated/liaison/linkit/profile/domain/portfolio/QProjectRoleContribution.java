package liaison.linkit.profile.domain.portfolio;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectRoleContribution is a Querydsl query type for ProjectRoleContribution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectRoleContribution extends EntityPathBase<ProjectRoleContribution> {

    private static final long serialVersionUID = 1869059164L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectRoleContribution projectRoleContribution = new QProjectRoleContribution("projectRoleContribution");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QProfilePortfolio profilePortfolio;

    public final EnumPath<ProjectContribution> projectContribution = createEnum("projectContribution", ProjectContribution.class);

    public final StringPath projectRole = createString("projectRole");

    public QProjectRoleContribution(String variable) {
        this(ProjectRoleContribution.class, forVariable(variable), INITS);
    }

    public QProjectRoleContribution(Path<? extends ProjectRoleContribution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectRoleContribution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectRoleContribution(PathMetadata metadata, PathInits inits) {
        this(ProjectRoleContribution.class, metadata, inits);
    }

    public QProjectRoleContribution(Class<? extends ProjectRoleContribution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profilePortfolio = inits.isInitialized("profilePortfolio") ? new QProfilePortfolio(forProperty("profilePortfolio"), inits.get("profilePortfolio")) : null;
    }

}

