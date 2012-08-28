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
package com.swisscom.rest.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Signer {

	public enum Algorithm {
		HmacSHA1
	}

	Logger log = LoggerFactory.getLogger(Signer.class);

	public String buildSignature(RequestSignInformations request, String secKey) throws SignatureException, MissingMandatoryArgumentException {
		
		// security stuff
		// do asserts
		assertNotNull(request.getDate(), "date or x-scs-date");
		assertNotNull(request.getMethod(), "The method must be set");
		assertNotNull(request.getPath(), "The path must be set");
		
		// calculate content md5
		String contentMd5b64 = "";
		String contentType = "";
		if (request.getData() != null) {
			contentMd5b64 = toBase64(DigestUtils.md5(request.getData()));
			//set content type to empty string if null
			contentType = request.getContentType()==null?"":request.getContentType();
			log.debug("contentMD5b64: " + contentMd5b64);
		}
		
		String stringToSign = request.getMethod() + "\n"
				+ contentMd5b64 + "\n"
				+ contentType + "\n" 
				+ request.getDate() + "\n"
				+ getCanonicalizedResourcePath(request.getPath());

		log.debug("String to sign:\n" + stringToSign);
		log.debug("Key:" + secKey);
		String signature = new String(Base64.encodeBase64(sign(stringToSign, secKey, Algorithm.HmacSHA1)));
		log.debug("Signature:" + signature);
		return signature;
	}
	
	public void validateSignature(RequestSignInformations request, SecurityInformations secInfo, String secKey) 
			throws SignatureException, RequestTimeOddException, MD5NotMatchException, MissingMandatoryArgumentException {
		
		assertNotNull(secInfo.getSignature(), "x-scs-signature");
		assertNotNull(request.getDate(), "date or x-scs-date");
		
		// if data was set, then content-md5 and content-type are mandatory
		if  (request.getData() != null) {
			// either both parameters are set (in case of write requests), or none (in case of read requests)!
			if (secInfo.getContentMD5() != null || request.getContentType() != null) {
				assertNotNull(secInfo.getContentMD5(), "Content-MD5");
				assertNotNull(request.getContentType(), "Content-Type");
				// check the MD5, if sent by client
				String contentMd5b64 = toBase64(DigestUtils.md5(request.getData()));
				if (!secInfo.getContentMD5().equals(contentMd5b64)) {
					throw new MD5NotMatchException("MD5 received: "+secInfo.getContentMD5()+", MD5 expected: "+contentMd5b64);
				}
			}
		}
		
		// check the date
		try {
	    	DateTimeFormatter df = new DateTimeFormatterBuilder().append(null, Constants.PARSERS).toFormatter();
			validateRequestTime(df.withLocale(Locale.US).parseDateTime(request.getDate()));
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new RequestTimeOddException("The request time is odd!");
		}
		
		// check the signature
		String signatureExpected = buildSignature(request, secKey);
		log.debug("Signature expected:"+signatureExpected);
		log.debug("Signature received:"+secInfo.getSignature());
		if (!signatureExpected.equals(secInfo.getSignature())) {
			throw new SignatureException("Signature received: "+secInfo.getSignature()+", Signature expected: "+signatureExpected);
		}
		
	}


	private void assertNotNull(Object param, String errorMessage) throws MissingMandatoryArgumentException {
		if (param == null) {
			throw new MissingMandatoryArgumentException(errorMessage);
		}
	}
	
	private void validateRequestTime(DateTime date) throws RequestTimeOddException {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date.toDate());
		GregorianCalendar gcPlusXMin = new GregorianCalendar();
		gcPlusXMin.add(GregorianCalendar.MINUTE, (Constants.REQUEST_TIME_TOLERANCE_IN_MINUTES/2));
		GregorianCalendar gcMinusXMin = new GregorianCalendar();
		gcMinusXMin.add(GregorianCalendar.MINUTE, -(Constants.REQUEST_TIME_TOLERANCE_IN_MINUTES/2));
		if (gc.before(gcMinusXMin) || gc.after(gcPlusXMin)) {
			throw new RequestTimeOddException("The request time is odd!");
		}
	}

	private String toBase64(byte[] data) {
		byte[] b64 = Base64.encodeBase64(data);
		return new String(b64);
	}

	private String getCanonicalizedResourcePath(String resourcePath) {
		if (resourcePath == null || resourcePath.length() == 0) {
			return "/";
		} else {
			return urlEncode(resourcePath, true);
		}
	}

	private String urlEncode(String value, boolean path) {
		if (value == null)
			return "";

		try {
			String encoded = URLEncoder.encode(value, Constants.DEFAULT_ENCODING)
					.replace("+", "%20").replace("*", "%2A")
					.replace("%7E", "~");
			if (path) {
				encoded = encoded.replace("%2F", "/");
			}

			return encoded;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

    private byte[] sign(String data, String key, Algorithm algorithm)
            throws SignatureException {
    	try {
			return sign(data.getBytes(Constants.DEFAULT_ENCODING), key.getBytes(Constants.DEFAULT_ENCODING), algorithm);
		} catch (UnsupportedEncodingException e) {
    		throw new SignatureException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
    }
    
    private byte[] sign(byte[] data, byte[] key, Algorithm algorithm) throws SignatureException {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new SignatureException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }
}
