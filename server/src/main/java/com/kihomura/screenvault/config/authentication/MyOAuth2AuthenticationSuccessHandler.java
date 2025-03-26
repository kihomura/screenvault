package com.kihomura.screenvault.config.authentication;

import com.alibaba.fastjson2.JSON;
import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.security.JWTTokenProvider;
import com.kihomura.screenvault.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;

    public MyOAuth2AuthenticationSuccessHandler(JWTTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String username = null;
        if (oauthToken.getAuthorizedClientRegistrationId().equalsIgnoreCase("google")) {
            username = (String) oauthUser.getAttributes().get("email");
        } else if (oauthToken.getAuthorizedClientRegistrationId().equalsIgnoreCase("github")) {
            username = (String) oauthUser.getAttributes().get("login");
        }

        // register automatically with OAuth account
        User user = userService.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            // as default
            user.setPassword("oauth2user");
            user.setNickname((String) oauthUser.getAttributes().get("name"));
            userService.registerOAuthUser(user);
        } else {
            // update user's info
            userService.updateUserFromOAuth2(user, oauthUser.getAttributes());
        }

        String token = jwtTokenProvider.generateToken(authentication);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60 * 30)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("authorities", authentication.getAuthorities());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OAuth2 Login successful");
        result.put("data", data);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
