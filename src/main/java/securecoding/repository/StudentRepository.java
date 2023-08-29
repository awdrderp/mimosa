package securecoding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import securecoding.model.Student;
import securecoding.model.Quiz;
import securecoding.model.User;

@RepositoryRestResource(path = "students")
public interface StudentRepository extends JpaRepository<Student, Integer> {
	
	public Student findByUser(User user);
	public Student findByQuizAndUser(Quiz quiz, User user);
	public List<Student> findByQuiz(Quiz quiz);

}
