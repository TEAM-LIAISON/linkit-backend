package liaison.linkit.member.domain.type;

public enum MemberProfileType {
    NO_PERMISSION,
    PROFILE_OPEN_PERMISSION,
    MATCHING_PERMISSION;

    public static MemberProfileType openPermission(final boolean isOpen) {
        if (isOpen) {
            return PROFILE_OPEN_PERMISSION;
        }
        return NO_PERMISSION;
    }

    public static MemberProfileType changePermission(final boolean isMatching) {
        if (isMatching) {
            return MATCHING_PERMISSION;
        }
        return PROFILE_OPEN_PERMISSION;
    }

}
