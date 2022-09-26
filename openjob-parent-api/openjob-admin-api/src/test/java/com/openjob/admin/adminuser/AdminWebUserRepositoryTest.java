package com.openjob.admin.adminuser;

import com.openjob.common.model.Admin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AdminWebUserRepositoryTest {

    @Mock
    private AdminUserRepository adminUserRepo;

    @Test
    public void testSearchActiveByPage(){
        Pageable pageable = PageRequest.of(0, 5);
        List<Admin> listAdmin = adminUserRepo.searchActiveByPage("n", null, pageable);
        Assert.notNull(listAdmin, "Has element");
    }
}
