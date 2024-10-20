package liaison.linkit.scrap.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberAnnouncementScrap is a Querydsl query type for TeamMemberAnnouncementScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncementScrap extends EntityPathBase<TeamMemberAnnouncementScrap> {

    private static final long serialVersionUID = 1660315976L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberAnnouncementScrap teamMemberAnnouncementScrap = new QTeamMemberAnnouncementScrap("teamMemberAnnouncementScrap");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement teamMemberAnnouncement;

    public QTeamMemberAnnouncementScrap(String variable) {
        this(TeamMemberAnnouncementScrap.class, forVariable(variable), INITS);
    }

    public QTeamMemberAnnouncementScrap(Path<? extends TeamMemberAnnouncementScrap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberAnnouncementScrap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberAnnouncementScrap(PathMetadata metadata, PathInits inits) {
        this(TeamMemberAnnouncementScrap.class, metadata, inits);
    }

    public QTeamMemberAnnouncementScrap(Class<? extends TeamMemberAnnouncementScrap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement")) : null;
    }

}

