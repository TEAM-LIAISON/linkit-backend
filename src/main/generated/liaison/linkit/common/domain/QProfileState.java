package liaison.linkit.common.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProfileState is a Querydsl query type for ProfileState
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileState extends EntityPathBase<ProfileState> {

    private static final long serialVersionUID = -525512393L;

    public static final QProfileState profileState = new QProfileState("profileState");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath profileStateName = createString("profileStateName");

    public QProfileState(String variable) {
        super(ProfileState.class, forVariable(variable));
    }

    public QProfileState(Path<? extends ProfileState> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProfileState(PathMetadata metadata) {
        super(ProfileState.class, metadata);
    }

}

