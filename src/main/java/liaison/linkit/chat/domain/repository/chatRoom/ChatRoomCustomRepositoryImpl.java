package liaison.linkit.chat.domain.repository.chatRoom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.QChatRoom;
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
                .where(qChatRoom.participantAMemberId.eq(memberId)
                        .or(qChatRoom.participantBMemberId.eq(memberId)))
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
}
