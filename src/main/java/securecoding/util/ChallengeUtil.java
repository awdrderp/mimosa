package securecoding.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import securecoding.model.Batch;
import securecoding.model.Quiz;
import securecoding.model.Challenge;
import securecoding.repository.ChallengeRepository;

@Component
public class ChallengeUtil {

	private static ChallengeRepository challengeRepository;
	
	@Autowired
	private ChallengeUtil(ChallengeRepository challengeRepository) {
		ChallengeUtil.challengeRepository = challengeRepository;
	}
	
	public static Challenge getChallenge(String url, Batch batch) {
		return challengeRepository.findByUrlAndBatches(url, batch);
	}
	
	public static Challenge getChallenge(String url, Quiz lobby) {
		return challengeRepository.findByUrlAndQuizzes(url, lobby);
	}
	
}
