package org.sergp.userservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sergp.userservice.dto.UpdateUser;
import org.sergp.userservice.dto.UserDTO;
import org.sergp.userservice.models.User;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);
    List<UserDTO> userListToUserDTOList(List<User> users);

    UpdateUser userToUserUpdate(User user);


}
