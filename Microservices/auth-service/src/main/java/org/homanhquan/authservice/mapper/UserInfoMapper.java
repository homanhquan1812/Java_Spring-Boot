package org.homanhquan.authservice.mapper;

import org.homanhquan.authservice.dto.admins.request.UpdateUserInfoRequestForAdmins;
import org.homanhquan.authservice.dto.register.request.UserRegisterRequest;
import org.homanhquan.authservice.dto.userInfo.request.UpdateUserInfoRequestByUsers;
import org.homanhquan.authservice.dto.userInfo.response.UserInfoResponseForUsers;
import org.homanhquan.authservice.dto.users.response.UsersResponseForAdmins;
import org.homanhquan.authservice.entity.UserInfo;
import org.homanhquan.authservice.enums.Role;
import org.homanhquan.authservice.enums.Status;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        imports = { Role.class, Status.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Reduces boilerplate code for "Mapping Ignore"
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserInfoMapper {
    // Entity -> DTO
    UserInfoResponseForUsers toDtoInfoForUsers(UserInfo userInfo);
    UsersResponseForAdmins toDtoInfoForAdmins(UserInfo userInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserInfo fromUserRegisterRequest(UserRegisterRequest userRegisterRequest);

    void updateEntityFromDtoInfoForUsers(UpdateUserInfoRequestByUsers updateUserInfoRequestByUsers, @MappingTarget UserInfo userInfo);
    void updateEntityFromDtoInfoForAdmins(UpdateUserInfoRequestForAdmins updateUserInfoRequestForAdmins, @MappingTarget UserInfo userInfo);
}
