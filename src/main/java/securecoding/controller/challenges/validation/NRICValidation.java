package securecoding.controller.challenges.validation;

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

@ChallengeController("/challenges/nric-validation")
public class NRICValidation extends ChallengeControllerAdapter {

	@Override
	public void unitTest(Attempt attempt, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = request.getParameter("code");

		// To ensure that the student does not try to import any module other than the
		// 'express' required
		if (!(code.replaceAll("require[(]\'express\'[)]", "").contains("require("))) {
			attempt.setSubmission(code);

			//Correct Answer: ^(S|T)[0-9]{7}[A-JZ]$  # should be one of 11 letters (A,B,C,D,E,F,G,H,I,Z,J)
 			// Initialize Hashtable to store test cases and scores
			HashMap<Map<String, String>, Boolean> testCases = new HashMap<Map<String, String>, Boolean>();
			// Add test cases and scores here

			//Condition1: Checks whether regex checks for number to start with S or T  (S|T) 
			testCases.put(Collections.singletonMap("", "Condition1"), false);
			testCases.put(Collections.singletonMap("S9123456D", "Condition1"),true);
			testCases.put(Collections.singletonMap("T8123456B", "Condition1"),true);
			testCases.put(Collections.singletonMap("G7123456N", "Condition1"),false);
			//Condition2: Checks whether regex checks for number's length (^[0-9]{7}[A-Z]$)
			testCases.put(Collections.singletonMap("", "Condition2"), false);
			testCases.put(Collections.singletonMap("S9123456D", "Condition2"),true);
			testCases.put(Collections.singletonMap("S912345E", "Condition2"), false);
			testCases.put(Collections.singletonMap("T812345679B", "Condition2"), false);
			//Condition3: Checks whether regex checks for letters, spaces and symbols
			testCases.put(Collections.singletonMap("", "Condition3"), false);
			testCases.put(Collections.singletonMap("T9123456J", "Condition3"),true);
			testCases.put(Collections.singletonMap("S9123 56C", "Condition3"), false);
			testCases.put(Collections.singletonMap("T9123abcD", "Condition3"), false);
			testCases.put(Collections.singletonMap("S912345_E", "Condition3"), false);	//incase they use \w
            //Condition4: Checks for valid and invalid NRIC numbers 
			testCases.put(Collections.singletonMap("", "Condition4"), false);
            testCases.put(Collections.singletonMap("S6969420I", "Condition4"),true);
			testCases.put(Collections.singletonMap("S0138802A", "Condition4"),false);
			testCases.put(Collections.singletonMap("S9907440Z", "Condition4"),true);
			testCases.put(Collections.singletonMap("T1224832J", "Condition4"),false);
			testCases.put(Collections.singletonMap("T2111988H", "Condition4"),true);
			testCases.put(Collections.singletonMap("T1947834G", "Condition4"),false);

			HashMap<String, Integer> conditions = new HashMap<String, Integer>();
			conditions.put("Condition1",4);
			conditions.put("Condition2",4);
			conditions.put("Condition3",2);
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
			script.append("if (res.statusCode == 200 && data.message === 'valid NRIC') regexPass = true;");
			script.append("if (res.statusCode == 403 && data.message === 'invalid NRIC') regexPass = false;");
			String condition = (testCase.getKey().get(testString));
			Boolean expectedBool = testCase.getValue();
			script.append("if (regexPass ===" + expectedBool + " && results.get('"+condition+"')!=false){ results.set('"+condition+"', true) ;} else { results.set('"+condition+"', false);}");
		}


		


		script.append("module.exports = results;");
		return script.toString();
	}

}
