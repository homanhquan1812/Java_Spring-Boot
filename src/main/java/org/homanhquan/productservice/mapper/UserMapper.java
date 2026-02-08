package org.homanhquan.productservice.mapper;

import org.homanhquan.productservice.entity.Brand;
import org.homanhquan.productservice.entity.User;
import org.homanhquan.productservice.entity.UserInfo;
import org.homanhquan.productservice.enums.Role;
import org.homanhquan.productservice.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        imports = { Role.class, Status.class },
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Reduces boilerplate code for "Mapping Ignore"
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(Role.USER)")
    @Mapping(target = "status", expression = "java(Status.ACTIVE)")
    @Mapping(target = "userInfoId", source = "userInfo.id")
    @Mapping(target = "brandId", source = "brand.id")
    User toUsersFromUserInfoAndBrand(UserInfo userInfo, Brand brand);
}
