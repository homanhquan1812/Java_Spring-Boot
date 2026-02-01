package org.homanhquan.authservice.service;

import org.homanhquan.authservice.dto.logout.response.LogoutResponse;

public interface LogoutService {

    LogoutResponse logout(String authorizationHeader);
}
