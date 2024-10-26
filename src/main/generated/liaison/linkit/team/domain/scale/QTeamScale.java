package liaison.linkit.team.domain.scale;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamScale is a Querydsl query type for TeamScale
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamScale extends EntityPathBase<TeamScale> {

    private static final long serialVersionUID = 178914152L;

    public static final QTeamScale teamScale = new QTeamScale("teamScale");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath scaleName = createString("scaleName");

    public QTeamScale(String variable) {
        super(TeamScale.class, forVariable(variable));
    }

    public QTeamScale(Path<? extends TeamScale> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamScale(PathMetadata metadata) {
        super(TeamScale.class, metadata);
    }

}

