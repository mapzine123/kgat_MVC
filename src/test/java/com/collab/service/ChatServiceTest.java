//package com.kgat.service;
//
//import com.kgat.entity.ChatMessage;
//import com.kgat.entity.ChatRoom;
//import com.kgat.entity.ChatRoomUser;
//import com.kgat.entity.User;
//import com.kgat.exception.ChatRoomNotFoundException;
//import com.kgat.exception.NotChatRoomParticipantException;
//import com.kgat.repository.ChatMessageRepository;
//import com.kgat.repository.ChatRoomRepository;
//import com.kgat.repository.ChatRoomUserRepository;
//import com.kgat.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ChatServiceTest {
//
//    @Mock
//    private ChatRoomRepository chatRoomRepository;
//
//    @Mock
//    private ChatRoomUserRepository chatRoomUserRepository;
//
//    @Mock
//    private ChatMessageRepository chatMessageRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private ChatService chatService;
//
//    // 테스트에서 공통으로 사용할 객체들
//    private User testUser;
//    private ChatRoom testRoom;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트에서 공통으로 사용할 객체 초기화
//        testUser = new User("user1");
//        testRoom = ChatRoom.create();
//
//        ChatRoomUser chatRoomUser = ChatRoomUser.create(testRoom, testUser);
//        testRoom.addUser(chatRoomUser);
//
//    }
//
//    @Test
//    @DisplayName("자신과 선택한 인원이 채팅방에 접속된다.")
//    void createChatRoomByInvitationTest() {
//        // given : 초대하는 사용자와 초대받는 사용자 설정
//        User inviter = new User("user1");
//        User[] invitees = {
//                new User("user2"),
//                new User("user3"),
//                new User("user4"),
//        };
//
//        // when : 채팅방 생성 요청
//        ChatRoom chatRoom = chatService.intiveToChat(inviter, invitees, "채널1");
//
//        // then : 채팅방이 정상적으로 생성되었는지 검증
//        assertNotNull(chatRoom, "채팅방이 생성되어야 함");
//        assertNotNull(chatRoom.getId(), "채팅방 id가 생성되어야함");
//
//        // then : 채팅방에 두 사용자가 정상적으로 등록되었는지 검증
//        assertEquals(4, chatRoom.getUsers().size(), "채팅방에 4명의 사용자가 있어야 함");
//    }
//
//    @Test
//    @DisplayName("채팅방에서 메세지를 전송할 수 있다.")
//    void sendMessageToChatRoomTest() {
//        // given : testUser, testRoom
//        String content = "안녕하세요";
//
//        // mock 설정
//        /*
//        * Mockito 프레임워크를 사용한 Test Double 설정
//        * when() : ~할 때 라는 상황을 설정
//        * chatRoomRepository.findById(any())
//        *   chatRoomRepository의 findById 메서드가 호출될 때
//        *   any() : 어떤 인자가 들어와도 적용된다는 의미
//        * thenReturn(Optional.of(chatRoom))
//        *   그러면 Optional.of(chatRoom)을 반환
//        *   실제 DB 조회 대신 미리 준비한 chatRoom객체를 Optional로 감싸서 반환
//        *
//        * 장점
//        * - DB에 의존하지 않아 테스트의 독립성 보장
//        * - 실제 DB 조회보다 빨라 테스트 속도 향상
//        * - DB 에러 상황 등 특정 상황 테스트 용이
//        */
//        when(chatRoomRepository.findById(any()))
//                .thenReturn(Optional.of(testRoom));
//
//        when(chatMessageRepository.save(any(ChatMessage.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        when(userRepository.findById(testUser.getId()))
//                .thenReturn(Optional.of(testUser));
//
//        // when
//        ChatMessage message = chatService.sendMessage(testRoom.getId(), testUser.getId(), content);
//
//        // then : 메시지가 정상적으로 저장되었는지 검증
//        assertNotNull(message, "메시지가 생성되어야함");
//        assertEquals(content, message.getContent(), "메시지 내용이 일치해야함");
//        assertEquals(testUser, message.getSender(), "발신자가 일치해야함");
//        assertEquals(testRoom, message.getChatRoom(), "채팅방이 일치해야함");
//        assertNotNull(message.getSentAt(), "전송 시간이 설정되어야함");
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 채팅방에는 메시지를 보낼 수 없다.")
//    void cannotSendmessageToNonExistentRoomTest() {
//        // given : 존재하지 않는 채팅방 Id, 사용자, 메시지
//        User testUser = new User("user1");
//        String nonExistentRoomId = "abc";
//        String content = "테스트 메시지";
//
//        // mock 설정
//        when(chatRoomRepository.findById(nonExistentRoomId))
//                .thenReturn(Optional.empty());
//
//        when(userRepository.findById(testUser.getId()))
//                .thenReturn(Optional.of(testUser));
//
//        // when & then : 존재하지 않는 채팅방에 메시지 전송 시도 시 예외 발생
//        assertThrows(ChatRoomNotFoundException.class, () -> {
//            chatService.sendMessage(nonExistentRoomId, testUser.getId(), content);
//        });
//    }
//
//    @Test
//    @DisplayName("채팅방 참여자만 메시지를 보낼 수 있다.")
//    void onlyParticipantsCanSendMessageTest() {
//        // given : 채팅방과 미참여 사용자 준비
//        User testUser = new User("user3");
//        ChatRoom chatRoom = ChatRoom.create();
//        String content = "테스트 메시지";
//
//        // mock 설정
//        when(chatRoomRepository.findById(any()))
//                .thenReturn(Optional.of(chatRoom));
//
//        when(userRepository.findById(testUser.getId()))
//                .thenReturn(Optional.of(testUser));
//
//        // when & then : 미참여자가 메시지 전송 시 예외 발생
//        assertThrows(NotChatRoomParticipantException.class, () -> {
//            chatService.sendMessage(chatRoom.getId(), testUser.getId(), content);
//        });
//    }
//}
