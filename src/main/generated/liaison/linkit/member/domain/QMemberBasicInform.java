package liaison.linkit.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberBasicInform is a Querydsl query type for MemberBasicInform
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberBasicInform extends EntityPathBase<MemberBasicInform> {

    private static final long serialVersionUID = 648770815L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberBasicInform memberBasicInform = new QMemberBasicInform("memberBasicInform");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    public final BooleanPath ageCheck = createBoolean("ageCheck");

    public final StringPath contact = createString("contact");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath marketingAgree = createBoolean("marketingAgree");

    public final QMember member;

    public final StringPath memberName = createString("memberName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath privateInformAgree = createBoolean("privateInformAgree");

    public final BooleanPath serviceUseAgree = createBoolean("serviceUseAgree");

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public QMemberBasicInform(String variable) {
        this(MemberBasicInform.class, forVariable(variable), INITS);
    }

    public QMemberBasicInform(Path<? extends MemberBasicInform> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberBasicInform(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberBasicInform(PathMetadata metadata, PathInits inits) {
        this(MemberBasicInform.class, metadata, inits);
    }

    public QMemberBasicInform(Class<? extends MemberBasicInform> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

