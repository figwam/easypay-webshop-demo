package com.swisscom.refimpl.login;

import java.io.Serializable;
import java.util.List;

import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Subscription;



public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<Service> services;
	
	List<Subscription> mySubscriptions;
    
    Service service;
    
    Subscription subscription;

	private String msisdn;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public List<Subscription> getMySubscriptions() {
		return mySubscriptions;
	}

	public void setMySubscriptions(List<Subscription> mySubscriptions) {
		this.mySubscriptions = mySubscriptions;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	
	
	
}
