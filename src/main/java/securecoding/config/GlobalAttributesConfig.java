package securecoding.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import securecoding.model.Batch;
import securecoding.model.Quiz;
import securecoding.model.Challenge;
import securecoding.model.Notice;
import securecoding.model.User;
import securecoding.model.property.Roles;
import securecoding.repository.BatchRepository;
import securecoding.repository.QuizRepository;
import securecoding.repository.ChallengeRepository;
import securecoding.repository.NoticeRepository;
import securecoding.util.UserUtil;

@ControllerAdvice
public class GlobalAttributesConfig {

	@Autowired
	private NoticeRepository noticeRepository;
	@Autowired
	private ChallengeRepository challengeRepository;
	@Autowired
	private BatchRepository batchRepository;
	@Autowired
	private QuizRepository quizRepository;

	@ModelAttribute("user")
	public User getUser() {
		return UserUtil.getCurrentUser();
	}

	@ModelAttribute("notices")
	public List<Notice> getNotices() {
		return noticeRepository.findAll();
	}

	@ModelAttribute("batchesMap")
	public HashMap<String, ArrayList<Batch>> getBatches() {
		User user = UserUtil.getCurrentUser();
		HashMap<String, ArrayList<Batch>> map = new HashMap<>();

		// Not authenticated or authorised
		if (user == null || user.getRole().equals(Roles.STUDENT))
			return map;

		List<Batch> batches = new ArrayList<>();
		if (user.getRole().equals(Roles.LECTURER))
			batches = batchRepository.findByLecturerAndEnabled(user, true);
		if (user.getRole().equals(Roles.ADMIN))
			batches = batchRepository.findAllByEnabled(true);

		// Map challenges according to category
		for (Batch batch : batches) {
			String year = batch.getYear();

			if (!map.containsKey(year))
				map.put(year, new ArrayList<>());

			map.get(year).add(batch);
		}

		return map;
	}

	@ModelAttribute("quizzesMap")
	public HashMap<String, ArrayList<Quiz>> getQuizzes() {
		User user = UserUtil.getCurrentUser();
		HashMap<String, ArrayList<Quiz>> map = new HashMap<>();

		// Not authenticated or authorised
		if (user == null || user.getRole().equals(Roles.STUDENT))
			return map;

		List<Quiz> quizzes = new ArrayList<>();
		if (user.getRole().equals(Roles.LECTURER))
			quizzes = quizRepository.findByLecturer(user);
		if (user.getRole().equals(Roles.ADMIN))
			quizzes = quizRepository.findAll();

		// Map challenges according to category
		for (Quiz quiz : quizzes) {
			String year = quiz.getYear();

			if (!map.containsKey(year))
				map.put(year, new ArrayList<>());

			map.get(year).add(quiz);
		}

		return map;
	}

	@ModelAttribute("challengesMap")
	public TreeMap<String, ArrayList<Challenge>> getChallenges() {
		User user = UserUtil.getCurrentUser();
		TreeMap<String, ArrayList<Challenge>> map = new TreeMap<>();

		// Not authenticated
		if (user == null)
			return map;

		Iterable<Challenge> challenges = new ArrayList<>();
		// Student
		if (user.getRole().equals(Roles.STUDENT) && user.getBatch() != null)
			challenges = user.getBatch().getAssignments();
		// Student in multiplayer lobby
		if (user.getRole().equals(Roles.STUDENT) && user.getQuiz() != null)
			if (user.getQuiz().isEnabled() && user.getQuiz().getExpire().isAfter(LocalDateTime.now()))
				challenges = user.getQuiz().getQuestions();
		// Lecturer and Admin
		if (user.getRole().equals(Roles.LECTURER) || user.getRole().equals(Roles.ADMIN))
			challenges = challengeRepository.findAll();

		// Map challenges according to category
		for (Challenge challenge : challenges) {
			String category = challenge.getCategory();

			if (!map.containsKey(category))
				map.put(category, new ArrayList<>());

			map.get(category).add(challenge);
			map.get(category).sort((a, b) -> a.getTitle().compareTo(b.getTitle()));
		}

		return map;
	}

}
