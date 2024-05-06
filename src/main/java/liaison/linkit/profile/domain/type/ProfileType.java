package liaison.linkit.profile.domain.type;

public enum ProfileType {
    NO_PERMISSION,
    PROFILE_OPEN_PERMISSION,
    MATCHING_PERMISSION;

    public static ProfileType openPermission(final boolean isOpen) {
        if (isOpen) {
            return PROFILE_OPEN_PERMISSION;
        }
        return NO_PERMISSION;
    }

    public static ProfileType changePermission(final boolean isMatching) {
        if (isMatching) {
            return MATCHING_PERMISSION;
        }
        return PROFILE_OPEN_PERMISSION;
    }

}
