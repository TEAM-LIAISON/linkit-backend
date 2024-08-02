package liaison.linkit.profile.domain.antecedents;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAntecedents is a Querydsl query type for Antecedents
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAntecedents extends EntityPathBase<Antecedents> {

    private static final long serialVersionUID = -1668007667L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAntecedents antecedents = new QAntecedents("antecedents");

    public final StringPath antecedentsDescription = createString("antecedentsDescription");

    public final StringPath endDate = createString("endDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public final StringPath projectName = createString("projectName");

    public final StringPath projectRole = createString("projectRole");

    public final BooleanPath retirement = createBoolean("retirement");

    public final StringPath startDate = createString("startDate");

    public QAntecedents(String variable) {
        this(Antecedents.class, forVariable(variable), INITS);
    }

    public QAntecedents(Path<? extends Antecedents> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAntecedents(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAntecedents(PathMetadata metadata, PathInits inits) {
        this(Antecedents.class, metadata, inits);
    }

    public QAntecedents(Class<? extends Antecedents> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

