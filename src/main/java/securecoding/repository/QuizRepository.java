package securecoding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import securecoding.model.Quiz;
import securecoding.model.Challenge;
import securecoding.model.User;

@RepositoryRestResource(path = "quizzes")
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

	public Quiz findByCode(String code);
	public Quiz findByCodeAndQuestions(String code, Challenge challenge);
	public List<Quiz> findByLecturer(User user);
	public List<Quiz> findByLecturerAndEnabled(User user, boolean enabled);
	public List<Quiz> findAllByEnabled(boolean enabled);
	public List<Quiz> findAll();
	
}
