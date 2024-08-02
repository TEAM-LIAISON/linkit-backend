package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberAnnouncementJobRole is a Querydsl query type for TeamMemberAnnouncementJobRole
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncementJobRole extends EntityPathBase<TeamMemberAnnouncementJobRole> {

    private static final long serialVersionUID = -1215453809L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = new QTeamMemberAnnouncementJobRole("teamMemberAnnouncementJobRole");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.role.QJobRole jobRole;

    public final QTeamMemberAnnouncement teamMemberAnnouncement;

    public QTeamMemberAnnouncementJobRole(String variable) {
        this(TeamMemberAnnouncementJobRole.class, forVariable(variable), INITS);
    }

    public QTeamMemberAnnouncementJobRole(Path<? extends TeamMemberAnnouncementJobRole> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberAnnouncementJobRole(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberAnnouncementJobRole(PathMetadata metadata, PathInits inits) {
        this(TeamMemberAnnouncementJobRole.class, metadata, inits);
    }

    public QTeamMemberAnnouncementJobRole(Class<? extends TeamMemberAnnouncementJobRole> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jobRole = inits.isInitialized("jobRole") ? new liaison.linkit.profile.domain.role.QJobRole(forProperty("jobRole")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement"), inits.get("teamMemberAnnouncement")) : null;
    }

}

