package com.saea.itasset.service;

import com.saea.itasset.dto.UserDto;
import com.saea.itasset.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto login(String loginId, String password, HttpServletRequest request) {
        List<UserDto> users = userRepository.findUserInfo(loginId);
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
        }
        
        // 로그인에서는 정확히 일치하는 리스트의 첫 번째 사용자를 사용
        UserDto user = users.get(0);

        // 입력받은 패스워드와 DB 반환 패스워드가 일치하는지 확인 (DB 데이터 공백 제거 trim)
        String dbPassword = user.getPassword() != null ? user.getPassword().trim() : null;
        String inputPassword = password != null ? password.trim() : null;
        
        System.out.println("디버그 로그 - DB 조회 패스워드: [" + dbPassword + "], 입력 패스워드: [" + inputPassword + "]");

        if (dbPassword == null) {
            throw new IllegalArgumentException("저장 프로시저에서 패스워드 컬럼(PASSWORD 또는 PWD)을 찾지 못했습니다.");
        }
        if (!dbPassword.equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        // 인증 성공 시 보안상 응답(세션) 객체에서 비밀번호 제거
        user.setPassword(null);

        // 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("USER_INFO", user);
        
        return user;
    }
    
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public List<UserDto> searchUsers(String keyword) {
        // 이름이나 사번으로 검색
        return userRepository.findUserInfo(keyword);
    }
}
