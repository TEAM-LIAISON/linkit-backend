package liaison.linkit.matching.domain;

import jakarta.persistence.*;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Matching {
    // 누가 누구한테 어떤 매칭 요청을 보냈는지 저장 필요

    // 매칭 아이디
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    // 매칭 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 내 이력서 ID, 팀 소개서 ID
    @Column(name = "receive_matching_id")
    private Long receiveMatchingId;

    // 어떤 소개서에 요청 보낸 것인지 type 필요
    @Column(name = "matching_type")
    private MatchingType matchingType;

    @Column(name = "request_message")
    private String requestMessage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
