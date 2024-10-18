package liaison.linkit.team.domain.miniprofile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMiniProfile is a Querydsl query type for TeamMiniProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMiniProfile extends EntityPathBase<TeamMiniProfile> {

    private static final long serialVersionUID = 1952020952L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMiniProfile teamMiniProfile = new QTeamMiniProfile("teamMiniProfile");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QIndustrySector industrySector;

    public final BooleanPath isTeamActivate = createBoolean("isTeamActivate");

    public final StringPath teamLogoImageUrl = createString("teamLogoImageUrl");

    public final StringPath teamName = createString("teamName");

    public final NumberPath<TeamProfile> teamProfile = createNumber("teamProfile", TeamProfile.class);

    public final StringPath teamProfileTitle = createString("teamProfileTitle");

    public final QTeamScale teamScale;

    public QTeamMiniProfile(String variable) {
        this(TeamMiniProfile.class, forVariable(variable), INITS);
    }

    public QTeamMiniProfile(Path<? extends TeamMiniProfile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMiniProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMiniProfile(PathMetadata metadata, PathInits inits) {
        this(TeamMiniProfile.class, metadata, inits);
    }

    public QTeamMiniProfile(Class<? extends TeamMiniProfile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.industrySector = inits.isInitialized("industrySector") ? new QIndustrySector(forProperty("industrySector")) : null;
        this.teamScale = inits.isInitialized("teamScale") ? new QTeamScale(forProperty("teamScale")) : null;
    }

}

