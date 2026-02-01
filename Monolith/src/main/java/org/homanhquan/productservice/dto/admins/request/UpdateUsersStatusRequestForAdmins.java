package org.homanhquan.productservice.dto.admins.request;

import org.homanhquan.productservice.enums.Status;

public record UpdateUsersStatusRequestForAdmins(
        Status status
) {
}
