/**
 * 
 */
package com.sample.co.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.sample.co.exceptions.ServiceException;
import com.sample.co.service.SMSMessage;
import com.sample.co.service.SMSService;


public class SMSStandardService implements SMSService {
	
	
	/** 
	 * {@link Logger}
	 */
	private static Logger log = Logger.getLogger(SMSStandardService.class);
	
	/** ERROR */
    private static final String ERROR_STRING = "ERROR";
    
	/** 
	 * Holder of the property smsUrl
	 */
	private String smsUrl;
	
	/** 
	 * Holder of the property action
	 */
	private String action;
	
	/** 
	 * Holder of the property userName
	 */
	private String userName;
	
	/** 
	 * Holder of the property password
	 */
	private String password;
	
	/** 
	 * Holder of the property maxSplit
	 */
	private String maxSplit;
	
	
	/**
	 * 	{@inheritDoc}
	 */
	public void doSend(SMSMessage smsMessage) throws ServiceException {
		try {
			smsMessage.validate();
			String data = constructData(smsMessage);
		    
		    URL url = new URL(smsUrl);
		    URLConnection connection = url.openConnection();
		    connection.setDoOutput(true);
		    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		    writer.write(data);
		    writer.flush();
		    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    StringBuffer response = new StringBuffer();
		    String line = "";
		    while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		    writer.close();
		    reader.close();
		    
		    if (response.toString().contains(ERROR_STRING)) {
		    	throw new ServiceException("Error in sending sms : " + response);
		    }
		    
		} catch (Exception e) {
			log.error(e);
			throw new ServiceException(e);
		}
		

	}


	/**
	 * @param smsMessage
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String constructData(SMSMessage smsMessage)
			throws UnsupportedEncodingException {
		String data = URLEncoder.encode("action", "UTF-8") 
			+ "=" + URLEncoder.encode(action, "UTF-8");
		data += "&" + URLEncoder.encode("user", "UTF-8") 
			+ "=" + URLEncoder.encode(userName, "UTF-8");
		data += "&" + URLEncoder.encode("password", "UTF-8") 
			+ "=" + URLEncoder.encode(password, "UTF-8");
		data += "&" + URLEncoder.encode("from", "UTF-8") 
			+ "=" + URLEncoder.encode(smsMessage.getSender(), "UTF-8");
		data += "&" + URLEncoder.encode("to", "UTF-8") 
			+ "=" + URLEncoder.encode(smsMessage.getRecipient(), "UTF-8");
		data += "&" + URLEncoder.encode("text", "UTF-8") 
			+ "=" + URLEncoder.encode(smsMessage.getText(), "UTF-8");
		data += "&" + URLEncoder.encode("maxsplit", "UTF-8") 
			+ "=" + URLEncoder.encode(maxSplit, "UTF-8");
		return data;
	}


	/**
	 * @param smsUrl the smsUrl to set
	 */
	public void setSmsUrl(String smsUrl) {
		this.smsUrl = smsUrl;
	}


	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}


	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * @param maxSplit the maxSplit to set
	 */
	public void setMaxSplit(String maxSplit) {
		this.maxSplit = maxSplit;
	}

}
