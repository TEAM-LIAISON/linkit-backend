package liaison.linkit.team.domain.memberIntroduction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeamMemberIntroduction is a Querydsl query type for TeamMemberIntroduction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberIntroduction extends EntityPathBase<TeamMemberIntroduction> {

    private static final long serialVersionUID = 722835978L;

    public static final QTeamMemberIntroduction teamMemberIntroduction = new QTeamMemberIntroduction("teamMemberIntroduction");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamMemberIntroductionText = createString("teamMemberIntroductionText");

    public final StringPath teamMemberName = createString("teamMemberName");

    public final StringPath teamMemberRole = createString("teamMemberRole");

    public final NumberPath<TeamProfile> teamProfile = createNumber("teamProfile", TeamProfile.class);

    public QTeamMemberIntroduction(String variable) {
        super(TeamMemberIntroduction.class, forVariable(variable));
    }

    public QTeamMemberIntroduction(Path<? extends TeamMemberIntroduction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamMemberIntroduction(PathMetadata metadata) {
        super(TeamMemberIntroduction.class, metadata);
    }

}

