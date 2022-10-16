package com.openjob.admin.adminuser;

import com.openjob.common.model.Admin;
import com.openjob.common.enums.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminWebUserRepositoryTest {

    @Autowired
    private AdminUserRepository adminUserRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin();
        admin.setPassword(passwordEncoder.encode("12345678"));
        admin.setUsername("superadmin");
        admin.setLastName("Admin");
        admin.setFirstName("Super");
        admin.setIsActive(true);
        admin.setRole(Role.SUPER_ADMIN);
        Assert.assertNotNull(adminUserRepo.save(admin));
    }
}
