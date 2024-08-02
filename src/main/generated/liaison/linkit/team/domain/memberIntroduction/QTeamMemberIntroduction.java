package liaison.linkit.team.domain.memberIntroduction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberIntroduction is a Querydsl query type for TeamMemberIntroduction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberIntroduction extends EntityPathBase<TeamMemberIntroduction> {

    private static final long serialVersionUID = 722835978L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberIntroduction teamMemberIntroduction = new QTeamMemberIntroduction("teamMemberIntroduction");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamMemberIntroductionText = createString("teamMemberIntroductionText");

    public final StringPath teamMemberName = createString("teamMemberName");

    public final StringPath teamMemberRole = createString("teamMemberRole");

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public QTeamMemberIntroduction(String variable) {
        this(TeamMemberIntroduction.class, forVariable(variable), INITS);
    }

    public QTeamMemberIntroduction(Path<? extends TeamMemberIntroduction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberIntroduction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberIntroduction(PathMetadata metadata, PathInits inits) {
        this(TeamMemberIntroduction.class, metadata, inits);
    }

    public QTeamMemberIntroduction(Class<? extends TeamMemberIntroduction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

