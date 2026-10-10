package com.meridian.commerce.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/outbox")
public class OutboxController {

    private final EmailSender sender;

    public OutboxController(EmailSender sender) {
        this.sender = sender;
    }

    @GetMapping
    public List<EmailMessage> outbox() {
        return sender.outbox();
    }
}
