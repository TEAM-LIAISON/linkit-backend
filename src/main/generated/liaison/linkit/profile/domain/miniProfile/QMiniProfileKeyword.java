package liaison.linkit.profile.domain.miniProfile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMiniProfileKeyword is a Querydsl query type for MiniProfileKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMiniProfileKeyword extends EntityPathBase<MiniProfileKeyword> {

    private static final long serialVersionUID = -626949360L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMiniProfileKeyword miniProfileKeyword = new QMiniProfileKeyword("miniProfileKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMiniProfile miniProfile;

    public final StringPath myKeywordNames = createString("myKeywordNames");

    public QMiniProfileKeyword(String variable) {
        this(MiniProfileKeyword.class, forVariable(variable), INITS);
    }

    public QMiniProfileKeyword(Path<? extends MiniProfileKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMiniProfileKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMiniProfileKeyword(PathMetadata metadata, PathInits inits) {
        this(MiniProfileKeyword.class, metadata, inits);
    }

    public QMiniProfileKeyword(Class<? extends MiniProfileKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.miniProfile = inits.isInitialized("miniProfile") ? new QMiniProfile(forProperty("miniProfile"), inits.get("miniProfile")) : null;
    }

}

