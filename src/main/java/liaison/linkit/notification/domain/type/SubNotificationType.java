package liaison.linkit.notification.domain.type;

public enum SubNotificationType {
    // MATCHING 관련
    MATCHING_REQUESTED,
    MATCHING_ACCEPTED,
    MATCHING_REJECTED,

    // CHATTING 관련
    NEW_CHAT,

    // TEAM_INVITATION 관련
    TEAM_INVITATION_REQUESTED,
    TEAM_MEMBER_JOINED,

    // TEAM 관련
    REMOVE_TEAM_REQUESTED,
    REMOVE_TEAM_REJECTED,
    REMOVE_TEAM_COMPLETED
}
