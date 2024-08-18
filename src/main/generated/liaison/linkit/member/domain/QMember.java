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

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final BooleanPath existDefaultPrivateProfile = createBoolean("existDefaultPrivateProfile");

    public final BooleanPath existDefaultTeamProfile = createBoolean("existDefaultTeamProfile");

    public final BooleanPath existMemberBasicInform = createBoolean("existMemberBasicInform");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMemberBasicInform memberBasicInform;

    public final EnumPath<liaison.linkit.member.domain.type.MemberType> memberType = createEnum("memberType", liaison.linkit.member.domain.type.MemberType.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> privateWishCount = createNumber("privateWishCount", Integer.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public final EnumPath<liaison.linkit.member.domain.type.ProfileType> profileType = createEnum("profileType", liaison.linkit.member.domain.type.ProfileType.class);

    public final StringPath socialLoginId = createString("socialLoginId");

    public final EnumPath<liaison.linkit.member.domain.type.MemberState> status = createEnum("status", liaison.linkit.member.domain.type.MemberState.class);

    public final liaison.linkit.team.domain.QTeamProfile teamProfile;

    public final EnumPath<liaison.linkit.member.domain.type.TeamProfileType> teamProfileType = createEnum("teamProfileType", liaison.linkit.member.domain.type.TeamProfileType.class);

    public final NumberPath<Integer> teamWishCount = createNumber("teamWishCount", Integer.class);

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
        this.teamProfile = inits.isInitialized("teamProfile") ? new liaison.linkit.team.domain.QTeamProfile(forProperty("teamProfile"), inits.get("teamProfile")) : null;
    }

}

