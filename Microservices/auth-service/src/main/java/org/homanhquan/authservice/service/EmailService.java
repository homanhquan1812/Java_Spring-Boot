package org.homanhquan.authservice.service;

public interface EmailService {

    void sendWelcomeEmail(String email, String username);
}
