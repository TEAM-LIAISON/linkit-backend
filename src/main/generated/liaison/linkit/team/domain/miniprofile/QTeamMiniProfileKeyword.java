package liaison.linkit.team.domain.miniprofile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMiniProfileKeyword is a Querydsl query type for TeamMiniProfileKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMiniProfileKeyword extends EntityPathBase<TeamMiniProfileKeyword> {

    private static final long serialVersionUID = -328730383L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMiniProfileKeyword teamMiniProfileKeyword = new QTeamMiniProfileKeyword("teamMiniProfileKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath teamKeywordNames = createString("teamKeywordNames");

    public final QTeamMiniProfile teamMiniProfile;

    public QTeamMiniProfileKeyword(String variable) {
        this(TeamMiniProfileKeyword.class, forVariable(variable), INITS);
    }

    public QTeamMiniProfileKeyword(Path<? extends TeamMiniProfileKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMiniProfileKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMiniProfileKeyword(PathMetadata metadata, PathInits inits) {
        this(TeamMiniProfileKeyword.class, metadata, inits);
    }

    public QTeamMiniProfileKeyword(Class<? extends TeamMiniProfileKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamMiniProfile = inits.isInitialized("teamMiniProfile") ? new QTeamMiniProfile(forProperty("teamMiniProfile"), inits.get("teamMiniProfile")) : null;
    }

}

