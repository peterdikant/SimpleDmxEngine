package com.fieryapps.dmx.beans;

public class Artnet {
	/**
	 * if provided, DMX is sent with unicast directly to this address
	 */
	private String address;
	private int subnet;
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getSubnet() {
		return subnet;
	}
	
	public void setSubnet(int subnet) {
		this.subnet = subnet;
	}
}
