package liaison.linkit.global.util;

import java.security.Principal;

import lombok.Getter;

@Getter
public class UserPrincipal implements Principal {
    private final String name;

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
