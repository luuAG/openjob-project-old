package com.openjob.admin.adminuser;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.AdminUserNotFound;
import com.openjob.common.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminUserService extends AbstractBaseService<Admin> {
    private final Integer NUMBER_OF_ITEM_IN_ONE_PAGE = 10;
    private final AdminUserRepository adminUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Optional<Admin> get(String id) throws IllegalArgumentException {
        Optional<Admin> admin = adminUserRepo.findById(id);
        if (admin.isPresent())
            admin.get().setPassword("hidden-for-security");
        return admin;
    }

    @Override
    public Collection<Admin> getAll() {
        return null;
    }

    public Collection<Admin> getAllByPage(Integer pageNumber, Boolean isActive) {
        Pageable pageable = PageRequest.of(pageNumber - 1, this.NUMBER_OF_ITEM_IN_ONE_PAGE);
        List<Admin> listAdmin;
        if (Objects.isNull(isActive))
            listAdmin = adminUserRepo.searchAllByPage("", pageable);
        else
            listAdmin = adminUserRepo.searchActiveByPage("", isActive, pageable);

        listAdmin.forEach(admin -> admin.setPassword("hidden-for-security"));
        return listAdmin;
    }



    public Collection<Admin> searchByPage(String keyword, Integer pageNumber, Boolean isActive) {
        if (Objects.isNull(keyword) || keyword.isEmpty())
            return getAllByPage(pageNumber, isActive);
        Pageable pageable = PageRequest.of(pageNumber - 1, this.NUMBER_OF_ITEM_IN_ONE_PAGE);
        List<Admin> listAdmin;
        if (Objects.isNull(isActive)){
            listAdmin = adminUserRepo.searchAllByPage(keyword, pageable);
        } else {

            listAdmin = adminUserRepo.searchActiveByPage(keyword, isActive, pageable);
        }
        listAdmin.forEach(admin -> admin.setPassword("hidden-for-security"));
        return listAdmin;
    }

    @Override
    public Admin save(Admin obj) {
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));
        return adminUserRepo.save(obj);
    }

    @Override
    public void delete(String id) {
    }

    @Override
    public void activate(String id) throws AdminUserNotFound {
        Optional<Admin> admin = adminUserRepo.findById(id);
        if (admin.isPresent()){
            admin.get().setIsActive(true);
            save(admin.get());
        } else {
            throw new AdminUserNotFound("Admin user not found with ID: " + id);
        }
    }

    @Override
    public void deactivate(String id) throws AdminUserNotFound {
        Optional<Admin> admin = adminUserRepo.findById(id);
        if (admin.isPresent()){
            admin.get().setIsActive(false);
            save(admin.get());
        } else {
            throw new AdminUserNotFound("Admin user not found with ID: " + id);
        }
    }


}
