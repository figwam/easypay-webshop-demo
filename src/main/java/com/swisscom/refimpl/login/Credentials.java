package com.swisscom.refimpl.login;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Credentials {

	private String phoneNumber;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
