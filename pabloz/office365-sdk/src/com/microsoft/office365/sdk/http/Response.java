package com.microsoft.office365.sdk.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP Response
 */
public interface Response {

	/**
	 * Returns the response headers
	 */
	public Map<String, List<String>> getHeaders();
	
	/**
	 * Return the header value
	 * @param headerName Header to retrieve
	 * @return The header Values
	 */
	public List<String> getHeader(String headerName);
	
	/**
	 * Reads the response stream to the end and returns its value as a String 
	 * @return The response content as a String
	 * @throws IOException
	 */
	public String readToEnd() throws IOException;

	/**
	 * Reads one line from the response stream 
	 * @return A line from the response stream
	 * @throws IOException
	 */
	public String readLine() throws IOException;

	/**
	 * Returns the response HTTP Status code
	 */
	public int getStatus();
}
