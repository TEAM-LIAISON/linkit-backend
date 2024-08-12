package liaison.linkit.profile.domain.teambuilding;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileTeamBuildingField is a Querydsl query type for ProfileTeamBuildingField
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileTeamBuildingField extends EntityPathBase<ProfileTeamBuildingField> {

    private static final long serialVersionUID = -248945472L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileTeamBuildingField profileTeamBuildingField = new QProfileTeamBuildingField("profileTeamBuildingField");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.QProfile profile;

    public final QTeamBuildingField teamBuildingField;

    public QProfileTeamBuildingField(String variable) {
        this(ProfileTeamBuildingField.class, forVariable(variable), INITS);
    }

    public QProfileTeamBuildingField(Path<? extends ProfileTeamBuildingField> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileTeamBuildingField(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileTeamBuildingField(PathMetadata metadata, PathInits inits) {
        this(ProfileTeamBuildingField.class, metadata, inits);
    }

    public QProfileTeamBuildingField(Class<? extends ProfileTeamBuildingField> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
        this.teamBuildingField = inits.isInitialized("teamBuildingField") ? new QTeamBuildingField(forProperty("teamBuildingField")) : null;
    }

}

