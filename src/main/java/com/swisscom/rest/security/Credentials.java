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


/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class Credentials {
	
	String authKey;
	String authKeyValue;
	
	/**
	 * 
	 */
	public Credentials() {
	}
	
	
	
	/**
	 * @param authKey
	 * @param authKeyValue
	 */
	public Credentials(String authKey, String authKeyValue) {
		super();
		this.authKey = authKey;
		this.authKeyValue = authKeyValue;
	}



	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	public String getAuthKeyValue() {
		return authKeyValue;
	}
	public void setAuthKeyValue(String authKeyValue) {
		this.authKeyValue = authKeyValue;
	}

}
