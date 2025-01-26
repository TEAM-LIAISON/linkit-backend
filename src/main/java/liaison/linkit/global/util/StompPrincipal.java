package liaison.linkit.global.util;

import java.security.Principal;
import lombok.Getter;

@Getter
public class StompPrincipal implements Principal {
    private final String memberId;
    private final String sessionId;
    private final String version;
    private final String heartbeat;

    public StompPrincipal(String memberId, String sessionId, String version, String heartbeat) {
        this.memberId = memberId;
        this.sessionId = sessionId;
        this.version = version;
        this.heartbeat = heartbeat;
    }

    // 기본 생성자도 유지 (이전 코드와의 호환성을 위해)
    public StompPrincipal(String memberId) {
        this(memberId, null, null, null);
    }

    @Override
    public String getName() {
        return memberId;
    }

    public boolean isConnected() {
        return sessionId != null;
    }
}
