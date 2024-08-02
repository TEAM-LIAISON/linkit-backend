package liaison.linkit.team.domain.miniprofile;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QIndustrySector is a Querydsl query type for IndustrySector
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIndustrySector extends EntityPathBase<IndustrySector> {

    private static final long serialVersionUID = -1592249503L;

    public static final QIndustrySector industrySector = new QIndustrySector("industrySector");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath sectorName = createString("sectorName");

    public QIndustrySector(String variable) {
        super(IndustrySector.class, forVariable(variable));
    }

    public QIndustrySector(Path<? extends IndustrySector> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIndustrySector(PathMetadata metadata) {
        super(IndustrySector.class, metadata);
    }

}

