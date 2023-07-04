package com.openjob.web.config.security.service;

import com.openjob.common.enums.AuthProvider;
import com.openjob.common.enums.Role;
import com.openjob.common.model.Company;
import com.openjob.common.model.User;
import com.openjob.web.company.CompanyRepository;
import com.openjob.web.config.security.info.UserPrincipal;
import com.openjob.web.exception.OAuth2AuthenticationProcessingException;
import com.openjob.web.config.security.info.OAuth2UserInfo;
import com.openjob.web.config.security.info.OAuth2UserInfoFactory;
import com.openjob.web.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) throws Exception {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getIsActive())
                throw new Exception("ACCOUNT_DISABLED");
            if (user.getRole().equals(Role.HR)){
                Company company = companyRepo.findByHeadHunterId(user.getId());
                if (!company.getIsActive())
                    throw new Exception("ACCOUNT_DISABLED");
            }
//            if(!user.getAuthProvider().name()
//                    .equalsIgnoreCase(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
//                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
//                        user.getAuthProvider().name() + " account. Please use your " + user.getAuthProvider().name() +
//                        " account to login.");
//            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        String[] name = oAuth2UserInfo.getName().split(" ");

        User user = new User();
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setPassword("");
        user.setAuthProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setFirstName(name[0]);
        user.setLastName(oAuth2UserInfo.getName().replace(name[0], "").trim());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        String[] name = oAuth2UserInfo.getName().split(" ");
        if (StringUtils.isEmpty(existingUser.getFirstName()) || StringUtils.isEmpty(existingUser.getLastName())){
            existingUser.setFirstName(name[0]);
            existingUser.setLastName(oAuth2UserInfo.getName().replace(name[0], "").trim());
        }
        if (StringUtils.isEmpty(existingUser.getAvatarUrl()))
            existingUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setAuthProvider(AuthProvider.GOOGLE);
        return userRepository.save(existingUser);
    }
}
