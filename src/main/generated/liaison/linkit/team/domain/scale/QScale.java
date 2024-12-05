package liaison.linkit.team.domain.scale;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScale is a Querydsl query type for Scale
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScale extends EntityPathBase<Scale> {

    private static final long serialVersionUID = 896907589L;

    public static final QScale scale = new QScale("scale");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath scaleName = createString("scaleName");

    public QScale(String variable) {
        super(Scale.class, forVariable(variable));
    }

    public QScale(Path<? extends Scale> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScale(PathMetadata metadata) {
        super(Scale.class, metadata);
    }

}

