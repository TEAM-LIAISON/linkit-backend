package liaison.linkit.wish.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.wish.domain.type.WishType;
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
public class Wish {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "receive_wish_id")
    private Long receiveWishId;

    @Column(name = "wish_type")
    private WishType wishType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wish(
            final Long id,
            final Member member,
            final Long receiveWishId,
            final WishType wishType,
            final LocalDateTime createdAt
    ) {
        this.id = id;
        this.member = member;
        this.receiveWishId = receiveWishId;
        this.wishType = wishType;
        this.createdAt = LocalDateTime.now();
    }


}
