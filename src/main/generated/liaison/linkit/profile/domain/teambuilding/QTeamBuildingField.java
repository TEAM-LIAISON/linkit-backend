package liaison.linkit.profile.domain.teambuilding;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamBuildingField is a Querydsl query type for TeamBuildingField
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamBuildingField extends EntityPathBase<TeamBuildingField> {

    private static final long serialVersionUID = 1225806633L;

    public static final QTeamBuildingField teamBuildingField = new QTeamBuildingField("teamBuildingField");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamBuildingFieldName = createString("teamBuildingFieldName");

    public QTeamBuildingField(String variable) {
        super(TeamBuildingField.class, forVariable(variable));
    }

    public QTeamBuildingField(Path<? extends TeamBuildingField> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamBuildingField(PathMetadata metadata) {
        super(TeamBuildingField.class, metadata);
    }

}

