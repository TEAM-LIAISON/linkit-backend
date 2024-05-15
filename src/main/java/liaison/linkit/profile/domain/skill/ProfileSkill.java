package liaison.linkit.profile.domain.skill;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.skill.ProfileSkillUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProfileSkill {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "profile_skill_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    public static ProfileSkill of(
            final Profile profile,
            final Skill skill
    ) {
        return new ProfileSkill(
                null,
                profile,
                skill
        );
    }

    public void update(final ProfileSkillUpdateRequest updateRequest) {

    }
}
