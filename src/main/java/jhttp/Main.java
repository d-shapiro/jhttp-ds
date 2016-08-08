package jhttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import jhttp.HttpRequest.ApiException;


public class Main {

	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
	
	public static void main(String[] args) {

		print("Login");
		String user = prompt("Username? ");
		String pass = prompt("Password? ");
		
		HttpRequest.setDefaultLogin(user, pass);
		
		print("");
		print("Send a request:");
		String type = prompt("Request Method? ");
		while (type != null && !type.isEmpty()) {
			String url = prompt("URL? ");
			
			if (type.toLowerCase().startsWith("g")) {
				sendGet(url);
			} else if (type.toLowerCase().startsWith("pu")) {
				sendPut(url);
			} else if (type.toLowerCase().contains("mu")) {
				sendMultipart(url);
			} else {
				sendPost(url);
			}
			
			print("");
			print("Send a request:");
			type = prompt("Request Method? ");
		}

	}
	
	private static void sendGet(String url) {
		HttpGetRequest req = HttpRequest.makeGet(url);
		String response = null;
		try {
			print("Sending request...");
			response = req.send();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		if (response != null) {
			print("Got Response:");
			print(response);
		} else {
			print("Request failed");
		}
	}
	
	private static void sendPut(String url) {
		HttpPutRequest req = HttpRequest.makePut(url);
		String response = null;
		try {
			print("Sending request...");
			response = req.send();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		if (response != null) {
			print("Got Response:");
			print(response);
		} else {
			print("Request failed");
		}
	}
	
	private static void sendPost(String url) {
		StringBuilder body = new StringBuilder();
		String line = prompt("Add line to body: ");
		while (line != null && !line.isEmpty()) {
			body.append(line);
			body.append("\n");
			line = prompt("Add line to body: ");
		}
		
		HttpPostRequest req = HttpRequest.makePost(url, body.toString());
		String response = null;
		try {
			print("Sending request...");
			response = req.send();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		if (response != null) {
			print("Got Response:");
			print(response);
		} else {
			print("Request failed");
		}
	}
	
	private static class MultipartElement {
		String name;
		String formValue = null;
		String headerValue = null;
		File file = null;
		MultipartElement(String name, String value, boolean isForm) {
			this.name = name;
			if (isForm) this.formValue = value;
			else this.headerValue = value;
		}
		MultipartElement(String name, File file) {
			this.name = name;
			this.file = file;
		}
	}
	
	private static void sendMultipart(String url) {
		List<MultipartElement> elements = new ArrayList<MultipartElement>();
		
		String type = prompt("Add a form field, header field, or file part? ");
		while (type != null && !type.isEmpty()) {
			if (type.toLowerCase().contains("form")) {
				String name = prompt("Name? ");
				String value = prompt("Value? ");
				elements.add(new MultipartElement(name, value, true));
			} else if (type.toLowerCase().contains("header")) {
				String name = prompt("Name? ");
				String value = prompt("Value? ");
				elements.add(new MultipartElement(name, value, true));
			} else {
				String name = prompt("Field name? ");
				String path = prompt("File path? ");
				File file = new File(path);
				elements.add(new MultipartElement(name, file));
			}
			type = prompt("Add a form field, header field, or file part? ");
		}
		
		HttpMultipartPostRequest req = HttpRequest.makeMultipart(url);
		String response = null;
		try {
			print("Sending request...");
			req.start();
			for (MultipartElement elem: elements) {
				if (elem.formValue != null) {
					req.addFormField(elem.name, elem.formValue, null);
				} else if (elem.headerValue != null) {
					req.addHeaderField(elem.name, elem.headerValue);
				} else if (elem.file != null) {
					try {
						req.addFilePart(elem.name, elem.file, null);
					} catch (IOException e) {
					}
				}
			}
			response = req.finish();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		if (response != null) {
			print("Got Response:");
			print(response);
		} else {
			print("Request failed");
		}
	}
	
	
	private static String prompt(String s) {
		System.out.print(s);
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private static void print(String s) {
		System.out.println(s);
	}

}
