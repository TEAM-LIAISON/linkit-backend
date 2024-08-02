package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberAnnouncementSkill is a Querydsl query type for TeamMemberAnnouncementSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncementSkill extends EntityPathBase<TeamMemberAnnouncementSkill> {

    private static final long serialVersionUID = 2107491597L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberAnnouncementSkill teamMemberAnnouncementSkill = new QTeamMemberAnnouncementSkill("teamMemberAnnouncementSkill");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.skill.QSkill skill;

    public final QTeamMemberAnnouncement teamMemberAnnouncement;

    public QTeamMemberAnnouncementSkill(String variable) {
        this(TeamMemberAnnouncementSkill.class, forVariable(variable), INITS);
    }

    public QTeamMemberAnnouncementSkill(Path<? extends TeamMemberAnnouncementSkill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberAnnouncementSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberAnnouncementSkill(PathMetadata metadata, PathInits inits) {
        this(TeamMemberAnnouncementSkill.class, metadata, inits);
    }

    public QTeamMemberAnnouncementSkill(Class<? extends TeamMemberAnnouncementSkill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.skill = inits.isInitialized("skill") ? new liaison.linkit.profile.domain.skill.QSkill(forProperty("skill")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement"), inits.get("teamMemberAnnouncement")) : null;
    }

}

