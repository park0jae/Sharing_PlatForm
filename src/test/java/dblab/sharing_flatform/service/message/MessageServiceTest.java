package dblab.sharing_flatform.service.message;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.message.Message;
import dblab.sharing_flatform.dto.message.crud.create.MessageCreateRequestDto;
import dblab.sharing_flatform.dto.message.MessageDto;
import dblab.sharing_flatform.factory.member.MemberFactory;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MemberRepository memberRepository;

    Member receiveMember;
    Member sendMember;

    @BeforeEach
    public void beforeEach(){
            receiveMember = MemberFactory.createReceiveMember();
            sendMember = MemberFactory.createSendMember();
    }

    @Test
    public void sendMessageTest() {
        // Given
        MessageCreateRequestDto messageCreateRequestDto = new MessageCreateRequestDto("sender", "HelloWorld","receiver");

        given(memberRepository.findByUsername("sender")).willReturn(Optional.of(sendMember));
        given(memberRepository.findByUsername("receiver")).willReturn(Optional.of(receiveMember));

        // When
        MessageDto result = messageService.sendMessage(messageCreateRequestDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    public void findSendMessageTest() {
        // Given

        Message message = new Message("content", receiveMember, sendMember);

        // When
        List<MessageDto> sendMessages = messageService.findSendMessage(message.getSendMember().getUsername());

        // Then
        assertThat(sendMessages).isNotNull();
    }

    @Test
    public void findReceiveMessageTest(){
        // Given

        Message message = new Message("content", receiveMember, sendMember);

        // When
        List<MessageDto> receiveMessages = messageService.findReceiveMessage(message.getSendMember().getUsername());

        // Then
        assertThat(receiveMessages).isNotNull();
    }

    @Test
    public void findMessageMemberToMemberTest(){
        // Given
        given(memberRepository.findByUsername(receiveMember.getUsername())).willReturn(Optional.of(receiveMember));
        given(messageRepository.findAllBySendAndReceiverMembers(sendMember.getUsername(), receiveMember.getUsername())).willReturn(List.of());

        // When
        List<MessageDto> result = messageService.findMessageMemberToMember(sendMember.getUsername(), receiveMember.getUsername());

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    public void deleteMessageBySender(){
        //Given
        Message message = new Message("content", receiveMember, sendMember);

        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));

        // When
        messageService.deleteMessageBySender(receiveMember.getId());

        // Then
        assertThat(message.isDeleteBySender()).isTrue();
    }

    @Test
    public void deleteMessageByReceiver(){
        //Given
        Message message = new Message("content", receiveMember, sendMember);

        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));

        // When
        messageService.deleteMessageByReceiver(sendMember.getId());

        // Then
        assertThat(message.isDeleteByReceiver()).isTrue();
    }

}
