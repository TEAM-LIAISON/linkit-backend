package liaison.linkit.common.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileState {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "profile_state_name", nullable = false)
    private String profileStateName;

    public static ProfileState of(final String profileStateName) {
        return new ProfileState(null, profileStateName);
    }
}
