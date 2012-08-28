package com.swisscom.refimpl.login;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.swisscom.refimpl.control.ServiceControl;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Subscription;

@SessionScoped
@Named
public class Login implements Serializable {
	
	private static final String MSISDN_PATTERN = "(41|42|\\+41|\\+42|0041|0042|0)(\\d){8,9}";
	
	String msisdn;

	@Inject
	Logger log;
    
    @Inject
    ServiceControl serviceControl;

	Map<String, String> params = FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterMap();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User currentUser;
	
//	@PostConstruct
//	public void init() {
//		msisdn = "0795932514";
//		login();
//	}

	public String login() {
		if (msisdn.matches(MSISDN_PATTERN)){
			currentUser = new User();
			currentUser.setMsisdn(parseMsisdn(msisdn));
			List<Service> services = serviceControl.retrieveServices();
			List<Subscription> subscriptions = serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn());
			currentUser.setServices(serviceControl.filterPurchasedSubscriptions(services,subscriptions));
			currentUser.setMySubscriptions(subscriptions);
			log.info("Login with msisdn: "+currentUser.getMsisdn()+" was successful.");
		}
		return "home";
	}

	public void logout() {
		String user = currentUser.getMsisdn();
		currentUser = null;
		log.info("Logout with msisdn: "+user+" was successful.");
	}

	public boolean isLoggedIn() {
		return currentUser != null;
	}

	@Produces
	@LoggedIn
	public User getCurrentUser() {
		return currentUser;
	}

	
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public static String parseMsisdn(String msisdn)
			throws IllegalArgumentException {
		msisdn = msisdn.replaceAll("\\s*", "").replaceAll("/", "")
				.replaceAll("\\(0\\)", "").replaceAll("-", "");
		if (msisdn.matches(MSISDN_PATTERN)) {
			// format the number
			if (msisdn.startsWith("+")) {
				return msisdn.substring(1);
			} else if (msisdn.startsWith("00")) {
				return msisdn.substring(2);
			} else if (msisdn.startsWith("0")) {
				return "41" + msisdn.substring(1);
			} else {
				return msisdn;
			}
		} else {
			throw new IllegalArgumentException("The given phone number: "
					+ msisdn + " has invalid format.");
		}
	}
}