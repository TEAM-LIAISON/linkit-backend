package liaison.linkit.auth.utils;

import java.util.Optional;

import liaison.linkit.auth.domain.Accessor;

public final class AuthUtils {

    // 생성자를 private으로 선언하여 인스턴스화 방지
    private AuthUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * 인증된 사용자인 경우 memberId를 추출합니다.
     *
     * @param accessor 인증 정보를 포함한 Accessor
     * @return 인증된 사용자의 memberId가 포함된 Optional, 인증되지 않은 경우 Optional.empty()
     */
    public static Optional<Long> extractMemberId(Accessor accessor) {
        return accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();
    }
}
