package liaison.linkit.member.domain.repository.memberBasicInform;

import liaison.linkit.member.domain.MemberBasicInform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBasicInformRepository extends JpaRepository<MemberBasicInform, Long>, MemberBasicInformRepositoryCustom {

}
