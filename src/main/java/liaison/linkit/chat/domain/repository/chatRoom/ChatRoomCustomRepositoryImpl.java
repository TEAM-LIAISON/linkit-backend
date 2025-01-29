package liaison.linkit.chat.domain.repository.chatRoom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.QChatRoom;
import liaison.linkit.global.type.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatRoom> findAllChatRoomsByMemberId(final Long memberId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;

        return jpaQueryFactory
                .selectFrom(qChatRoom).distinct()
                .where(
                        // (A_Member = memberId AND A_Status = USABLE)
                        //    OR (B_Member = memberId AND B_Status = USABLE)
                        qChatRoom.participantAMemberId.eq(memberId)
                                .and(qChatRoom.participantAStatus.eq(StatusType.USABLE))
                                .or(
                                        qChatRoom.participantBMemberId.eq(memberId)
                                                .and(qChatRoom.participantBStatus.eq(StatusType.USABLE))
                                )
                )
                .fetch();
    }

    @Override
    public boolean existsChatRoomByMatchingId(final Long matchingId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;

        Integer count = jpaQueryFactory.selectOne()
                .from(qChatRoom)
                .where(qChatRoom.matchingId.eq(matchingId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public Long getChatRoomIdByMatchingId(final Long matchingId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;

        return jpaQueryFactory
                .select(qChatRoom.id)
                .from(qChatRoom)
                .where(qChatRoom.matchingId.eq(matchingId))
                .fetchOne();
    }

    @Override
    public boolean existsChatRoomByMemberId(final Long memberId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;

        // 해당 회원이 A 또는 B 참여자로 있는 채팅방이 존재하는지 확인
        Integer count = jpaQueryFactory
                .selectOne()
                .from(qChatRoom)
                .where(qChatRoom.participantAMemberId.eq(memberId)
                        .or(qChatRoom.participantBMemberId.eq(memberId)))
                .fetchFirst();

        // count가 null이 아닌 경우 true 반환
        return count != null;
    }

}
