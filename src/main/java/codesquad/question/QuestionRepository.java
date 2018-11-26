package codesquad.question;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface QuestionRepository extends CrudRepository<Question, Long> {
    public List<Question> findByDeleted(boolean deleted);
}
