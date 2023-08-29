package securecoding.controller.template;

import java.time.LocalDateTime;
import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import securecoding.model.Attempt;
import securecoding.model.Challenge;
import securecoding.model.Hint;
import securecoding.model.Message;
import securecoding.model.User;
import securecoding.model.property.Roles;
import securecoding.repository.AttemptRepository;
import securecoding.repository.ChallengeRepository;
import securecoding.repository.HintRepository;
import securecoding.util.AttemptUtil;
import securecoding.util.HintUtil;
import securecoding.util.StudentUtil;
import securecoding.util.PropertiesUtil;
import securecoding.util.UserUtil;
import weilianglol.ixora.PageReader;
import weilianglol.mimosa.java.exception.CompilationException;
import weilianglol.mimosa.node.exception.ExecutionException;

@Controller
public abstract class ChallengeControllerAdapter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ChallengeController annotation;
	private String url;

	protected ChallengeRepository challengeRepository;
	protected AttemptRepository attemptRepository;
	protected HintRepository hintRepository;

	@Autowired
	private void init(ChallengeRepository challengeRepository, AttemptRepository attemptRepository,
			HintRepository hintRepository) throws Exception {
		this.challengeRepository = challengeRepository;
		this.attemptRepository = attemptRepository;
		this.hintRepository = hintRepository;

		// Grab common annotations
		this.annotation = this.getClass().getAnnotation(ChallengeController.class);
		this.url = annotation.value();

		initChallenge();
		initHints();

		// Verbose loading
		if (PropertiesUtil.getProperty("securecoding.challenges.verbose-load").equals("true"))
			logger.info("Challenge: " + url + " successfully loaded");
	}

	private void initChallenge() throws Exception {
		// Collect Challenge
		Challenge challenge = challengeRepository.findByUrl(url);

		// Gather resources
		PageReader pageReader = new PageReader("templates/pages" + url + ".html");
		// Gather info
		String title = pageReader.getFragment("title").text();
		String description = pageReader.getFragment("description").text();
		int points = Integer.parseInt(pageReader.getFragment("points").text());
		String category = pageReader.getFragment("category").text();
		String difficulty = pageReader.getFragment("difficulty").text();

		if (challenge != null) {
			// Update challenge
			challenge.setTitle(title);
			challenge.setDescription(description);
			challenge.setPoints(points);
			challenge.setDifficulty(difficulty);
			challenge.setCategory(category);
		} else {
			// Set Up Challenge
			challenge = new Challenge(url, title, description, points, difficulty, category);
		}

		// Save Challenge
		challengeRepository.saveAndFlush(challenge);
	}

	private void initHints() throws Exception {
		// Collect Challenge
		this.url = this.getClass().getAnnotation(ChallengeController.class).value();
		Challenge challenge = challengeRepository.findByUrl(url);

		// Gather resources
		PageReader pageReader = new PageReader("templates/pages" + url + ".html");
		// Grab hints
		if (pageReader.hasFragment("hints")) {
			for (Element hintFragment : pageReader.getFragment("hints").children()) {
				// Gather hint info
				String hintDescription = hintFragment.getElementsByClass("hint").get(0).text();
				int cost = Integer.parseInt(hintFragment.getElementsByClass("cost").get(0).text());
				int priority = Integer.parseInt(hintFragment.getElementsByClass("priority").get(0).text());

				Hint hint = hintRepository.findByUrl(url + "/" + priority);
				if (hint != null) {
					// Update Hint
					hint.setHint(hintDescription);
					hint.setCost(cost);
				} else {
					// Set Up Hint
					hint = new Hint(hintDescription, cost, priority, challenge);
				}

				// Save Hint
				hintRepository.saveAndFlush(hint);
			}
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String doGet(HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtil.getCurrentUser();
		Challenge challenge = challengeRepository.findByUrl(url);
		Attempt attempt = attemptRepository.findByUserAndChallenge(user, challenge);

		if (user.getRole().equals(Roles.STUDENT) && challengeRepository.findByUrlAndBatches_students(url, user) == null
				&& !user.getQuiz().getQuestions().contains(challengeRepository.findByUrl(url)))
			return "pages/error";

		model.addAttribute("attempt", attempt);
		model.addAttribute("challenge", challenge);
		return "pages" + url;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> doPost(HttpServletRequest request, HttpServletResponse response) {
		User user = UserUtil.getCurrentUser();
		Challenge challenge = challengeRepository.findByUrl(url);
		Attempt attempt = new Attempt(user, challenge);

		if (user.getRole().equals(Roles.STUDENT) && challengeRepository.findByUrlAndBatches_students(url, user) == null
				&& !user.getQuiz().getQuestions().contains(challengeRepository.findByUrl(url)))
			return ResponseEntity.badRequest().body(null);

		// Checks if the lobby(test) has been expired or if the duration of the test/lobby is over(the student has ran out of time to do the test)
		if (user.getQuiz() != null && (user.getQuiz().getExpire().isBefore(LocalDateTime.now()) || 
			StudentUtil.findStudent(user).getDatetime_joined().plus(Duration.ofSeconds(user.getQuiz().getDuration())).isBefore(LocalDateTime.now()))) {
			System.out.println("Lobby expire");
			Message message = new Message();
			message.setType("Redirect");
			return ResponseEntity.badRequest().body(message);
		}

		try {
			// Grade and update
			unitTest(attempt, request, response);
			AttemptUtil.updateAttempt(attempt, annotation.overrideSubmission());
		} catch (CompilationException | ExecutionException e) {
			// Append to page
			Message message = new Message();
			message.setException(e);
			message.setType("Compilation");
			return ResponseEntity.badRequest().body(message);
		} catch (Exception e) {
			// Safe exit regardless
			logger.error("Challenge: " + url + " has encountered an error!", e);
			return ResponseEntity.badRequest().body(null);
		}

		return ResponseEntity.ok().body(attempt);
	}

	@RequestMapping(path = "/hint", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Hint> getHint(HttpServletRequest request) {
		User user = UserUtil.getCurrentUser();
		Challenge challenge = challengeRepository.findByUrl(url);
		Hint hint = HintUtil.getNextLockedHint(challenge, user);

		// Allowing negative scoring
		if (hint != null /* && user.getPoints() >= hint.getCost() */) {
			hint.getUsers().add(user);
			hintRepository.saveAndFlush(hint);

			return ResponseEntity.ok().body(hint);
		} else {
			// Invalid hint request
			return ResponseEntity.badRequest().body(null);
		}
	}

	public abstract void unitTest(Attempt attempt, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
