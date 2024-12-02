package liaison.linkit.team.domain.announcement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnnouncementSkill is a Querydsl query type for AnnouncementSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnnouncementSkill extends EntityPathBase<AnnouncementSkill> {

    private static final long serialVersionUID = -1616522204L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnnouncementSkill announcementSkill = new QAnnouncementSkill("announcementSkill");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.skill.QSkill skill;

    public final QTeamMemberAnnouncement teamMemberAnnouncement;

    public QAnnouncementSkill(String variable) {
        this(AnnouncementSkill.class, forVariable(variable), INITS);
    }

    public QAnnouncementSkill(Path<? extends AnnouncementSkill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnnouncementSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnnouncementSkill(PathMetadata metadata, PathInits inits) {
        this(AnnouncementSkill.class, metadata, inits);
    }

    public QAnnouncementSkill(Class<? extends AnnouncementSkill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.skill = inits.isInitialized("skill") ? new liaison.linkit.profile.domain.skill.QSkill(forProperty("skill")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement"), inits.get("teamMemberAnnouncement")) : null;
    }

}

