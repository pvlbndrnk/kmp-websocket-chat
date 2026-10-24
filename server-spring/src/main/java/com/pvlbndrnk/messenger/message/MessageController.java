package com.pvlbndrnk.messenger.message;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/messages")
    public List<MessageResponse> getMessages() {
        return messageService.getMessages();
    }
}
