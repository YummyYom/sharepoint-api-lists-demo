package com.microsoft.office365.sdk.http;


/**
 * Interface that defines a generic HttpConnection
 */
public interface HttpConnection {
	
	/**
	 * Executes an request
	 * @param request The request to execute
	 * @param responseCallback The callback to invoke when the response is returned
	 * @return A Future for the operation
	 */
	public HttpConnectionFuture execute(final Request request);
}
