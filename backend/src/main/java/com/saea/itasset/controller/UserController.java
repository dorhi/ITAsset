package com.saea.itasset.controller;

import com.saea.itasset.dto.UserDto;
import com.saea.itasset.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            String loginId = body.get("id");
            String password = body.get("password");
            
            // 패스워드 검증을 위해 Service 단에 비밀번호도 전달
            UserDto user = userService.login(loginId, password, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("USER_INFO") != null) {
            return ResponseEntity.ok(session.getAttribute("USER_INFO"));
        }
        return ResponseEntity.status(401).body(Map.of("message", "세션이 만료되었습니다."));
    }

    @GetMapping("/info")
    public ResponseEntity<?> searchUsers(@RequestParam("keyword") String keyword) {
        List<UserDto> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }
}
