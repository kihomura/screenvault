package com.kihomura.screenvault.config.authentication;

import com.alibaba.fastjson2.JSON;
import com.kihomura.screenvault.security.JWTTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTTokenProvider jwtTokenProvider;

    public MyAuthenticationSuccessHandler(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

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
        data.put("username", userDetails.getUsername());
        data.put("authorities", userDetails.getAuthorities());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Login successful");
        result.put("data", data);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}