package org.ats.mapper.user;

import org.ats.entities.user.Account;
import org.ats.security.request.RegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @org.mapstruct.Mapping(target = "accountId", ignore = true)
    @org.mapstruct.Mapping(target = "role", ignore = true)
    @org.mapstruct.Mapping(target = "customer", ignore = true)
    Account toAccount(RegisterRequest request);
}
