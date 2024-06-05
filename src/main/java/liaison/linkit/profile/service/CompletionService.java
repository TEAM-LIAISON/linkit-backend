package liaison.linkit.profile.service;

import liaison.linkit.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompletionService {

    private final ProfileRepository profileRepository;

//    @Transactional(readOnly = true)
//    public CompletionResponse getCompletion(final Long memberId) {
//
//
//        return CompletionResponse.profileCompletion(profile);
//    }
}
