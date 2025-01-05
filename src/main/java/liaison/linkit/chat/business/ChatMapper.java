//package liaison.linkit.chat.business;
//
//import liaison.linkit.chat.domain.ChatRoom;
//import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
//import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
//import liaison.linkit.common.annotation.Mapper;
//
//@Mapper
//public class ChatMapper {
//    public ChatResponseDTO.CreateChatRoomResponse toCreateChatRoomResponse(
//            final ChatRoom chatRoom
//    ) {
//        return CreateChatRoomResponse.builder()
//                .chatRoomId(chatRoom.getId())
//                .participantAId(chatRoom.getParticipantAId())
//                .participantAType(chatRoom.getParticipantAType())
//                .participantBId(chatRoom.getParticipantBId())
//                .participantBType(chatRoom.getParticipantBType())
//                .lastMessage(null)
//                .unreadCount(0L)
//                .build();
//    }
//
//}
