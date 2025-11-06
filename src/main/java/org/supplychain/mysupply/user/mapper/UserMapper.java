package org.supplychain.mysupply.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.user.dto.UserDTO;
import org.supplychain.mysupply.user.dto.UserResponseDTO;
import org.supplychain.mysupply.user.model.User;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    User toEntity(UserDTO userDTO);

    @Mapping(target = "idUser", ignore = true)
    void updateEntityFromDTO(UserDTO userDTO, @MappingTarget User user);
}
