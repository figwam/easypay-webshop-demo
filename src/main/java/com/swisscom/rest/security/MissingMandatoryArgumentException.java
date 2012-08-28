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
public class MissingMandatoryArgumentException extends Exception {

	/**
	 * @param string
	 * @param e
	 */
	public MissingMandatoryArgumentException(String string, Exception e) {
		super(string,e);
	}
	
	public MissingMandatoryArgumentException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
