package org.homanhquan.authservice.mapper;

import org.homanhquan.authservice.dto.admins.request.UpdateUsersStatusRequestForAdmins;
import org.homanhquan.authservice.projection.UsersProjection;
import org.homanhquan.authservice.dto.users.response.UsersResponseForAdmins;
import org.homanhquan.authservice.dto.users.response.UsersResponseForUsers;
import org.homanhquan.authservice.entity.Brand;
import org.homanhquan.authservice.entity.UserInfo;
import org.homanhquan.authservice.entity.Users;
import org.homanhquan.authservice.enums.Role;
import org.homanhquan.authservice.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        imports = { Role.class, Status.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface UsersMapper {
    // Entity -> DTO
    UsersResponseForAdmins projectionToDtoStatusForAdmins(Users users);

    // Projection -> DTO
    UsersResponseForUsers projectionToDto(UsersProjection usersProjection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.USER)")
    @Mapping(target = "status", expression = "java(Status.ACTIVE)")
    @Mapping(target = "userInfoId", source = "userInfo.id")
    @Mapping(target = "brandId", source = "brand.id")
    Users toUsersFromUserInfoAndBrand(UserInfo userInfo, Brand brand);

    // DTO -> Entity (Update)
    //void updateEntityFromDto(UpdateUsersStatusRequest updateUsersStatusRequest, @MappingTarget Users users);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDtoStatusForAdmins(UpdateUsersStatusRequestForAdmins updateUsersStatusRequestForAdmins, @MappingTarget Users users);
}
