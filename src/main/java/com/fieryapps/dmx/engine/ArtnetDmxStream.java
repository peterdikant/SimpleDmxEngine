package com.fieryapps.dmx.engine;

import ch.bildspur.artnet.ArtNetClient;
import com.fieryapps.dmx.DmxStream;

class ArtnetDmxStream implements DmxStream {
	private final ArtNetClient client = new ArtNetClient();
	private final String address;
	private final int subnet;
	
	public ArtnetDmxStream(String address, int subnet) {
		this.address = address;
		this.subnet = subnet;
		client.start();
	}
	
	@Override
	public void streamDmx(short universe, short[] currentFrame) {
		byte[] dmxData = toBytes(currentFrame);
		
		if (address != null) {
			client.unicastDmx(address, subnet, universe, dmxData);
		} else {
			client.broadcastDmx(subnet, universe, dmxData);
		}
	}
	
	private byte[] toBytes(short[] currentFrame) {
		byte[] dmxData = new byte[currentFrame.length];
		for (int i = 0; i < currentFrame.length; i++) {
			dmxData[i] = (byte) currentFrame[i];
		}
		return dmxData;
	}
	
	@Override
	public void close() {
		client.stop();
	}
}
