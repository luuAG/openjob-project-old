package com.openjob.web.user;


import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> get(String id) {
        return userRepo.findById(id);
    }

    public User saveWithPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User saveWithoutPassword(User user) {
        return userRepo.save(user);
    }
}
