package com.openjob.admin.adminuser;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.AdminUserNotFound;
import com.openjob.common.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminUserService extends AbstractBaseService<Admin> implements UserDetailsService {
    private final AdminUserRepository adminUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }

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

    public Page<Admin> getAllByPage(Integer pageNumber, Integer size, Boolean isActive) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        Page<Admin> pageAdmin;
        if (Objects.isNull(isActive))
            pageAdmin = adminUserRepo.searchAllByPage("", pageable);
        else
            pageAdmin = adminUserRepo.searchActiveByPage("", isActive, pageable);
        pageAdmin.getContent().forEach(admin -> admin.setPassword("đã che"));
        return pageAdmin;
    }



    public Page<Admin> searchByPage(Integer page, Integer size, String keyword, Boolean isActive) {
        if (Objects.isNull(keyword) || keyword.isEmpty())
            return getAllByPage(page, size, isActive);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Admin> pageAdmin;
        if (Objects.isNull(isActive)){
            pageAdmin = adminUserRepo.searchAllByPage(keyword, pageable);
        } else {

            pageAdmin = adminUserRepo.searchActiveByPage(keyword, isActive, pageable);
        }
        pageAdmin.getContent().forEach(admin -> admin.setPassword("đã che"));
        return pageAdmin;
    }

    @Override
    public Admin save(Admin obj) throws SQLException {
        obj.setPassword(passwordEncoder.encode(obj.getPassword()));
        try {
            return adminUserRepo.save(obj);
        } catch (Exception ex){
            throw new SQLException("Object to be saved is invalid");
        }
    }

    @Override
    public void delete(String id) {
    }

    @Override
    public void activate(String id) throws AdminUserNotFound, SQLException {
        Optional<Admin> admin = adminUserRepo.findById(id);
        if (admin.isPresent()){
            admin.get().setIsActive(true);
            save(admin.get());
        } else {
            throw new AdminUserNotFound("Admin user not found with ID: " + id);
        }
    }

    @Override
    public void deactivate(String id) throws AdminUserNotFound, SQLException {
        Optional<Admin> admin = adminUserRepo.findById(id);
        if (admin.isPresent()){
            admin.get().setIsActive(false);
            save(admin.get());
        } else {
            throw new AdminUserNotFound("Admin user not found with ID: " + id);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> user = adminUserRepo.findByUsername(username);
        if (user.isPresent()){
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.get().getRole().name()));
            return new User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    authorities);
        }
        throw new UsernameNotFoundException("Admin user not found for username: "+username);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminUserRepo.findByUsername(username);
    }
}
