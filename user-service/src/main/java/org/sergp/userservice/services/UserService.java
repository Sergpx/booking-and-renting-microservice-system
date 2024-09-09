package org.sergp.userservice.services;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.userservice.dto.UpdateUser;
import org.sergp.userservice.dto.UserDTO;
import org.sergp.userservice.exceptions.AccessDeniedException;
import org.sergp.userservice.exceptions.UserAlreadyExistsException;
import org.sergp.userservice.exceptions.UserNotFoundException;
import org.sergp.userservice.mappers.UserMapper;
import org.sergp.userservice.models.User;
import org.sergp.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getUsers() {
        return UserMapper.INSTANCE.userListToUserDTOList(userRepository.findAll());
    }

    public UserDTO getUserById(UUID id) {
        return UserMapper.INSTANCE.userToUserDTO(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    public UserDTO updateUser(UpdateUser updateUser, UUID id, HttpServletRequest request) {

        if(!id.equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("Access Denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // username update
        if (updateUser.getUsername() != null) {
            userRepository.findByUsername(updateUser.getUsername())
                    .ifPresent(error -> {throw new UserAlreadyExistsException("Username already exists");});

            user.setUsername(updateUser.getUsername());
        }
        // password update
        if (updateUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }

        // email update
        if (updateUser.getEmail() != null) {
            userRepository.findByUsername(updateUser.getUsername())
                    .ifPresent(error -> {throw new UserAlreadyExistsException("Email already used");});
            user.setEmail(updateUser.getEmail());
        }

        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return UserMapper.INSTANCE.userToUserDTO(userRepository.save(user));
    }

    public void deleteUser(UUID id, HttpServletRequest request) {
        if(!id.equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("Access Denied");
        }
        userRepository.deleteById(id);
    }

}
