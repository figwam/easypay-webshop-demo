/**
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 */
package com.swisscom.rest.security;


/**
 * @author tgdscald
 *
 */
public class SecurityInformations {
	
	String signature;
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	String contentMD5;
	public String getContentMD5() {
		return contentMD5;
	}
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}
}
