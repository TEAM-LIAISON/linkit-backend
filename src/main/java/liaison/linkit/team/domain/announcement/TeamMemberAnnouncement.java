package liaison.linkit.team.domain.announcement;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberAnnouncement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_announcement_id")
    private Long id;

    private String announcementTitle;

    // 찾는 포지션 (필터 항목)

    // 요구 스킬 (필터 항목)
    private String announcementStartDate; // 공고 시작 기간
    private String announcementEndDate; // 공고 마감 기간
    private boolean isRegionFlexible; // 지역 무관
    private String detailedAnnouncement; // 세부 공고 작성
    private String mainTasks; // 주요 업무
    private String workMethod; // 업무 방식
    private String idealCandidate; // 이런 분을 찾고 있어요
    private String preferredQualifications; // 이런 분이면 더 좋아요
    private String joiningProcess; // 이런 과정으로 합류해요
    private String benefits; // 합류하면 이런 것들을 얻어 갈 수 있어요
}
