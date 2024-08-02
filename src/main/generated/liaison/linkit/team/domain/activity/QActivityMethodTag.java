package liaison.linkit.team.domain.activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QActivityMethodTag is a Querydsl query type for ActivityMethodTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QActivityMethodTag extends EntityPathBase<ActivityMethodTag> {

    private static final long serialVersionUID = 137944012L;

    public static final QActivityMethodTag activityMethodTag = new QActivityMethodTag("activityMethodTag");

    public final StringPath activityTagName = createString("activityTagName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QActivityMethodTag(String variable) {
        super(ActivityMethodTag.class, forVariable(variable));
    }

    public QActivityMethodTag(Path<? extends ActivityMethodTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QActivityMethodTag(PathMetadata metadata) {
        super(ActivityMethodTag.class, metadata);
    }

}

