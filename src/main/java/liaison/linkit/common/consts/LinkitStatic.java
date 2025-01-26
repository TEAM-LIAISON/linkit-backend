package liaison.linkit.common.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkitStatic {
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int DUPLICATE = 409;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int INTERNAL_SERVER = 500;
}
