package edu.uic.shibboleth.service;

import org.springframework.stereotype.Service;

import edu.uic.shibboleth.model.User;
import edu.uic.shibboleth.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}