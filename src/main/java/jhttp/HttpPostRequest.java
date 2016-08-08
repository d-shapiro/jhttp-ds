package jhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpPostRequest extends HttpRequest {
	
	protected Writer writer;
	protected OutputStream outputStream; 
	protected String body;
	
	protected HttpPostRequest(String urlString) {
		this(urlString, null);
	}
	
	protected HttpPostRequest(String urlString, String body) {
		super(urlString);
		this.body = body;
	}

	public Writer getWriter() {
		return writer;
	}
	
	protected void makeConnection(URL url) throws IOException {
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
	}
	
	protected void makeWriter() throws UnsupportedEncodingException, IOException {
		outputStream = connection.getOutputStream();
		writer = new OutputStreamWriter(outputStream, charset);
	}
	
	@Override
	public void start() {
		URL url;
		try {
			url = new URL(urlString);
			makeConnection(url);
			
			makeWriter();
			
			if (body != null) {
				writer.write(body);
				writer.flush();
			}
		} catch (MalformedURLException e) {
			connection = null;
			writer = null;
			LOGGER.debug("", e);
		} catch (IOException e) {
			connection = null;
			writer = null;
			LOGGER.debug("", e);
		}
		
	}
	
	protected void finishWriting() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

	@Override
	public String finish() throws ApiException {
		if (connection == null || writer == null) return null;
		try {
			finishWriting();
			
			int code = connection.getResponseCode();
			if (code != 200) {
				throw new ApiException(code, connection.getResponseMessage());
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response.toString();
		} catch (IOException e) {
			LOGGER.debug("", e);
			return null;
		} finally {
			if(connection != null) connection.disconnect(); 
		}
	}

}
