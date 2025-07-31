package edu.uic.uic_shibboleth.service;

import edu.uic.uic_shibboleth.model.User;
import edu.uic.uic_shibboleth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}