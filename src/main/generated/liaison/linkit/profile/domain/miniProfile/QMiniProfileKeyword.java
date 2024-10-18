package liaison.linkit.profile.domain.miniProfile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMiniProfileKeyword is a Querydsl query type for MiniProfileKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMiniProfileKeyword extends EntityPathBase<MiniProfileKeyword> {

    private static final long serialVersionUID = -626949360L;

    public static final QMiniProfileKeyword miniProfileKeyword = new QMiniProfileKeyword("miniProfileKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<MiniProfile> miniProfile = createNumber("miniProfile", MiniProfile.class);

    public final StringPath myKeywordNames = createString("myKeywordNames");

    public QMiniProfileKeyword(String variable) {
        super(MiniProfileKeyword.class, forVariable(variable));
    }

    public QMiniProfileKeyword(Path<? extends MiniProfileKeyword> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMiniProfileKeyword(PathMetadata metadata) {
        super(MiniProfileKeyword.class, metadata);
    }

}

