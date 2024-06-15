package liaison.linkit.notification.domain.type;

public enum NotificationType {
    // 발신자의 이름이 필요
    RECEIVE_PROFILE_MATCHING("님이 이력서에 매칭 요청을 보냈습니다."),
    RECEIVE_TEAM_PROFILE_MATCHING("님이 팀 소개서에 매칭 요청을 보냈습니다."),
    ACCEPT_MATCHING("님과의 매칭이 성사되었습니다."),
    REJECT_MATCHING("님과의 매칭이 거절되었습니다.");

    private final String messageTemplate;

    NotificationType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessage(String name) {
        return name + messageTemplate;
    }
}
