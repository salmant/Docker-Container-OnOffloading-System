// ------------------------------------------------------------------------
// Author: Salman Taherizadeh - Jozef Stefan Institute (JSI)
// ------------------------------------------------------------------------

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendingPostRequest {
	
	public static void main(String[] args) throws Exception {

		String url = "http://52.58.107.100:8282/onoffload/api/instruction";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");

		String urlParameters = "json={\"Action\": \"Instantiation\",\"ContainerImageName\": \"tutum/tomcat\",\"HostIP\": \"195.82.14.15\",\"Port\": [{\"Type\": \"tcp\",\"ContainerPort\": \"8080\",\"HostPort\": \"8181\"}],\"EnvironmentVariables\": {\"TOMCAT_PASS\": \"mypass\"}}";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		System.out.println("Sending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());


	}

}
