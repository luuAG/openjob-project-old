package com.openjob.admin.adminuser;

import com.openjob.common.model.Admin;
import com.openjob.common.model.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminWebUserRepositoryTest {

    @Autowired
    private AdminUserRepository adminUserRepo;

    @Test
    public void testInsertAdmin(){
        Admin admin = new Admin();
        admin.setPassword("12345678");
        admin.setUsername("admin");
        admin.setLastName("Admin");
        admin.setFirstName("Normal");
        admin.setIsActive(true);
        admin.setRole(Role.ADMIN);
        Assert.assertNotNull(adminUserRepo.save(admin));
    }
}
