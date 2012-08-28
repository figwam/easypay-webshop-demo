package com.swisscom.refimpl.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import com.swisscom.refimpl.model.Service;
import com.swisscom.refimpl.model.Services;
import com.swisscom.refimpl.model.Subscription;
import com.swisscom.refimpl.model.Subscriptions;
import com.swisscom.refimpl.util.BaseRequests;

@Named
public class ServiceControl implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int HTTP_NO_CONTENT = 204;

	@Inject
	Logger log;

	@Inject
	BaseRequests requestor;

	public List<Service> retrieveServices() {
		List<GetMethod> out = new ArrayList<GetMethod>();
		try {
			requestor.retrieveServices(BaseRequests.MERCHANT_ID, true, out);
			ObjectMapper mapper = new ObjectMapper();
			Services srvs = mapper.readValue(out.get(0).getResponseBody(),
					Services.class);
			Collections.sort(srvs.getServices(),
					Service.COMPARATOR_BY_SERVICE_ID);
			return srvs.getServices();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void commitSubscription(String sId) {
		try {
			List<PutMethod> out = new ArrayList<PutMethod>();
			requestor.commitSubscription(BaseRequests.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Subscription> retrieveSubscriptionsForMsisdn(String msisdn) {
		try {
			List<GetMethod> out = new ArrayList<GetMethod>();
			int rCode = requestor.retrieveSubscriptions(msisdn, BaseRequests.MERCHANT_ID, null, out);
			ObjectMapper mapper = new ObjectMapper();
			log.info("Got response code ["+rCode+"] ");
			if (rCode == HTTP_NO_CONTENT) {
				return new ArrayList<Subscription>();
			} else {
				String r = new String(out.get(0).getResponseBody());
				log.info("Got response: "+r);
				Subscriptions srvs = mapper.readValue(r,Subscriptions.class);
				return srvs.getSubscriptions();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Service retrieveService(String id) {
		try {
			log.info("Get info for service: " + id);
			List<GetMethod> out = new ArrayList<GetMethod>();
			requestor.retrieveServiceByUri(BaseRequests.MERCHANT_ID, BaseRequests.SERVICES_URL
					+ "/" + id, out);
			ObjectMapper mapper = new ObjectMapper();
			return mapper
					.readValue(out.get(0).getResponseBody(), Service.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String img(int idx, int hash) {
		return "img/tn" + hash % 11 + ".jpg";
	}

	public List<Service> filterPurchasedSubscriptions(
			List<Service> services,
			List<Subscription> subs) {
		List<String> l = new ArrayList<String>();
		List<Service> ret = new ArrayList<Service>();
		for (Subscription s : subs) {
			l.add(s.getServiceId());
		}
		for (Service s : services) {
			if (!l.contains(s.getServiceId())){
				ret.add(s);
			}
		}
		return ret;
	}

	public void cancelSubscription(String sId) {
		try {
			List<PutMethod> out = new ArrayList<PutMethod>();
			requestor.cancelSubscription(BaseRequests.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Subscription retrieveSubscription(String id) {
		try {
			log.info("Get info for service: " + id);
			List<GetMethod> out = new ArrayList<GetMethod>();
			requestor.retrieveSubscriptionByUri(BaseRequests.MERCHANT_ID, BaseRequests.SUBSCRIPTIONS_URL
					+ "/" + id, out);
			ObjectMapper mapper = new ObjectMapper();
			String r = new String(out.get(0).getResponseBody());
			log.info("Got response: "+r);
			return mapper.readValue(r, Subscription.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteSubscription(String sId) {
		try {
			List<DeleteMethod> out = new ArrayList<DeleteMethod>();
			requestor.deleteSubscription(BaseRequests.MERCHANT_ID, sId, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	

}
