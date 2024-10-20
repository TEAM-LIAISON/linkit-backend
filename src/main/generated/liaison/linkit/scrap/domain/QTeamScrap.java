package liaison.linkit.scrap.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamScrap is a Querydsl query type for TeamScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamScrap extends EntityPathBase<TeamScrap> {

    private static final long serialVersionUID = -301825079L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamScrap teamScrap = new QTeamScrap("teamScrap");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.team.domain.QTeam team;

    public QTeamScrap(String variable) {
        this(TeamScrap.class, forVariable(variable), INITS);
    }

    public QTeamScrap(Path<? extends TeamScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamScrap(PathMetadata metadata, PathInits inits) {
        this(TeamScrap.class, metadata, inits);
    }

    public QTeamScrap(Class<? extends TeamScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

