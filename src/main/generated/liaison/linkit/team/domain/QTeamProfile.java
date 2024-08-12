package liaison.linkit.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamProfile is a Querydsl query type for TeamProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamProfile extends EntityPathBase<TeamProfile> {

    private static final long serialVersionUID = -1662329301L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamProfile teamProfile = new QTeamProfile("teamProfile");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActivity = createBoolean("isActivity");

    public final BooleanPath isActivityMethod = createBoolean("isActivityMethod");

    public final BooleanPath isActivityRegion = createBoolean("isActivityRegion");

    public final BooleanPath isHistory = createBoolean("isHistory");

    public final BooleanPath isTeamAttach = createBoolean("isTeamAttach");

    public final BooleanPath isTeamAttachFile = createBoolean("isTeamAttachFile");

    public final BooleanPath isTeamAttachUrl = createBoolean("isTeamAttachUrl");

    public final BooleanPath isTeamIntroduction = createBoolean("isTeamIntroduction");

    public final BooleanPath isTeamMemberAnnouncement = createBoolean("isTeamMemberAnnouncement");

    public final BooleanPath isTeamMemberIntroduction = createBoolean("isTeamMemberIntroduction");

    public final BooleanPath isTeamMiniProfile = createBoolean("isTeamMiniProfile");

    public final BooleanPath isTeamProfileTeamBuildingField = createBoolean("isTeamProfileTeamBuildingField");

    public final liaison.linkit.member.domain.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final StringPath teamIntroduction = createString("teamIntroduction");

    public final liaison.linkit.team.domain.miniprofile.QTeamMiniProfile teamMiniProfile;

    public final NumberPath<Double> teamProfileCompletion = createNumber("teamProfileCompletion", Double.class);

    public QTeamProfile(String variable) {
        this(TeamProfile.class, forVariable(variable), INITS);
    }

    public QTeamProfile(Path<? extends TeamProfile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamProfile(PathMetadata metadata, PathInits inits) {
        this(TeamProfile.class, metadata, inits);
    }

    public QTeamProfile(Class<? extends TeamProfile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.teamMiniProfile = inits.isInitialized("teamMiniProfile") ? new liaison.linkit.team.domain.miniprofile.QTeamMiniProfile(forProperty("teamMiniProfile"), inits.get("teamMiniProfile")) : null;
    }

}

