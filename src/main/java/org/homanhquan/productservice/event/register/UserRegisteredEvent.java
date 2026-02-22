package org.homanhquan.productservice.event.register;

public record UserRegisteredEvent(
        String email,
        String username
) {}
