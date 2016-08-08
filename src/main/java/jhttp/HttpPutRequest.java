package jhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpPutRequest extends HttpRequest {
	
	protected HttpPutRequest(String urlString) {
		super(urlString);
	}

	@Override
	public void start() {
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("PUT");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LOGGER.debug("", e);
			connection = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.debug("", e);
			connection = null;
		}
	}

	@Override
	public String finish() throws ApiException {
		if (connection == null) return null;
		try {
			int code = connection.getResponseCode();
			if (code != 200) {
				throw new ApiException(code, connection.getResponseMessage());
			}
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response.toString();
			
		} catch (UnsupportedEncodingException e) {
			LOGGER.debug("", e);
			return null;
		} catch (IOException e) {
			LOGGER.debug("", e);
			return null;
		} finally {
			connection.disconnect(); 
		}
	}

}
