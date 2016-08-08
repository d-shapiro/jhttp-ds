package jhttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class HttpMultipartPostRequest extends HttpPostRequest {
	
	private String boundary;
	private static final String LINE_FEED = "\r\n";
	
	protected HttpMultipartPostRequest(String urlString) {
		super(urlString);
		boundary = "===" + System.currentTimeMillis() + "===";
	}
	
	@Override
	protected void makeConnection(URL url) throws IOException {
		super.makeConnection(url);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("User-Agent", "DSA Agent");
	}
	
//	@Override
//	protected void makeWriter() throws UnsupportedEncodingException, IOException {
//		super.makeWriter();
//        writer = new PrintWriter(writer, true);
//	}
	
	/**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value, String contentType) {
    	if (writer == null) return;
    	try {
	        writer.append("--" + boundary).append(LINE_FEED);
	        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
	                .append(LINE_FEED);
	        if (contentType != null) {
	        	writer.append("Content-Type: " + contentType).append(LINE_FEED);
	        }
	        writer.append(LINE_FEED);
	        writer.append(value).append(LINE_FEED);
	        writer.flush();
    	} catch (IOException e) {
    		LOGGER.debug("", e);
    	}
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile, String contentType)
            throws IOException {
    	if (writer == null) return;
        String fileName = uploadFile.getName();
        if (contentType == null) {
        	contentType = URLConnection.guessContentTypeFromName(fileName);
        }
        
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: " + contentType).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        try {
	        byte[] buffer = new byte[4096];
	        int bytesRead = -1;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }
	        outputStream.flush();
        } finally {
        	inputStream.close();
        	writer.append(LINE_FEED);
            writer.flush();
        }    
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
    	if (writer == null) return;
        try {
			writer.append(name + ": " + value).append(LINE_FEED);
			writer.flush();
		} catch (IOException e) {
			LOGGER.debug("" ,e);
		}
    }
    
    @Override
    protected void finishWriting() throws IOException {
    	if (writer == null) return;
    	writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
    }
    
	

}
