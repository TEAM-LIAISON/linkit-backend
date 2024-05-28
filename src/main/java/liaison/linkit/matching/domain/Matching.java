package liaison.linkit.matching.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Matching {

    // 매칭 아이디
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    // 매칭 요청을 보낸 사람의 아이디가 외래키로 작동한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "receive_id")
    private Long receiveId;

}
