package liaison.linkit.profile.domain.role;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJobRole is a Querydsl query type for JobRole
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobRole extends EntityPathBase<JobRole> {

    private static final long serialVersionUID = -1474999784L;

    public static final QJobRole jobRole = new QJobRole("jobRole");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jobRoleName = createString("jobRoleName");

    public QJobRole(String variable) {
        super(JobRole.class, forVariable(variable));
    }

    public QJobRole(Path<? extends JobRole> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJobRole(PathMetadata metadata) {
        super(JobRole.class, metadata);
    }

}

