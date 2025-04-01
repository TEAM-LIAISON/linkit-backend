package liaison.linkit.chat.domain.repository.chatRoom;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.QChatRoom;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.member.domain.type.MemberState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatRoom> findAllChatRoomsByMemberId(final Long memberId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;
        QMember qMember = QMember.member;

        // 조건 A: participant A가 해당 멤버이고, A의 상태가 USABLE이며, B가 DELETED가 아님
        BooleanExpression conditionA =
                qChatRoom
                        .participantAMemberId
                        .eq(memberId)
                        .and(qChatRoom.participantAStatus.eq(StatusType.USABLE))
                        .and(
                                JPAExpressions.selectOne()
                                        .from(qMember)
                                        .where(
                                                qMember.id
                                                        .eq(qChatRoom.participantBMemberId)
                                                        .and(
                                                                qMember.memberState.ne(
                                                                        MemberState.DELETED)))
                                        .exists());

        // 조건 B: participant B가 해당 멤버이고, B의 상태가 USABLE이며, A가 DELETED가 아님
        BooleanExpression conditionB =
                qChatRoom
                        .participantBMemberId
                        .eq(memberId)
                        .and(qChatRoom.participantBStatus.eq(StatusType.USABLE))
                        .and(
                                JPAExpressions.selectOne()
                                        .from(qMember)
                                        .where(
                                                qMember.id
                                                        .eq(qChatRoom.participantAMemberId)
                                                        .and(
                                                                qMember.memberState.ne(
                                                                        MemberState.DELETED)))
                                        .exists());

        return jpaQueryFactory
                .selectFrom(qChatRoom)
                .where(conditionA.or(conditionB))
                .orderBy(
                        new CaseBuilder()
                                .when(qChatRoom.lastMessageTime.isNull())
                                .then(qChatRoom.createdAt)
                                .otherwise(qChatRoom.lastMessageTime)
                                .desc())
                .groupBy(qChatRoom.id)
                .fetch();
    }

    @Override
    public boolean existsChatRoomByMatchingId(final Long matchingId) {
        QChatRoom qChatRoom = QChatRoom.chatRoom;

        Integer count =
                jpaQueryFactory
                        .selectOne()
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
        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qChatRoom)
                        .where(
                                qChatRoom
                                        .participantAMemberId
                                        .eq(memberId)
                                        .or(qChatRoom.participantBMemberId.eq(memberId)))
                        .fetchFirst();

        // count가 null이 아닌 경우 true 반환
        return count != null;
    }
}
