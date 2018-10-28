package com.fanatics.sterling.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import com.yantra.yfc.log.YFCLogCategory;

public class RESTClient {
	private String baseUrl;
	private String username;
	private String password;
	
	private String clientID;
	private String secretClientID;
	private String token;
	
	private String authType;

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory"); 

	public String getDataFromServer(String path) throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			log.info("RESTClient URL: " + getBaseUrl() + path);
			URL url = new URL(getBaseUrl() + path);//

			HttpsURLConnection conn = (HttpsURLConnection) setConnectionDetails(url);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("Accept", "application/xml");
			
			if (conn.getResponseCode() != FANConstants.SC_OK && conn.getResponseCode() != FANConstants.SC_NO_CONTENT) {
				log.error("Failed : HTTP error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	public String putDataToServer(String path, String payload) throws Exception {
		StringBuilder sb = new StringBuilder();

		try {
			URL url = new URL(getBaseUrl() + path);
			HttpsURLConnection conn = (HttpsURLConnection) setConnectionDetails(url);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("Accept", "application/xml");
			conn.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(payload);
			osw.flush();

			if (conn.getResponseCode() != FANConstants.SC_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage() + " \nURL: "
						+ conn.getRequestMethod() + " " + conn.getURL());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);

			}
			reader.close();

			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return payload;
	}

	public String postDataToServer(String path, String payload) throws Exception {
		StringBuilder sb = new StringBuilder();

			URL url = new URL(getBaseUrl() + path);
			log.info("payload " + payload + " Url:" + url);
			HttpsURLConnection conn = (HttpsURLConnection) setConnectionDetails(url);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/xml");
            conn.setRequestProperty("Accept", "application/xml");
			conn.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
	        osw.write(payload);        
	        osw.flush();
	        
			if (conn.getResponseCode() != FANConstants.SC_OK && conn.getResponseCode() != FANConstants.SC_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "  + conn.getResponseCode() + " - " + conn.getResponseMessage() + " \nURL: " + conn.getRequestMethod() + " " + conn.getURL());
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			return sb.toString();

	}
	
	private URLConnection setConnectionDetails(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();

		log.info("AuthType: " + getAuthType());
		//basic
		if (getAuthType().equals(FANConstants.AUTHTYPE_BASIC)) {
			String authString = getUsername() + ":" + getPassword();
			String authStringEnc = new String(Base64.encode(authString.getBytes()));
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			log.info("AuthString Basic : " + authStringEnc);
		}
		else
		{	//oauth
			urlConnection.setRequestProperty("X-Auth-Client",  getClientID());
			urlConnection.setRequestProperty("X-Auth-Token", getToken());
			
			log.info("X-Auth-Client : " + getClientID() + "\n X-Auth-Token : " + getToken());
		}
	
		return urlConnection;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getSecretClientID() {
		return secretClientID;
	}

	public void setSecretClientID(String secretClientID) {
		this.secretClientID = secretClientID;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
