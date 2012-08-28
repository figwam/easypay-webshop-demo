/**
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 */
package com.swisscom.rest.security;

/**
 * @author TGDSCALD
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
