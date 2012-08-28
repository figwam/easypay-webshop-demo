/*
 (c) Copyright Swisscom (Schweiz) AG. All rights reserved.

 This product is the proprietary and sole property of Swisscom (Schweiz) AG
 Use, duplication or dissemination is subject to prior written consent of
 Swisscom (Schweiz) AG.

 $Id: $

 */
package com.swisscom.refimpl.model;


/**
 * @author $Author: tgdscald$
 * @version $Revision: $ / $Date: 28.07.2010$
 * 
 */
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "checkoutResponse", propOrder = {})
//@XmlRootElement(name = "checkoutResponseItem")
public class CheckoutResponseItem {
    
//    @XmlEnum(String.class)
    public enum ExceptionType {
//	@XmlEnumValue("PolicyException")
	POLICY_EXCEPTION("PolicyException"), 
//	@XmlEnumValue("ServiceException")
	SERVICE_EXCEPTION("ServiceException"),
//	@XmlEnumValue("RemoteException")
	REMOTE_EXCEPTION("RemoteException");
	String name;
	private ExceptionType(String name) {
	    this.name = name;
	}
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}
    }

    /**
     * Identification of the request. Can be used to correlate the response to
     * the request.
     * */
    private String requestId;

    /**
     * Identifier of the successful reservation. The reservationIdentifer has to
     * be used for the commit or rollback method. Is present in successful
     * reservation
     * */
    private String reservationIdentifier;

    /** The user identification. */
    private String userIdentification;

    /**
     * Is present in not successful reservation
     * */
    private String exception;

    /**
     * Is present in not successful reservation
     */
    private String reason;
    
    private String exceptionType;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReservationIdentifier() {
        return reservationIdentifier;
    }

    public void setReservationIdentifier(String reservationIdentifier) {
        this.reservationIdentifier = reservationIdentifier;
    }

    public String getUserIdentification() {
        return userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getExceptionType() {
		return exceptionType;
	}

}
