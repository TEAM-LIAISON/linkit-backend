package liaison.linkit.team.domain.announcement;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class AnnouncementSkill extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "announcement_skill_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_member_announcement_id")
    private TeamMemberAnnouncement teamMemberAnnouncement;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
