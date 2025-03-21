package com.collab.service;

import com.collab.entity.User;
import com.collab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User login(String id, String password) {
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()) {
            if(user.get().getPassword().equals(password)) {
                return user.get();
            } else {
                return null;
            }
        }
        return null;
    }
}
