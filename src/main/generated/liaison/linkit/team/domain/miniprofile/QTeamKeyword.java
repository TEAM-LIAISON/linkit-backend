package liaison.linkit.team.domain.miniprofile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamKeyword is a Querydsl query type for TeamKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamKeyword extends EntityPathBase<TeamKeyword> {

    private static final long serialVersionUID = -1972105873L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamKeyword teamKeyword = new QTeamKeyword("teamKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.team.domain.QTeam team;

    public final StringPath teamKeywordNames = createString("teamKeywordNames");

    public QTeamKeyword(String variable) {
        this(TeamKeyword.class, forVariable(variable), INITS);
    }

    public QTeamKeyword(Path<? extends TeamKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamKeyword(PathMetadata metadata, PathInits inits) {
        this(TeamKeyword.class, metadata, inits);
    }

    public QTeamKeyword(Class<? extends TeamKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

