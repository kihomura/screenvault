package com.kihomura.screenvault.config.authentication;

import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.security.JWTTokenProvider;
import com.kihomura.screenvault.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Value("${app.oauth2.redirect-uri}")  // in application.properties
    private String redirectUri;

    public MyOAuth2AuthenticationSuccessHandler(JWTTokenProvider jwtTokenProvider, @Lazy UserService userService) {
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

        String provider = oauthToken.getAuthorizedClientRegistrationId();
        String providerId = null;
        String username = null;

        if ("google".equalsIgnoreCase(provider)) {
            providerId = (String) oauthUser.getAttributes().get("sub");
            username = (String) oauthUser.getAttributes().get("email");
        } else if ("github".equalsIgnoreCase(provider)) {
            providerId = String.valueOf(oauthUser.getAttributes().get("id"));
            username = (String) oauthUser.getAttributes().get("login");
        }

        // check if user already exists and upadete
        User user = userService.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword("oauth2user"); // default password
            user.setNickname((String) oauthUser.getAttributes().get("name"));
            user.setProvider(provider);
            user.setProviderId(providerId);
            userService.registerOAuthUser(user);
        } else {
            // update OAuth2 users with provider info
            userService.updateUserFromOAuth2(user, oauthUser.getAttributes(), provider, providerId);
        }

        // generate JWT token and store in Cookie
        String token = jwtTokenProvider.generateToken(authentication);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // redirect to front-end
        response.sendRedirect(redirectUri);
    }

}