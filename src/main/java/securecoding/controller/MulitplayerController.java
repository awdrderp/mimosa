package securecoding.controller;

import java.util.List;

import java.time.Duration;
import java.time.LocalDateTime;
// import java.util.concurrent.TimeUnit;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.servlet.view.RedirectView;

import org.springframework.beans.factory.annotation.Autowired;

// import weilianglol.ixora.PageReader;
import securecoding.model.User;
import securecoding.model.Challenge;
import securecoding.model.Quiz;
import securecoding.model.Student;
import securecoding.repository.ChallengeRepository;
import securecoding.repository.QuizRepository;
import securecoding.repository.StudentRepository;
import securecoding.repository.UserRepository;
import securecoding.util.UserUtil;

@Controller
public class MulitplayerController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChallengeRepository challengeRepository;
	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private StudentRepository studentRepository;
	// @Autowired
	// private NoticeRepository noticeRepository;

	@RequestMapping(path = { "/multiplayer/quiz" }, method = RequestMethod.GET)
	public String getJoin() {
		return "pages/multiplayer/join-quiz";
	}

	@RequestMapping(path = { "/multiplayer/leave" }, method = RequestMethod.GET)
	public String getLeave() {
		return "pages/multiplayer/leave-quiz";
	}

	@RequestMapping(path = { "/multiplayer/create" }, method = RequestMethod.GET)
	public String getCreate() {
		return "pages/multiplayer/create-quiz";
	}

	// @RequestMapping(path = {"/quiz/games"}, method = RequestMethod.GET)
	// public String getCreatequiz() {
	// return "pages/multiplayer/games";
	// }

	@RequestMapping(path = "/multiplayer/{code}/questions", method = RequestMethod.GET)
	public String getquizQuestions(Model model, @PathVariable String code) {
		Quiz quiz = quizRepository.findByCode(code);

		model.addAttribute("quiz", quiz);
		return "pages/multiplayer/questions";
	}

	@RequestMapping(path = "/multiplayer/{code}/questions", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Challenge> postquizQuestions(@PathVariable("code") String code,
			@RequestParam("url") String url) {
		Challenge challenge = challengeRepository.findByUrl(url);
		Quiz quiz = quizRepository.findByCodeAndQuestions(code, challenge);

		if (quiz == null) {
			quiz = quizRepository.findByCode(code);
			quiz.getQuestions().add(challenge);
			challenge.getQuizzes().add(quiz);
		} else {
			challenge.getQuizzes().removeIf(b -> b.getCode() == code);
		}

		challengeRepository.saveAndFlush(challenge);
		quizRepository.saveAndFlush(quiz);
		return ResponseEntity.ok().body(challenge);
	}

	@RequestMapping(path = { "/multiplayer/quiz/join" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<User> join(@RequestParam("quizCode") String quizCode) throws Exception {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(quizCode);

		// Checks if the user is in a quiz or if the quiz code provided is valid
		if (user == null || !user.isVerified() || user.getQuiz() != null || quiz == null)
			return ResponseEntity.badRequest().body(null);

		// Checks if the quiz is disabled or is expired or their time limit has been reached
		Student student = studentRepository.findByQuizAndUser(quiz, user);
		Boolean timeLimitReached = false;
		if (student != null){
			LocalDateTime timeJoined = student.getDatetime_joined();
			Long duration = quiz.getDuration();
			LocalDateTime endTime = timeJoined.plusSeconds(duration);
			timeLimitReached = endTime.isBefore(LocalDateTime.now());
		}
		if (!quiz.isEnabled() || quiz.getExpire().isBefore(LocalDateTime.now()) || timeLimitReached)
			return ResponseEntity.badRequest().body(null);

		user.setQuiz(quiz);
		userRepository.saveAndFlush(user);

		// Check database for records
		Student newStudent = new Student(quiz, user);
		Student oStudent = studentRepository.findByQuizAndUser(newStudent.getQuiz(), newStudent.getUser());
		if (oStudent == null) {
			// Save first time joining
			studentRepository.saveAndFlush(newStudent);
		} else if (newStudent.getQuiz() != oStudent.getQuiz()) {
			// Remove outdated data
			studentRepository.delete(oStudent);
			studentRepository.saveAndFlush(newStudent);
		} 

		return ResponseEntity.ok().body(userRepository.saveAndFlush(user));
	}

	@RequestMapping(path = { "/multiplayer/quiz/create" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Quiz> create(@RequestParam("quizCode") String quizCode,
			@RequestParam("year") String year, @RequestParam("duration") String duration,
			@RequestParam("expire") String expire) throws Exception {
		User user = UserUtil.getCurrentUser();

		if (user == null || !user.isVerified() || user.getRole() == "student")
			return ResponseEntity.badRequest().body(null);

		if (quizRepository.findByCode(quizCode) == null) {
			LocalTime lt = LocalTime.parse(duration);
			Duration d = Duration.between(LocalTime.MIN, lt);
			Quiz quiz = new Quiz(quizCode, year, user, d.toSeconds(), LocalDateTime.parse(expire));
			quizRepository.saveAndFlush(quiz);
			return ResponseEntity.ok().body(quiz);
		} else {
			System.out.println("error when creating quiz code alr taken");
			return ResponseEntity.badRequest().body(null);
		}
	}

	@RequestMapping(path = { "/multiplayer/quiz/leave" }, method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<User> leave() throws Exception {
		User user = UserUtil.getCurrentUser();
		// int quizCode = user.getquiz().getCode();

		if (user == null || !user.isVerified())
			return ResponseEntity.badRequest().body(user);

		if (user.getQuiz() != null) {
			user.setQuiz(null);
			userRepository.saveAndFlush(user);
			return ResponseEntity.ok().body(user);
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@RequestMapping(path = "/multiplayer/{code}/questions/enable", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Challenge> postquizEnable(@PathVariable("code") String code) {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(code);


		if (quiz == null || !user.isVerified() || user.getRole().equals("student")) {
			return ResponseEntity.badRequest().body(null);
		} else {
			quiz.setEnabled(!quiz.isEnabled());
		}

		quizRepository.saveAndFlush(quiz);
		return ResponseEntity.ok().body(null);
	}

	@RequestMapping(path = { "/multiplayer/{code}/questions/change-code" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Quiz> changeCode(@PathVariable("code") String code,
			@RequestParam("quizCode") String quizCode) throws Exception {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(code);

		if (user == null || !user.isVerified() || user.getRole().equals("student"))
			return ResponseEntity.badRequest().body(null);

		if (quiz == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			Quiz newQuiz = new Quiz(quizCode, quiz.getYear(), quiz.getLecturer(),
			quiz.getDuration(), quiz.getExpire());
			newQuiz.setQuestions(quiz.getQuestions());
			newQuiz.setEnabled(quiz.isEnabled());
			newQuiz.setStudents(quiz.getStudents());
			newQuiz.setUsers(quiz.getUsers());
			// quiz.setCode(quizCode);
			// quizRepository.saveAndFlush(quiz);
			quizRepository.saveAndFlush(newQuiz);

			// Change user and student quiz before deleting quiz
			List<Student> students = studentRepository.findByQuiz(quiz);
			
			for (Student student : students) {
				User studentUser = student.getUser();
				studentUser.setQuiz(newQuiz);
				student.setQuiz(newQuiz);
				userRepository.saveAndFlush(studentUser);
				studentRepository.saveAndFlush(student);
			}

			quizRepository.delete(quiz);
			return ResponseEntity.ok().body(newQuiz);
			// return ResponseEntity.ok().body(quiz);
		}
	}

	@RequestMapping(path = { "/multiplayer/{code}/questions/change-year" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Quiz> changeYear(@PathVariable("code") String code,
			@RequestParam("year") String year) throws Exception {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(code);

		if (user == null || !user.isVerified() || user.getRole().equals("student"))
			return ResponseEntity.badRequest().body(null);

		if (quiz == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			quiz.setYear(year);
			quizRepository.saveAndFlush(quiz);
			return ResponseEntity.ok().body(quiz);
		}
	}

	@RequestMapping(path = { "/multiplayer/{code}/questions/change-duration" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Quiz> changeDuration(@PathVariable("code") String code,
			@RequestParam("duration") String duration) throws Exception {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(code);

		if (user == null || !user.isVerified() || user.getRole().equals("student"))
			return ResponseEntity.badRequest().body(null);

		if (quiz == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			LocalTime lt = LocalTime.parse(duration);
			Duration d = Duration.between(LocalTime.MIN, lt);
			quiz.setDuration(d.toSeconds());
			quizRepository.saveAndFlush(quiz);
			return ResponseEntity.ok().body(quiz);
		}
	}

	@RequestMapping(path = { "/multiplayer/{code}/questions/change-expire" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Quiz> changeExpire(@PathVariable("code") String code,
			@RequestParam("expire") String expire) throws Exception {
		User user = UserUtil.getCurrentUser();
		Quiz quiz = quizRepository.findByCode(code);

		if (user == null || !user.isVerified() || user.getRole().equals("student"))
			return ResponseEntity.badRequest().body(null);

		if (quiz == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			quiz.setExpire(LocalDateTime.parse(expire));
			quizRepository.saveAndFlush(quiz);
			return ResponseEntity.ok().body(quiz);
		}
	}

	// @RequestMapping(path = "/notices", method = RequestMethod.POST)
	// public @ResponseBody Notice post(HttpServletRequest request) {
	// Notice notice = new Notice(request.getParameter("title"),
	// request.getParameter("content"));
	// return noticeRepository.saveAndFlush(notice);
	// }

}
