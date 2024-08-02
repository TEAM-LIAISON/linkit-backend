package liaison.linkit.profile.domain.education;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDegree is a Querydsl query type for Degree
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDegree extends EntityPathBase<Degree> {

    private static final long serialVersionUID = -630750385L;

    public static final QDegree degree = new QDegree("degree");

    public final StringPath degreeName = createString("degreeName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDegree(String variable) {
        super(Degree.class, forVariable(variable));
    }

    public QDegree(Path<? extends Degree> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDegree(PathMetadata metadata) {
        super(Degree.class, metadata);
    }

}

