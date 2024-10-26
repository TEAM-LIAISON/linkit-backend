package liaison.linkit.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1352825112L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final liaison.linkit.common.domain.QBaseDateTimeEntity _super = new liaison.linkit.common.domain.QBaseDateTimeEntity(this);

    public final BooleanPath consentServiceUse = createBoolean("consentServiceUse");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath createMemberBasicInform = createBoolean("createMemberBasicInform");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMemberBasicInform memberBasicInform;

    public final EnumPath<liaison.linkit.member.domain.type.MemberState> memberState = createEnum("memberState", liaison.linkit.member.domain.type.MemberState.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> privateScrapCount = createNumber("privateScrapCount", Integer.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public final StringPath socialLoginId = createString("socialLoginId");

    public final NumberPath<Integer> teamMemberAnnouncementScrapCount = createNumber("teamMemberAnnouncementScrapCount", Integer.class);

    public final NumberPath<Integer> teamScrapCount = createNumber("teamScrapCount", Integer.class);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberBasicInform = inits.isInitialized("memberBasicInform") ? new QMemberBasicInform(forProperty("memberBasicInform"), inits.get("memberBasicInform")) : null;
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

