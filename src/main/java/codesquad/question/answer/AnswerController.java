package codesquad.question.answer;

import codesquad.config.HttpSessionUtils;
import codesquad.question.QuestionNotFoundException;
import codesquad.question.QuestionRepository;
import codesquad.question.Result;
import codesquad.user.User;
import codesquad.user.UserNotFoundException;
import codesquad.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping("")
    public String isNotLogin(HttpSession session, Model model) {
        model.addAttribute("errorMessage", "로그인이 필요합니다.");
        return "user/login";
    }

    @PostMapping("/{id}")
    public String post(@PathVariable long questionId, @PathVariable long id, HttpSession session, String comment, Model model) {
        Result result = valid(session);
        if(!result.isValid()) {
            model.addAttribute("errorMessage", result.getErrorMessage());
            return "user/login";
        }
        Answer answer = new Answer(
                questionRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException("해당 질문을 찾을 수 없습니다.")),
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다.")),
                comment,
                false
        );
        answerRepository.save(answer);
        return String.format("redirect:/questions/%s", questionId);
    }

    @DeleteMapping("/{answerId}")
    public String delete(@PathVariable long questionId, @PathVariable long answerId, HttpSession session, Model model) {
        Answer answer = answerRepository.findById(answerId).orElse(null);
        Result result = valid(session, answer);
        if (!result.isValid()) {
            model.addAttribute("errorMessage", result.getErrorMessage());
            return "user/login";
        }
        answer.deleted();
        answerRepository.save(answer);
        return String.format("redirect:/questions/%s", questionId);
    }

    private Result valid(HttpSession session) {
        if (!HttpSessionUtils.isLogin(session)) {
            return Result.fail("로그인이 필요합니다.");
        }
        return Result.ok();
    }

    private Result valid(HttpSession session, Answer answer) {
        if (!HttpSessionUtils.isLogin(session)) {
            return Result.fail("로그인이 필요합니다.");
        }
        User sessionedUser = HttpSessionUtils.getUserFromSession(session);
        if (!answer.isSameUser(sessionedUser)) {
            return Result.fail("다른 사람의 글을 수정 또는 삭제할 수 없습니다.");
        }
        return Result.ok();
    }
}
