package com.openjob.web.config.security.service;

import com.openjob.common.model.User;
import com.openjob.web.exception.ResourceNotFoundException;
import com.openjob.web.config.security.info.UserPrincipal;
import com.openjob.web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (optionalUser.isPresent()){
            authorities.add(new SimpleGrantedAuthority(optionalUser.get().getRole().name()));
            return new UserPrincipal(
                    optionalUser.get().getId(),
                    optionalUser.get().getEmail(),
                    optionalUser.get().getPassword(),
                    authorities
            );
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}
