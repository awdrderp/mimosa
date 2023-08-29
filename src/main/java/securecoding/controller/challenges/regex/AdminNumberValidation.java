package securecoding.controller.challenges.regex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueMap;

import securecoding.controller.template.ChallengeController;
import securecoding.controller.template.ChallengeControllerAdapter;
import securecoding.model.Attempt;
import weilianglol.mimosa.node.compiler.NodeCompiler;

@ChallengeController("/challenges/admin-number-validation")
public class AdminNumberValidation extends ChallengeControllerAdapter {

	@Override
	public void unitTest(Attempt attempt, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = request.getParameter("code");
		System.out.println("here in admin number validation controller");

		// To ensure that the student does not try to import any module other than the
		// 'express' required
		if (!(code.replaceAll("require[(]\'express\'[)]", "").contains("require("))) {
			attempt.setSubmission(code);

			//Correct Answer: ^[p|P]?(2[2-9]|[3-9]\d)\d{5}$
 			// Initialize Hashtable to store test cases and scores
			HashMap<Map<String, String>, Boolean> testCases = new HashMap<Map<String, String>, Boolean>();
			// Add test cases and scores here

			//Condition1: Checks whether regex checks for number to start with P or p or neither (P|p)? 
			testCases.put(Collections.singletonMap("", "Condition1"), false);
			testCases.put(Collections.singletonMap("P2223456", "Condition1"),true);
			testCases.put(Collections.singletonMap("p2523456", "Condition1"),true);
			testCases.put(Collections.singletonMap("2323456", "Condition1"),true);
			testCases.put(Collections.singletonMap("L7123456", "Condition1"),false);
			//Condition2: Checks whether regex checks for number's length ([0-9]{7})
			testCases.put(Collections.singletonMap("", "Condition2"), false);
			testCases.put(Collections.singletonMap("P2223456", "Condition2"),true);
			testCases.put(Collections.singletonMap("P252345", "Condition2"), false);
			testCases.put(Collections.singletonMap("232345", "Condition2"), false);
			testCases.put(Collections.singletonMap("P302345679", "Condition2"), false);
			//Condition3: Checks whether regex checks for letters, spaces and symbols
			testCases.put(Collections.singletonMap("", "Condition3"), false);
			testCases.put(Collections.singletonMap("P3623456", "Condition3"),true);
			testCases.put(Collections.singletonMap("P3123 56", "Condition3"), false);
			testCases.put(Collections.singletonMap("P5123abc", "Condition3"), false);
			testCases.put(Collections.singletonMap("P812345_", "Condition3"), false);	//incase they use \w
            //Condition4: Checks for first 2 numbers atleast 22
			testCases.put(Collections.singletonMap("", "Condition4"), false);
            testCases.put(Collections.singletonMap("p2269420", "Condition4"),true);
			testCases.put(Collections.singletonMap("p2138802", "Condition4"),false);
			testCases.put(Collections.singletonMap("P2707440", "Condition4"),true);
			testCases.put(Collections.singletonMap("p1924832", "Condition4"),false);
			testCases.put(Collections.singletonMap("P4011988", "Condition4"),true);

			HashMap<String, Integer> conditions = new HashMap<String, Integer>();
			conditions.put("Condition1",2);
			conditions.put("Condition2",2);
			conditions.put("Condition3",1);
			conditions.put("Condition4",5);

			try (NodeCompiler compiler = new NodeCompiler(attempt.getUser().getUsername())) {
				compiler.load("nhwr.js", code);
				compiler.load("grader.js", getTest(testCases));
				V8ValueMap map = compiler.execute("grader.js");
				map.forEach((V8ValueString key, V8ValueBoolean value) -> {
					if (value.toPrimitive()==true){
						attempt.addPoints(conditions.get(key.toPrimitive()));
					}
				});
			} catch (Exception e) {
				System.out.println(e);
			}


		}


	}

	private String getTest(HashMap<Map<String, String>, Boolean> testCases) {
		StringBuilder script = new StringBuilder();
		script.append("const httpMocks = require(`node-mocks-http`);");
		script.append("const app = require(`./nhwr`);");
		script.append("let req = '';");
		script.append("let res = '';");
		script.append("let data = '';");
		script.append("let regexPass = null;");
		script.append("let results = new Map();");

		for (Map.Entry<Map<String, String>, Boolean> testCase : testCases.entrySet()) {
			String testString = (testCase.getKey()).keySet().iterator().next();
			script.append("req = httpMocks.createRequest({ method: 'POST', url: '/' , body: {input: '" + testString + "'}});");
			script.append("res = httpMocks.createResponse();");
			script.append("app.handle(req, res);");
			script.append("data = res._getData();");
			script.append("if (res.statusCode == 200 && data.message === 'valid ID') regexPass = true;");
			script.append("if (res.statusCode == 403 && data.message === 'invalid ID') regexPass = false;");
			String condition = (testCase.getKey().get(testString));
			Boolean expectedBool = testCase.getValue();
			script.append("if (regexPass ===" + expectedBool + " && results.get('"+condition+"')!=false){ results.set('"+condition+"', true) ;} else { results.set('"+condition+"', false);}");
		}


		


		script.append("module.exports = results;");
		return script.toString();
	}

}
