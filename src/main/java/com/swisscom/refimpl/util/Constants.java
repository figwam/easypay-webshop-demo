/*
 * Copyright 2010-2012 swisscom.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 *
 * This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.swisscom.refimpl.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParser;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Constants {
	
	
    /* **************************************************************** 
	 * This sample App is for demonstration purposes only.
	 * 
     * It is not secure to embed your credentials into source code!
     * 
     * Please keep your secure key always internal. Do never commit/push
     * your key on public repositories!
     ******************************************************************/
	public static final String SEC_KEY = System.getProperty("sec.key","Your_SEC_KEY_here"); //Your SEC_KEY here	
	public static final String MERCHANT_ID = System.getProperty("merchant.id","Your_MERCHANT_ID_here");//Your MERCHANT_ID here
	
	
	

    /* **************************************************************** 
	 * Set the back urls for your app here
     ******************************************************************/
	public static final String REFIMPL_HOST = System.getProperty("refimpl.host","localhost");
	public static final String REFIMPL_PORT = System.getProperty("refimpl.port","8080");
	public static final String REFIMPL_PATH = System.getProperty("refimpl.path","easypay-webshop-demo");
	
	
	
	

	public static final String RFC_822_DATE_FORMAT_TZ_GMT = "EEE, dd MMM yyyy HH:mm:ss Z";

	public static final String ISO_8601_DATE_FORMAT_TZ_GMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static final DateTimeParser[] PARSERS = {
			DateTimeFormat.forPattern(RFC_822_DATE_FORMAT_TZ_GMT).getParser(),
			DateTimeFormat.forPattern(ISO_8601_DATE_FORMAT_TZ_GMT).getParser()};

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final String AUTH_PREFIX = "SCS";

	public static final int REQUEST_TIME_TOLERANCE_IN_MINUTES = 20;

}
