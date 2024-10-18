package liaison.linkit.wish.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberAnnouncementWish is a Querydsl query type for TeamMemberAnnouncementWish
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberAnnouncementWish extends EntityPathBase<TeamMemberAnnouncementWish> {

    private static final long serialVersionUID = -223411120L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberAnnouncementWish teamMemberAnnouncementWish = new QTeamMemberAnnouncementWish("teamMemberAnnouncementWish");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement teamMemberAnnouncement;

    public QTeamMemberAnnouncementWish(String variable) {
        this(TeamMemberAnnouncementWish.class, forVariable(variable), INITS);
    }

    public QTeamMemberAnnouncementWish(Path<? extends TeamMemberAnnouncementWish> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberAnnouncementWish(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberAnnouncementWish(PathMetadata metadata, PathInits inits) {
        this(TeamMemberAnnouncementWish.class, metadata, inits);
    }

    public QTeamMemberAnnouncementWish(Class<? extends TeamMemberAnnouncementWish> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement")) : null;
    }

}

