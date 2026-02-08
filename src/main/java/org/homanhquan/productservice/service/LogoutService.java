package org.homanhquan.productservice.service;

import org.homanhquan.productservice.dto.logout.response.LogoutResponse;

public interface LogoutService {

    LogoutResponse logout(String authorizationHeader);
}
