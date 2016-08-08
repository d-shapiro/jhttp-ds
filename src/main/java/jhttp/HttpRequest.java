package jhttp;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpRequest {
	protected final static Logger LOGGER;
	static {
        LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    }
	
	protected String urlString;
	protected HttpURLConnection connection = null;
	protected String charset = "UTF-8";

	
	protected HttpRequest(String urlString) {
		this.urlString = urlString;
	}
	
	public void addUrlParam(String key, String value) {
		urlString = urlString.replaceAll("\\{"+key+"\\}", value);
	}
	
	public abstract void start();
	public abstract String finish() throws ApiException;
	
	public String send() throws ApiException {
		start();
		return finish();
	}
	
	public static HttpGetRequest makeGet(String urlString) {
		return new HttpGetRequest(urlString);
	}
	
	public static HttpGetRequest makeGet(String urlString, Map<String, String> urlParams) {
		urlString = getUrlString(urlString, urlParams);
		return new HttpGetRequest(urlString);
	}
	
	public static HttpPutRequest makePut(String urlString) {
		return new HttpPutRequest(urlString);
	}
	
	public static HttpPutRequest makePut(String urlString, Map<String, String> urlParams) {
		urlString = getUrlString(urlString, urlParams);
		return new HttpPutRequest(urlString);
	}

	public static HttpPostRequest makePost(String urlString) {
		return new HttpPostRequest(urlString);
	}
	
	public static HttpPostRequest makePost(String urlString, Map<String, String> urlParams) {
		urlString = getUrlString(urlString, urlParams);
		return new HttpPostRequest(urlString);
	}
	
	public static HttpPostRequest makePost(String urlString, String body) {
		return new HttpPostRequest(urlString, body);
	}
	
	public static HttpPostRequest makePost(String urlString, Map<String, String> urlParams, String body) {
		urlString = getUrlString(urlString, urlParams);
		return new HttpPostRequest(urlString, body);
	}
	
	public static HttpMultipartPostRequest makeMultipart(String urlString) {
		return new HttpMultipartPostRequest(urlString);
	}
	
	public static HttpMultipartPostRequest makeMultipart(String urlString, Map<String, String> urlParams) {
		urlString = getUrlString(urlString, urlParams);
		return new HttpMultipartPostRequest(urlString);
	}
	
	public static void setDefaultLogin(String username, String password) {
		Authenticator.setDefault(new BasicAuth(username, password));
	}
	
	private static String getUrlString(String urlTemplate, Map<String, String> urlParams) {
		String urlString = urlTemplate;
		if (urlParams != null) {
			for (Entry<String, String> entry: urlParams.entrySet()) {
				urlString = urlString.replaceAll("\\{"+entry.getKey()+"\\}", entry.getValue());
			}
		}
		return urlString;
	}
	
	static class BasicAuth extends Authenticator {
		private final String user;
		private final String password;
		
		public BasicAuth(String user, String pass) {
			this.user = user;
			this.password = pass;
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication () { 
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}
	
	
	public static class ApiException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4800502515609984212L;
		int code = 0;
		public ApiException(String message) {
		    super(message);
		}
		public ApiException(int code, String message) {
			super("Response code "+ code+ " :" + message);
			this.code = code;
		}
	}
}
