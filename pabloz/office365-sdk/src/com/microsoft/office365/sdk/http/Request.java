package com.microsoft.office365.sdk.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.office365.sdk.Constants;
import com.microsoft.office365.sdk.SimpleEntry;


/**
 * Represents an HTTP Request
 */
public class Request {

	private String mVerb;

	private byte[] mContent;

	private HashMap<String, String> mHeaders = new HashMap<String, String>();

	private String mUrl;

	/**
	 * Initializes a request with an HTTP verb
	 * @param httpVerb the HTTP verb
	 */
	public Request(String httpVerb) {
		mVerb = httpVerb;
	}

	/**
	 * Sets the request content
	 */
	public void setContent(String content) {
		mContent = content.getBytes();
	}
	
	/**
	 * Sets the request content
	 */
	public void setContent(byte[] content) {
		mContent = content;
	}

	/**
	 * Returns the request content
	 */
	public byte[] getContent() {
		return mContent;
	}
	
	/**
	 * Returns the request content as a String
	 */
	public String getStringContent() {
		return new String(mContent, Constants.UTF8);
	}
	
	

	/**
	 * Sets the request content with a single name-value pair, using form encoding
	 * @param name The name for the form data
	 * @param value The value for the form data
	 */
	public void setFormContent(String name, String value) {
		List<Entry<String, String>> formValues = new ArrayList<Entry<String, String>>();
		formValues.add(new SimpleEntry<String, String>(name, value));

		setFormContent(formValues);
	}

	/**
	 * Sets the request content with several name-value pairs, using form encoding
	 * @param formValues The name-value pairs
	 */
	public void setFormContent(List<Entry<String, String>> formValues) {
		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> entry : formValues) {
			try {
				sb.append(String.format("%s=%s&", URLEncoder.encode(entry.getKey(), Constants.UTF8_NAME),
						URLEncoder.encode(entry.getValue(), Constants.UTF8_NAME)));
			} catch (UnsupportedEncodingException e) {
			}
		}

		mContent = sb.toString().getBytes();
	}

	/**
	 * Returns the request headers
	 */
	public Map<String, String> getHeaders() {
		HashMap<String, String> copy = new HashMap<String, String>();
		copy.putAll(mHeaders);

		return copy;
	}

	/**
	 * Sets the request headers
	 */
	public void setHeaders(Map<String, String> headers) {
		mHeaders = new HashMap<String, String>();

		if (headers != null) {
			mHeaders.putAll(headers);
		}
	}

	/**
	 * Adds a header to the request
	 * @param name The header name
	 * @param value The header value
	 */
	public void addHeader(String name, String value) {
		mHeaders.put(name, value);
	}

	/**
	 * Removes a header
	 * @param name The header name
	 */
	public void removeHeader(String name) {
		mHeaders.remove(name);
	}

	/**
	 * Sets the request HTTP verb
	 */
	public void setVerb(String httpVerb) {
		mVerb = httpVerb;
	}

	/**
	 * Returns the request HTTP verb
	 */
	public String getVerb() {
		return mVerb;
	}

	/**
	 * Sets the request URL
	 */
	public void setUrl(String url) {
		mUrl = url;
	}

	/**
	 * Returns the request URL
	 */
	public String getUrl() {
		return mUrl;
	}
}
