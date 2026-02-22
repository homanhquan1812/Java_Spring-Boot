package org.homanhquan.productservice.listener.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homanhquan.productservice.event.register.UserRegisteredEvent;
import org.homanhquan.productservice.service.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.email(), event.username());

        log.info("Email has been sent to: {}", event.email());
    }
}