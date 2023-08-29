package securecoding.controller.challenges.general;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import securecoding.controller.template.ChallengeController;
import securecoding.controller.template.ChallengeControllerAdapter;
import securecoding.model.Attempt;

@ChallengeController("/challenges/overflow-basics")
public class OverflowBasicsChallengeController extends ChallengeControllerAdapter {

	@Override
	public void unitTest(Attempt attempt, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = Optional.ofNullable(request.getParameter("ans")).orElse("");

		String password = "didUFindAllOfMe";
		String b64Password = "ZGlkVUZpbmRBbGxPZk1l";
		String junk = "@4l\n # code : " + b64Password;
		String reply = "Incorrect passphrase!";

		// Correct password
		if (code.equals(password)) {
			attempt.setPoints(15);
			reply = "You found the entire Hidden value!";
		} else if (code.length() > 15) {
			attempt.addPoints(5);

			// Iterate through the junk per excess character
			for (int i = 0; i < code.substring(15).length()
					&& i < junk.length(); i++) {
				attempt.getData().put("i", i + " " + junk.charAt(i));
				if ((i - 4) % 5 == 0 && i > 15)
					reply = reply.substring(0, 35);
				reply += junk.charAt(i);
			}
		}

		attempt.getData().put("reply", reply);
		attempt.getData().put("code", code);
	}

}
