package liaison.linkit.admin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdminMember is a Querydsl query type for AdminMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminMember extends EntityPathBase<AdminMember> {

    private static final long serialVersionUID = -1364834362L;

    public static final QAdminMember adminMember = new QAdminMember("adminMember");

    public final EnumPath<liaison.linkit.admin.domain.type.AdminType> adminType = createEnum("adminType", liaison.linkit.admin.domain.type.AdminType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastLoginDate = createDateTime("lastLoginDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath password = createString("password");

    public final EnumPath<liaison.linkit.member.domain.type.MemberState> status = createEnum("status", liaison.linkit.member.domain.type.MemberState.class);

    public final StringPath username = createString("username");

    public QAdminMember(String variable) {
        super(AdminMember.class, forVariable(variable));
    }

    public QAdminMember(Path<? extends AdminMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminMember(PathMetadata metadata) {
        super(AdminMember.class, metadata);
    }

}

