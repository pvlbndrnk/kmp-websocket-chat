package com.pvlbndrnk.messenger.message;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<MessageResponse> getMessages() {
        return messageRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt", "id"))
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse saveMessage(String username, String text) {
        Message message = messageRepository.save(Message.create(username, text));
        return MessageResponse.from(message);
    }
}
