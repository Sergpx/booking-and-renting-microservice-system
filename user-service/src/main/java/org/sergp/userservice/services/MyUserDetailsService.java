package org.sergp.userservice.services;

import lombok.RequiredArgsConstructor;
import org.sergp.userservice.exceptions.UserNotFoundException;
import org.sergp.userservice.models.MyUserDetails;
import org.sergp.userservice.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new MyUserDetails(userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found with username: " + username)
        ));
    }
}
