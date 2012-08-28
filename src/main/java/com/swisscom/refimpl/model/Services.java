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
 * The Class ServiceResponseList.
 * 
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 05.01.2012$
 */
public class Services implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private List<Service> services;

	/**
	 * Instantiates a new response list ServiceResponseList.
	 */
	public Services() {
		super();
	}

	/**
	 * Gets the services.
	 * 
	 * @return the services
	 */
	public List<Service> getServices() {
		if (services == null)
			services = new ArrayList<Service>();
		return services;
	}

	/**
	 * Sets the services.
	 * 
	 * @param services
	 *            the new services
	 */
	public void setServices(List<Service> services) {
		this.services = services;
	}
}
