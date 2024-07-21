package liaison.linkit.matching.domain;

import jakarta.persistence.*;
import liaison.linkit.matching.domain.type.MatchingStatus;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class TeamMatching {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_matching_id")
    private Long id;

    // 발신자의 ID
    // 매칭 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_member_announcement_id")
    private TeamMemberAnnouncement teamMemberAnnouncement;

    @Column(name = "sender_type")
    @Enumerated(value = STRING)
    private SenderType senderType;

    // 어떤 소개서에 요청 보낸 것인지 type 필요
    @Column(name = "matching_type")
    @Enumerated(value = STRING)
    private MatchingType matchingType;

    // 요청할 때 보내는 메시지
    @Column(name = "request_message")
    private String requestMessage;

    // 해당 매칭의 상태 관리 필요
    @Column(name = "matching_status")
    @Enumerated(value = STRING)
    private MatchingStatus matchingStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateMatchingStatus(final boolean isAllow) {
        if (isAllow) {
            this.matchingStatus = MatchingStatus.SUCCESSFUL;
        } else {
            this.matchingStatus = MatchingStatus.REQUESTED;
        }
    }
}
