package com.fieryapps.dmx.engine;

import com.fieryapps.dmx.DmxStream;
import ola.OlaClient;

public final class OlaClientDmxStream implements DmxStream {
	private final OlaClient olaClient;
	
	public OlaClientDmxStream() throws Exception {
		this.olaClient = new OlaClient();
	}
	
	@Override
	public void streamDmx(short universe, short[] currentFrame) {
		olaClient.streamDmx(universe, currentFrame);
	}
	
	@Override
	public void close() {
	}
}
