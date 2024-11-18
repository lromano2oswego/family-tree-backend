package com.family_tree.familytree;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {


    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }
//
//    @GetMapping(value = "/", produces = "text/html")
//    public String landing() {
//        return "landing";
//    }
//
//    @GetMapping("/dashboard")
//    public String dashboard(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
//        if (oAuth2User == null) {
//            return "No user found";
//        }
//        model.addAttribute("userName", oAuth2User.getAttribute("name"));
//        return "dashboard";
//    }

//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

    // New API endpoint for React frontend login
    @GetMapping("/login")
    public Map<String, Object> apiLogin(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            String accessToken = userOptional.get().getAccessToken();
            String name = userOptional.get().getUsername();
            Integer id =  userOptional.get().getId();
            assert email != null;
            return Map.of(
                    "name", name,
                    "email", email,
                    "token", accessToken,
                    "id", id
            );
        } else {
            return Map.of ("error", "User not found");
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "Logged out successfully";
    }
}