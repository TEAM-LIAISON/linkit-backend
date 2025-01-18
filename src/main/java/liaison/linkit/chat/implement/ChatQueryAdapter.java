package liaison.linkit.chat.implement;

import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatQueryAdapter {
    private final ChatMessageRepository chatMessageRepository;

    public int countByMessageReceiverMemberIdAndIsReadFalse(final Long memberId) {
        return chatMessageRepository.countByMessageReceiverMemberIdAndIsReadFalse(memberId);
    }
}
