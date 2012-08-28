/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The Class SubscriptionResponseList.
 * 
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 05.01.2012$
 */
public class Subscriptions implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private List<Subscription> subscriptions;

	/**
	 * Instantiates a new response list SubscriptionResponseList.
	 */
	public Subscriptions() {
		super();
	}

	/**
	 * Gets the subscriptions.
	 * 
	 * @return the subscriptions
	 */
	public List<Subscription> getSubscriptions() {
		if (subscriptions == null)
			subscriptions = new ArrayList<Subscription>();
		return subscriptions;
	}

	/**
	 * Sets the subscriptions.
	 * 
	 * @param subscriptions
	 *            the new subscriptions
	 */
	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
}
