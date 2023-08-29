package securecoding.util;

// import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import securecoding.model.Student;
// import securecoding.model.Lobby;
import securecoding.model.User;
// import securecoding.model.property.Statuses;
import securecoding.repository.StudentRepository;

@Component
public class StudentUtil {

	private static StudentRepository studentRepository;

	@Autowired
	private StudentUtil(StudentRepository PlayerRepository) {
		StudentUtil.studentRepository = PlayerRepository;
	}

	public static Student findStudent(User user) {
		return studentRepository.findByQuizAndUser(user.getQuiz(), user);
	}

	// public static void fillStatus(Player player) {
	// 	if (player.getStatus() != null)
	// 		return;
	// 	else
	// 		player.setStatus(Statuses.IN_PROGRESS);

	// 	if (player.getPoints() >= player.getChallenge().getPoints())
	// 		player.setStatus(Statuses.PERFECT);
	// 	else if (player.getPoints() >= player.getChallenge().getPoints() * 0.5)
	// 		player.setStatus(Statuses.COMPLETED);
	// 	else
	// 		player.setStatus(Statuses.FAIL);
	// }

}
