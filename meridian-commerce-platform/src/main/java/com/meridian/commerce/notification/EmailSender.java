package com.meridian.commerce.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Dev stand-in for the SES client. Keeps an outbox so QA can inspect what would be sent. */
@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final List<EmailMessage> outbox = Collections.synchronizedList(new ArrayList<>());

    public void send(EmailMessage message) {
        outbox.add(message);
        log.info("[email] to={} subject={}", message.to(), message.subject());
    }

    public List<EmailMessage> outbox() {
        return outbox;
    }
}
