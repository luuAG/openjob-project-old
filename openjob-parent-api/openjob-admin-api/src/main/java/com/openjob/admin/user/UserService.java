package com.openjob.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService  {
    private final WebUserRepository webUserRepo;
    private final HrRepository hrRepo;
    private final BCryptPasswordEncoder passwordEncoder;




}
