package org.sergp.userservice.services;

import lombok.RequiredArgsConstructor;
import org.sergp.userservice.dto.AuthRequest;
import org.sergp.userservice.dto.LoginRequest;
import org.sergp.userservice.dto.UserDTO;
import org.sergp.userservice.exceptions.UserAlreadyExistsException;
import org.sergp.userservice.mappers.UserMapper;
import org.sergp.userservice.models.Role;
import org.sergp.userservice.models.User;
import org.sergp.userservice.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDTO register(AuthRequest authRequest) {
        if(userRepository.findByUsername(authRequest.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }

        User newUser = User.builder()
                .username(authRequest.getUsername())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .email(authRequest.getEmail())
                .role(Role.USER)
        .build();

        return UserMapper.INSTANCE.userToUserDTO(userRepository.save(newUser));
    }

    public String login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtService.generateToken((authentication.getName()));
    }
}
