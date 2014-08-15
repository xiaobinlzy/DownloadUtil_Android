package com.dll.downloadutil.download;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeaderParser {
    public final static String CONTENT_LENGTH = "Content-Length";
    public final static String CONTENT_TYPE = "Content-Type";
    public final static String ACCEPT_RANGES = "Accetp-Ranges";
    public final static String STATUS_CODE = "Status-Code";

    private Map<String, String> headerMap;

    public HttpResponseHeaderParser() {
	headerMap = new HashMap<String, String>();
    }

    /**
     * <p>
     * get the response header key value pair
     * </p>
     * 
     * @param responseHeaderLine
     */
    public void addResponseHeaderLine(String responseHeaderLine) {
	if (responseHeaderLine.contains(":")) {
	    String[] keyValue = responseHeaderLine.split(": ");
	    if (keyValue[0].equalsIgnoreCase(CONTENT_LENGTH)) {
		headerMap.put(CONTENT_LENGTH, keyValue[1]);
	    } else if (keyValue[0].equalsIgnoreCase(CONTENT_TYPE)) {
		headerMap.put(CONTENT_TYPE, keyValue[1]);
	    } else {
		headerMap.put(keyValue[0], keyValue[1]);
	    }
	} else if (responseHeaderLine.startsWith("HTTP")) {
	    String[] subString = responseHeaderLine.split(" ");
	    headerMap.put(STATUS_CODE, subString[1]);
	}
    }
    
    public int getStatusCode() {
	String strStatusCode = headerMap.get(STATUS_CODE);
	if (strStatusCode == null) {
	    return 0;
	}
	return Integer.parseInt(strStatusCode);
    }

    public int getContentLength() {
	if (headerMap.get(CONTENT_LENGTH) == null) {
	    return 0;
	}
	return Integer.parseInt(headerMap.get(CONTENT_LENGTH));
    }

    public String getFileType() {
	return headerMap.get(CONTENT_TYPE);
    }

    public Map<String, String> getAllHeaders() {
	return headerMap;
    }
}
