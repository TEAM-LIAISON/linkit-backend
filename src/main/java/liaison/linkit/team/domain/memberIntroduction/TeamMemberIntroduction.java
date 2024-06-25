package liaison.linkit.team.domain.memberIntroduction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberIntroduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
