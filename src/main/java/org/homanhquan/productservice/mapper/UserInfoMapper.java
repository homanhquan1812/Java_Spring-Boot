package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        imports = { Role.class, Status.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Reduces boilerplate code for "Mapping Ignore"
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserInfo fromUserRegisterRequest(UserRegisterRequest userRegisterRequest);
}
