//package liaison.linkit.chat.presentation;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class StompWebSocketIntegrationTest {
//
//    private static final String WEBSOCKET_URI = "ws://localhost:{port}/stomp/linkit/chat";
//    private static final String WEBSOCKET_TOPIC = "/topic/chat/room/";
//    private static final String WEBSOCKET_SEND_URL = "/app/chat/message";
//
//    @LocalServerPort
//    private int port;
//
//    private WebSocketStompClient stompClient;
//
//    @BeforeEach
//    void setup() {
//        stompClient = new WebSocketStompClient(new SockJsClient(
//                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//    }
//
//    @Test
//    @DisplayName("WebSocket 연결 테스트")
//    void connectWebSocket() throws Exception {
//        // given
//        StompHeaders connectHeaders = new StompHeaders();
//        connectHeaders.add("Authorization", "Bearer test-token");
//
//        // when
//        StompSession session = stompClient
//                .connect(WEBSOCKET_URI.replace("{port}", String.valueOf(port)),
//                        new WebSocketHttpHeaders(),
//                        connectHeaders,
//                        new StompSessionHandlerAdapter() {
//                        })
//                .get(1, TimeUnit.SECONDS);
//
//        // then
//        assertTrue(session.isConnected());
//    }
//
/// /    @Test /    @DisplayName("채팅방 구독 테스트") /    void subscribeChat() throws Exception { /        // given /        Long chatRoomId = 1L; /        BlockingQueue<ChatMessageResponse> messages = new
/// LinkedBlockingQueue<>(); /        StompSession session = connectSession(); / /        // when /        StompSession.Subscription subscription = session.subscribe( /                WEBSOCKET_TOPIC
/// + chatRoomId, /                new StompFrameHandler() { /                    @Override /                    public Type getPayloadType(StompHeaders headers) { /                        return
/// ChatMessageResponse.class; /                    } / /                    @Override /                    public void handleFrame(StompHeaders headers, Object payload) { /
/// messages.add((ChatMessageResponse) payload); /                    } /                }); / /        // then /        assertNotNull(subscription); /        assertTrue(session.isConnected()); /
/// }
//
////    @Test
////    @DisplayName("채팅 메시지 전송 테스트")
////    void sendMessage() throws Exception {
////        // given
////        Long chatRoomId = 1L;
////        ChatMessageRequest chatMessage = ChatMessageRequest.builder()
////                .chatRoomId(chatRoomId)
////                .senderEntityId(1L)
////                .content("테스트 메시지")
////                .build();
////
////        StompSession session = connectSession();
////        BlockingQueue<ChatMessageResponse> messages = new LinkedBlockingQueue<>();
////
////        StompHeaders headers = new StompHeaders();
////        headers.add("Authorization", "Bearer test-token");
////        headers.setDestination(WEBSOCKET_SEND_URL);
////        headers.setContentType(MediaType.APPLICATION_JSON);
////
////        session.subscribe(WEBSOCKET_TOPIC + chatRoomId, new StompFrameHandler() {
////            @Override
////            public Type getPayloadType(StompHeaders headers) {
////                return ChatMessageResponse.class;
////            }
////
////            @Override
////            public void handleFrame(StompHeaders headers, Object payload) {
////                messages.add((ChatMessageResponse) payload);
////            }
////        });
////
////        // when
////        session.send(headers, chatMessage);
////
////        // then
////        ChatMessageResponse response = messages.poll(5, TimeUnit.SECONDS);
////        assertNotNull(response);
////        assertEquals(chatMessage.getContent(), response.getContent());
////    }
////
////    private StompSession connectSession() throws Exception {
////        StompHeaders connectHeaders = new StompHeaders();
////        connectHeaders.add("Authorization", "Bearer test-token");
////
////        return stompClient
////                .connect(WEBSOCKET_URI.replace("{port}", String.valueOf(port)),
////                        new WebSocketHttpHeaders(),
////                        connectHeaders,
////                        new StompSessionHandlerAdapter() {
////                        })
////                .get(1, TimeUnit.SECONDS);
////    }
//}
