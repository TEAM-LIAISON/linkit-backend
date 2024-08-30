package liaison.linkit.global.config;

import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.matching.MatchingAccessInterceptor;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.profile.browse.ProfileBrowseAccessInterceptor;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ProfileBrowseAccessInterceptor.class, MatchingAccessInterceptor.class})
public class CorsTestConfig {

    @Bean
    public ProfileBrowseAccessInterceptor profileBrowseAccessInterceptor(
            final JwtProvider jwtProvider,
            final BearerAuthorizationExtractor extractor,
            final ProfileRepository profileRepository,
            final TeamProfileRepository teamProfileRepository) {
        return new ProfileBrowseAccessInterceptor(
                jwtProvider, extractor, profileRepository, teamProfileRepository
        );
    }

    @Bean
    public MatchingAccessInterceptor matchingAccessInterceptor(
            final JwtProvider jwtProvider,
            final BearerAuthorizationExtractor extractor,
            final MemberRepository memberRepository
    ) {
        return new MatchingAccessInterceptor(jwtProvider, extractor, memberRepository);
    }
}
