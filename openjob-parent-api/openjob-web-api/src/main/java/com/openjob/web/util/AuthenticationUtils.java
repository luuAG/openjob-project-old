package com.openjob.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.model.User;
import com.openjob.web.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticationUtils {
    private final UserService userService;

    public User getLoggedInUser(HttpServletRequest request) throws IOException {
        String accessToken = request.getHeader("authorization");
        User loggedInUser=null;
        if (Objects.nonNull(accessToken)){
            String payloadJWT = accessToken.split("\\.")[1];
            Base64 base64 = new Base64(true);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> info = mapper.readValue(base64.decode(payloadJWT), Map.class);
            String email = info.get("sub");
            loggedInUser = userService.getByEmail(email);
        }
        return loggedInUser;
    }
}
