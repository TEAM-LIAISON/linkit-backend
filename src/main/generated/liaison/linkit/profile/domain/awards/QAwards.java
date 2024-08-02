package liaison.linkit.profile.domain.awards;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAwards is a Querydsl query type for Awards
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAwards extends EntityPathBase<Awards> {

    private static final long serialVersionUID = 135431889L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAwards awards = new QAwards("awards");

    public final StringPath awardsDescription = createString("awardsDescription");

    public final NumberPath<Integer> awardsMonth = createNumber("awardsMonth", Integer.class);

    public final StringPath awardsName = createString("awardsName");

    public final NumberPath<Integer> awardsYear = createNumber("awardsYear", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath organizer = createString("organizer");

    public final liaison.linkit.profile.domain.QProfile profile;

    public final StringPath ranking = createString("ranking");

    public QAwards(String variable) {
        this(Awards.class, forVariable(variable), INITS);
    }

    public QAwards(Path<? extends Awards> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAwards(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAwards(PathMetadata metadata, PathInits inits) {
        this(Awards.class, metadata, inits);
    }

    public QAwards(Class<? extends Awards> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

