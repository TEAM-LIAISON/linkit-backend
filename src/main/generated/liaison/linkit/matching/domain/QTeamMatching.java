package liaison.linkit.matching.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMatching is a Querydsl query type for TeamMatching
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMatching extends EntityPathBase<TeamMatching> {

    private static final long serialVersionUID = 242081115L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMatching teamMatching = new QTeamMatching("teamMatching");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isReceiverCheck = createBoolean("isReceiverCheck");

    public final BooleanPath isSenderCheck = createBoolean("isSenderCheck");

    public final EnumPath<liaison.linkit.matching.domain.type.MatchingStatusType> matchingStatusType = createEnum("matchingStatusType", liaison.linkit.matching.domain.type.MatchingStatusType.class);

    public final EnumPath<liaison.linkit.matching.domain.type.MatchingType> matchingType = createEnum("matchingType", liaison.linkit.matching.domain.type.MatchingType.class);

    public final liaison.linkit.member.domain.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath requestMessage = createString("requestMessage");

    public final EnumPath<liaison.linkit.matching.domain.type.RequestSenderDeleteStatusType> requestSenderDeleteStatusType = createEnum("requestSenderDeleteStatusType", liaison.linkit.matching.domain.type.RequestSenderDeleteStatusType.class);

    public final EnumPath<liaison.linkit.matching.domain.type.SenderType> senderType = createEnum("senderType", liaison.linkit.matching.domain.type.SenderType.class);

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final EnumPath<liaison.linkit.matching.domain.type.SuccessReceiverDeleteStatusType> successReceiverDeleteStatusType = createEnum("successReceiverDeleteStatusType", liaison.linkit.matching.domain.type.SuccessReceiverDeleteStatusType.class);

    public final EnumPath<liaison.linkit.matching.domain.type.SuccessSenderDeleteStatusType> successSenderDeleteStatusType = createEnum("successSenderDeleteStatusType", liaison.linkit.matching.domain.type.SuccessSenderDeleteStatusType.class);

    public final liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement teamMemberAnnouncement;

    public QTeamMatching(String variable) {
        this(TeamMatching.class, forVariable(variable), INITS);
    }

    public QTeamMatching(Path<? extends TeamMatching> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMatching(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMatching(PathMetadata metadata, PathInits inits) {
        this(TeamMatching.class, metadata, inits);
    }

    public QTeamMatching(Class<? extends TeamMatching> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.teamMemberAnnouncement = inits.isInitialized("teamMemberAnnouncement") ? new liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement(forProperty("teamMemberAnnouncement"), inits.get("teamMemberAnnouncement")) : null;
    }

}

