package codesquad.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public String createUser(User user, Model model) {
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    @GetMapping("/{id}")
    public String showProfile(@PathVariable Long id, Model model) throws IdNotFoundException {
        User user = getUser(id);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/{id}/form")
    public String showUserInfo(@PathVariable Long id, Model model) throws IdNotFoundException {
        User user = getUser(id);
        model.addAttribute("user", user);
        return "user/updateForm";
    }

    @PutMapping("/{id}")
    public String updateUserInfo(@PathVariable Long id, User userUpdated) throws IdNotFoundException {
        User user = getUser(id);
        user.update(userUpdated);
        userRepository.save(user);
        return "redirect:/users";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {  // 전달하는 인자가 많지않을떄는 User 대신 이렇게 하나씩 받을 수 있음
        Optional<User> maybeUser = userRepository.findByUserId(userId);  // findByUserId 만들어야 할 필요성!
        if (maybeUser.isPresent() && maybeUser.get().matchPassword(password)) {  // session을 통해 로그인한 정보를 클라이언트에게 보냄
            User user = maybeUser.get();
            session.setAttribute("loginUser", user);  // 서버가 세션 아이디 기억함, 톰캣 서버상에 파일로 저장
            return "redirect:/";
        }
        throw new LoginErrorException("아이디 또는 비밀번호가 일치하지 않습니다");
    }

    private User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("해당 id를 찾을 수 없습니다"));
    }

}
