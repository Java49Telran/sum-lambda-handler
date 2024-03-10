package telran.aws.lambda;

import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class SumHttpHandler implements RequestStreamHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		var logger = context.getLogger();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		String response = null;
		try {
			Map<String, Object> mapInput = (Map<String, Object>) parser.parse(reader);
			logger.log("mapInput: " + mapInput.toString());
			Map<String, Object> mapParameters = (Map<String, Object>) mapInput.get("pathParameters");
			if(mapParameters == null) {
				throw new IllegalArgumentException("request doesn't contain parameters");
			}
			logger.log("mapParameters: " + mapParameters.toString());
			String op1Str = (String) mapParameters.get("op1");
			if(op1Str == null) {
				throw new IllegalArgumentException("no op1 operand-parameter");
			}
			
			double op1 = Double.parseDouble(op1Str);
			String op2Str = (String) mapParameters.get("op2");
			if(op2Str == null) {
				throw new IllegalArgumentException("no op2 operand-parameter");
			}
			double op2 = Double.parseDouble(op2Str);
			response = createResponse(Double.toString(op1 + op2), 200);
			logger.log("debug: response is " + response);
			
			
		} catch (Exception e) {
			String body = e.toString();
			logger.log("error: " + body);
			response = createResponse(body, 400);
		} 
		PrintStream printStream = new PrintStream(output);
		printStream.println(response);
		printStream.close();
		

	}
	String createResponse(String body, int statusCode) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", statusCode);
		map.put("body", body);
		String jsonStr = JSONObject.toJSONString(map);
		return jsonStr;
	}
	

}
