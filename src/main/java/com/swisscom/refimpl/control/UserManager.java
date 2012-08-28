package com.swisscom.refimpl.control;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import com.swisscom.refimpl.login.LoggedIn;
import com.swisscom.refimpl.login.User;
import com.swisscom.refimpl.model.CheckoutRequestItem;
import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Subscription;
import com.swisscom.refimpl.util.Constants;
import com.swisscom.rest.security.SignatureException;

@Named
public class UserManager {

	private static final String PURCHASE_STATUS_SUCCESS = "success";

	private static final String PURCHASE_STATUS_ERROR = "error";

	private static final String PURCHASE_STATUS_CANCEL = "cancel";

	@Inject
	Logger log;

	@Inject
	ServiceControl serviceControl;

	@Inject
	@LoggedIn
	User currentUser;

	public Service getService() {	
		Map<String, String> params = FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterMap();

		String id = params.get("serviceId");
		if (currentUser.getService() != null && id != null
				&& currentUser.getService().getServiceId().equals(id)) {
			return currentUser.getService();
		} else {
			currentUser.setService(serviceControl.retrieveService(id));
			return currentUser.getService();
		}
	}

	public Subscription getSubscription() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String id = params.get("subscriptionId");
		if (currentUser.getSubscription() != null && id != null
				&& currentUser.getSubscription().getSubscriptionId().equals(id)) {
			return currentUser.getSubscription();
		} else {
			currentUser.setSubscription(serviceControl.retrieveSubscription(id));
			return currentUser.getSubscription();
		}
	}


	public String getPurchaseStatusColor() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		if (status == null) {
			status = "";
		}
		if (status.equals(PURCHASE_STATUS_ERROR)){
			return "red";
		} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
			return "green";
		} else if (status.equals(PURCHASE_STATUS_CANCEL)){
			return "gray";
		} else {
			return "black";
		}
	}


	public String printPurchaseStatus() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();

		String status = params.get("purchase");
		if (status == null) {
			status = "";
		}
		if (status.equals(PURCHASE_STATUS_ERROR)){
			return "Service purchase failed, do to some technical Problems.";
		} else if (status.equals(PURCHASE_STATUS_SUCCESS)){
			return "Service purchase was successfull.";
		} else if (status.equals(PURCHASE_STATUS_CANCEL)){
			return "Service purchase was canceled by customer.";
		} else {
			return "";
		}
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public String doCheckoutOnePhase() {
		return doCheckout(false);
	}

	public String doCheckoutTwoPhase() {
		return doCheckout(true);
	}

	public String doCheckout(boolean isTwoPhase) {
		try {
			CheckoutRequestItem ri = new CheckoutRequestItem();
			serviceToCheckoutItem(currentUser.getService(), ri);
			ri.setServiceUrl(ri.getServiceUrl()+"&isTwoPhase="+isTwoPhase);
			String coItem = ri.toJson();
			byte[] coItemb64 = Base64.encodeBase64(coItem.getBytes("UTF-8"));
			byte[] signb64 = Base64.encodeBase64(sign(coItem.getBytes("UTF-8"),
					Constants.SEC_KEY.getBytes("UTF-8")));
			String coUrl = "https://tpe-int.swisscom.ch/checkout/CheckoutPage.seam?signature="
					+ urlEncode(new String(signb64),false)
					+ "&checkoutRequestItem="
					+ urlEncode(new String(coItemb64),false);
			log.info(coUrl);
			
			// do avoid auto commit on easypay side
			if (isTwoPhase) {
				coUrl+="&capture=false";
			}
			return coUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doCancel(String sId) {
		try {
			serviceControl.cancelSubscription(sId);
			currentUser.setSubscription(null);
			return "mySubscriptions";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doDelete(String sId) {
		try {
			serviceControl.deleteSubscription(sId);
			currentUser.setMySubscriptions(serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn()));
			return "mySubscriptions";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String doCommit() {
		try {
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();

			String purchaseStatus = params.get("purchase");
			boolean isTwoPhase = Boolean.valueOf(params.get("isTwoPhase") == null?"false":params.get("isTwoPhase"));
			if (purchaseStatus == null) {
				purchaseStatus ="";
			}
			String reqId = params.get("requestId");
			String reservationId = params.get("reservationId");
			String signature = params.get("signature");
			String checkoutResponseItem = params.get("checkoutResponseItem");
			
			if (purchaseStatus.equals(PURCHASE_STATUS_SUCCESS)) {
				
				if (checkoutResponseItem != null) {
					String coItem = new String(Base64.decodeBase64(checkoutResponseItem.getBytes("UTF-8")));
					log.info("Checkout response: "+coItem);
					byte[] signb64 = Base64.encodeBase64(sign(coItem.getBytes("UTF-8"),
							Constants.SEC_KEY.getBytes("UTF-8")));
					log.info("Checkout signature expected: "+new String(signb64));
					log.info("Checkout signature received: "+signature);
				}
				log.info("Request id received: "+reqId);
				log.info("Resevation id received: "+reservationId);
				
				if (isTwoPhase) {
					log.info("Do manual commit");
					serviceControl.commitSubscription(reservationId);
				}

				List<Service> services = serviceControl.retrieveServices();
				List<Subscription> subscriptions = serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn());
				currentUser.setServices(serviceControl.filterPurchasedSubscriptions(services,subscriptions));
				currentUser.setMySubscriptions(serviceControl.retrieveSubscriptionsForMsisdn(currentUser.getMsisdn()));
				
			}
			
			return purchaseStatus;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void serviceToCheckoutItem(Service service, CheckoutRequestItem ri) {
		String backUrl = "http://"+Constants.REFIMPL_HOST+""+(Constants.REFIMPL_PORT.equals("80")?"":":"+Constants.REFIMPL_PORT)+""+(Constants.REFIMPL_PATH.equals("")?"":"/"+Constants.REFIMPL_PATH);
		ri.setAdultContent(false);
		ri.setAmount(new BigDecimal(service.getAmount()));
		ri.setBillingText(service.getServiceId());
		ri.setCancelUrl(backUrl+"/commit.jsf?purchase="+PURCHASE_STATUS_CANCEL);
		ri.setContentPartnerId("testPartner7");
		ri.setContentPartnerServiceId(service.getServiceId());
		ri.setCurrency("CHF");
		if (service.getDuration() != null) {
			ri.setDuration(service.getDuration().toString());
		}
		ri.setDurationUnit(service.getDurationUnit());
		ri.setRequestId(UUID.randomUUID().toString());
		ri.setRoaming(false);
		ri.setServiceErrorUrl(backUrl+"/commit.jsf?purchase="+PURCHASE_STATUS_ERROR);
		ri.setServiceUrl(backUrl+"/commit.jsf?purchase="+PURCHASE_STATUS_SUCCESS);
		if (service.getHasTrial()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Trial);
		} else if (service.getIsRecurrent()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Recurrent);
		} else if (service.getDuration() != null
				&& service.getDurationUnit() != null
				&& !service.getIsRecurrent()) {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.NonRecurrent);
		} else {
			ri.setTransactionType(CheckoutRequestItem.TransactionType.Event);
		}
	}

	private byte[] sign(byte[] data, byte[] key) throws SignatureException {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			return mac.doFinal(data);
		} catch (Exception e) {
			throw new SignatureException(
					"Unable to calculate a request signature: "
							+ e.getMessage(), e);
		}
	}
	
	private String urlEncode(String value, boolean path) {
		if (value == null)
			return "";

		try {
			String encoded = URLEncoder.encode(value, Constants.DEFAULT_ENCODING)
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
			if (path) {
				encoded = encoded.replace("%2F", "/");
			}

			return encoded;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

}