package shop.mtcoding.jwtstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.jwtstudy.config.auth.JwtProvider;
import shop.mtcoding.jwtstudy.config.auth.LoginUser;
import shop.mtcoding.jwtstudy.model.User;
import shop.mtcoding.jwtstudy.model.UserRepository;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final HttpSession session;

    @GetMapping("/user") // 인증 필요
    public ResponseEntity<?> user() {
        // 권한처리 이 사람이 이 게시글의 주인
        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser.getId() == 1) {
            return ResponseEntity.ok().body("접근 성공");
        } else {
            return new ResponseEntity<>("접근 실패", HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/") // 인증 불필요
    public ResponseEntity<?> main() {
        return ResponseEntity.ok().body("접근 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(User user) {
        Optional<User> userOP = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (userOP.isPresent()) {
            String jwt = JwtProvider.create(userOP.get());
            return ResponseEntity.ok().header(JwtProvider.HEADER, jwt).body("로그인 성공");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}