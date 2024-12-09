package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import liaison.linkit.team.domain.state.TeamState;


/**
 * QTeamState is a Querydsl query type for TeamState
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamState extends EntityPathBase<TeamState> {

    private static final long serialVersionUID = -1969860205L;

    public static final QTeamState teamState = new QTeamState("teamState");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamStateName = createString("teamStateName");

    public QTeamState(String variable) {
        super(TeamState.class, forVariable(variable));
    }

    public QTeamState(Path<? extends TeamState> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamState(PathMetadata metadata) {
        super(TeamState.class, metadata);
    }

}

