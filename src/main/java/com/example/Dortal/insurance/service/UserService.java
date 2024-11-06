package com.example.Dortal.insurance.service;

import com.example.Dortal.insurance.entity.User;
import com.example.Dortal.insurance.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
