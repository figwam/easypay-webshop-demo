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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.swisscom.rest.security.RequestSignInformations;
import com.swisscom.rest.security.Signer;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
@Stateless
public class BaseRequests {

	public static final String SEC_KEY = System.getProperty("sec.key"); //Your SEC_KEY here
	
	public static final String MERCHANT_ID = System.getProperty("merchant.id");//Your MERCHANT_ID here

	public static final String BASE_URL = "https://tpe-int.swisscom.ch/ce-rest-service";

	public static final String ACCOUNTS_URL = BASE_URL + "/accounts";

	public static final String SUBSCRIPTIONS_URL = BASE_URL + "/subscriptions";

	public static final String SERVICES_URL = BASE_URL + "/services";

	public static final String PAYMENTS_URL = BASE_URL + "/payments";

	public static final SimpleDateFormat RFC_822_DATE_FORMAT_TZ_GMT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

	private void addSignature(HttpMethod request, String methodByName,
			String path, String merchantId, String contentType, byte[] data) {
		RequestSignInformations reqSign = new RequestSignInformations();
		reqSign.setDate(RFC_822_DATE_FORMAT_TZ_GMT.format(new Date()));
		reqSign.setMethod(methodByName);
		reqSign.setPath(path);
		reqSign.setData(data);
		reqSign.setContentType(contentType);

		if (merchantId != null) {
			request.addRequestHeader("x-merchant-id", merchantId);
		}

		String sig;
		try {
			sig = new Signer().buildSignature(reqSign, SEC_KEY);
			request.addRequestHeader("x-scs-signature", sig);
			request.addRequestHeader("x-scs-date", reqSign.getDate());
			if (data != null) {
				request.addRequestHeader("content-md5",
						new String(Base64.encodeBase64(DigestUtils.md5(data))));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int retrieveSubscriptions(String msisdn, String merchantId,
			String status, List<GetMethod> outMethod) throws HttpException,
			IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod request = new GetMethod(SUBSCRIPTIONS_URL);
		// set headers
		request.addRequestHeader("Accept",
				"application/vnd.ch.swisscom.easypay.subscription.list+json");
		request.addRequestHeader("X-Request-Id", "[req-id]-msisdn-" + msisdn);
		List<NameValuePair> qParam = new ArrayList<NameValuePair>();
		if (msisdn != null) {
			qParam.add((new BasicNameValuePair("msisdn.is", msisdn)));
		}
		if (status != null) {
			qParam.add((new BasicNameValuePair("status.is", status)));
		}
		if (!qParam.isEmpty()) {
			NameValuePair[] q = new BasicNameValuePair[qParam.size()];
			for (int i = 0; i < qParam.size(); i++) {
				q[i] = qParam.get(i);
			}
			String paramString = URLEncodedUtils.format(qParam, null);
			request.setQueryString(paramString);
		}
		if (outMethod != null) {
			outMethod.add(request);
		}

		addSignature(request, "GET", "/subscriptions", merchantId, null, null);
		return httpClient.executeMethod(request);
	}

	public int retrieveServices(String merchantId,
			Boolean doRetrieveOnlyActive, List<GetMethod> outMethod)
			throws HttpException, IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod request = new GetMethod(SERVICES_URL);
		// set headers
		request.addRequestHeader("Accept",
				"application/vnd.ch.swisscom.easypay.service.list+json");
		request.addRequestHeader("X-Request-Id", "[req-id]-msisdn-"
				+ merchantId);
		List<NameValuePair> qParam = new ArrayList<NameValuePair>();
		if (!doRetrieveOnlyActive) {
			qParam.add(new BasicNameValuePair("doRetrieveOnlyActive", "false"));
		}
		if (!qParam.isEmpty()) {
			NameValuePair[] q = new BasicNameValuePair[qParam.size()];
			for (int i = 0; i < qParam.size(); i++) {
				q[i] = qParam.get(i);
			}
			String paramString = URLEncodedUtils.format(qParam, null);
			request.setQueryString(paramString);
		}
		if (outMethod != null) {
			outMethod.add(request);
		}

		addSignature(request, "GET", "/services", merchantId, null, null);
		return httpClient.executeMethod(request);
	}

	public int retrieveServiceByUri(String merchantId, String uri,
			List<GetMethod> outMethod) throws HttpException, IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod request = new GetMethod(uri);
		// set headers
		request.addRequestHeader("Accept",
				"application/vnd.ch.swisscom.easypay.service+json");
		request.addRequestHeader("X-Request-Id", "[req-id]-uri-" + uri);
		if (outMethod != null) {
			outMethod.add(request);
		}
		addSignature(request, "GET", "/services/" + extractServiceId(uri),
				merchantId, null, null);
		return httpClient.executeMethod(request);
	}

