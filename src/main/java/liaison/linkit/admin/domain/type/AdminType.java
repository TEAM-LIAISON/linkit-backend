package liaison.linkit.admin.domain.type;

import liaison.linkit.global.exception.AdminException;

import java.util.Arrays;

import static liaison.linkit.global.exception.ExceptionCode.NULL_ADMIN_AUTHORITY;

public enum AdminType {
    ADMIN,
    MASTER;

    public static AdminType getMappedAdminType(final String adminType) {
        return Arrays.stream(values())
                .filter(value -> value.name().toUpperCase().equals(adminType))
                .findAny()
                .orElseThrow(() -> new AdminException(NULL_ADMIN_AUTHORITY));
    }
}
