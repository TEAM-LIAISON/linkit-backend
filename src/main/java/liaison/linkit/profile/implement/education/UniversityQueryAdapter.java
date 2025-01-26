package liaison.linkit.profile.implement.education;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.common.domain.University;
import liaison.linkit.profile.domain.repository.education.UniversityRepository;
import liaison.linkit.profile.exception.education.UniversityNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class UniversityQueryAdapter {
    final UniversityRepository universityRepository;

    public University findUniversityByUniversityName(final String universityName) {
        return universityRepository.findUniversityByUniversityName(universityName).orElseThrow(() -> UniversityNotFoundException.EXCEPTION);
    }
}