	public int commitSubscription(String merchantId,
			String subscriptionId, List<PutMethod> out) throws IOException,
			HttpException {
		return modifySubscription(merchantId, subscriptionId, out,"COMMIT");
	}

	public int rejectSubscription(String merchantId,
			String subscriptionId, List<PutMethod> out) throws IOException,
			HttpException {
		return modifySubscription(merchantId, subscriptionId, out,"REJECT");
	}
	public int cancelSubscription(String merchantId,String subscriptionId, List<PutMethod> outMethod) 
			throws HttpException, IOException {
		return modifySubscription(merchantId, subscriptionId, outMethod, "CANCEL");
	}
	public int deleteSubscription(String merchantId,String subscriptionId, List<DeleteMethod> outMethod) 
			throws HttpException, IOException {
		HttpClient httpClient = new HttpClient();
		DeleteMethod request = new DeleteMethod(BaseRequests.SUBSCRIPTIONS_URL+"/"+subscriptionId);
		// set headers
		request.addRequestHeader("Accept", "application/vnd.ch.swisscom.easypay.message.list+json");
		request.addRequestHeader("X-Request-Id", "[req-id]-subscriptionId-"+subscriptionId);
		if (outMethod != null) {
			outMethod.add(request);
		}
		addSignature(request, "DELETE", "/subscriptions/"+subscriptionId, merchantId, null, null);
		return httpClient.executeMethod(request);
	}

	public int modifySubscription(String merchantId,
			String subscriptionId, List<PutMethod> out, String status)
			throws IOException, HttpException {
		HttpClient httpClient = new HttpClient();
		PutMethod method = new PutMethod(BaseRequests.SUBSCRIPTIONS_URL + "/"
				+ subscriptionId);
		String entity = "{\"operation\": \"" + status + "\"}";
		method.setRequestEntity(new StringRequestEntity(entity,
				"application/vnd.ch.swisscom.easypay.subscription+json", "UTF-8"));
		if (out != null) {
			out.add(method);
		}
		addSignature(method, "PUT", "/subscriptions/" + subscriptionId,
				merchantId, "application/vnd.ch.swisscom.easypay.subscription+json",
				entity.getBytes("UTF-8"));
		return httpClient.executeMethod(method);
	}

	public String extractServiceId(String uri) {
		Matcher matcher = Pattern.compile("/services/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/services/", "");
	}
	
	public String extractSubscriptionId(String uri){
		Matcher matcher = Pattern.compile("/subscriptions/[\\w-]*").matcher(uri);
		matcher.find();
		return matcher.group().replaceAll("/subscriptions/", "");
	}

	public int retrieveSubscriptionByUri(String merchantId,String uri, List<GetMethod> outMethod) throws HttpException, IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod request = new GetMethod(uri);
		// set headers
		request.addRequestHeader("Accept", "application/vnd.ch.swisscom.easypay.subscription+json");
		request.addRequestHeader("X-Request-Id", "[req-id]-uri-"+uri);
		if (outMethod != null) {
			outMethod.add(request);
		}
		addSignature(request, "GET", "/subscriptions/"+extractSubscriptionId(uri),merchantId, null, null);
		return httpClient.executeMethod(request);
	}
	
	public static void main(String[] args) {

		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("EEE, dd MMM yyyy HH:mm:ss t");
		String dateString = formatter.withLocale(Locale.US).print(
				new DateTime().toInstant());
		System.out.println(dateString);
	}
}
