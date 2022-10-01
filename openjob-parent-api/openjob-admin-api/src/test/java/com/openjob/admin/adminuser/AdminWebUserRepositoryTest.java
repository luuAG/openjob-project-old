package com.openjob.admin.adminuser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AdminWebUserRepositoryTest {

    @Mock
    private AdminUserRepository adminUserRepo;

    @Test
    public void testSearchActiveByPage(){

    }
}
