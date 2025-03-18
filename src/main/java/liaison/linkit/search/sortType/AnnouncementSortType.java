package liaison.linkit.search.sortType;

public enum AnnouncementSortType {
    LATEST("latest", "최신순"),
    POPULAR("popular", "인기순"),
    DEADLINE("deadline", "마감임박순");

    private final String code;
    private final String description;

    AnnouncementSortType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
