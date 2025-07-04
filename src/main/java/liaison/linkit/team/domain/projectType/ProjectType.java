package liaison.linkit.team.domain.projectType;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProjectType {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "project_type_name", nullable = false)
    private String projectTypeName;
}
