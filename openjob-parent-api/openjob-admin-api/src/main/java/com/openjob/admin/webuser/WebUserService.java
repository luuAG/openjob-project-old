package com.openjob.admin.webuser;

import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.MailCase;
import com.openjob.common.model.MailSetting;
import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WebUserService {
    private final WebUserRepository webUserRepo;
//    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomJavaMailSender mailSender;
    private final SettingService settingService;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }

    public Optional<User> get(String id) {
        return webUserRepo.findById(id);
    }

    public void activate(String id) {
        User user = webUserRepo.getById(id);
        user.setIsActive(true);
        webUserRepo.save(user);
        // mail to user
        MailSetting mailSetting = new MailSetting(
                user.getEmail(),
                "Tài khoản của bạn đã được kích hoạt",
                settingService.getByName(MailCase.MAIL_USER_REACTIVATED.name()).orElseThrow().getValue(),
                user,
                null,
                null,
                null);
        mailSender.sendMail(mailSetting); // async
    }
    public void deactivate(String id) {
        User user = webUserRepo.getById(id);
        user.setIsActive(false);
        webUserRepo.save(user);
        // mail to user
        MailSetting mailSetting = new MailSetting(
                user.getEmail(),
                "Tài khoản của bạn đã bị vô hiệu hoá",
                settingService.getByName(MailCase.MAIL_USER_DEACTIVATED.name()).orElseThrow().getValue(),
                user,
                null,
                null,
                null);
        mailSender.sendMail(mailSetting); // async
    }

    public Page<User> searchByKeyword(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<User> pageUser = webUserRepo.searchByKeyword(keyword ,pageable);
        return pageUser;
    }
}
