package com.fieryapps.dmx.engine;

import com.fieryapps.dmx.DmxStream;
import com.fieryapps.dmx.beans.Artnet;
import com.fieryapps.dmx.beans.Show;

public final class DmxStreams {
	public static DmxStream createStreamFor(Show show) throws Exception {
		if (show == null) {
			throw new NullPointerException("Show must not be null");
		}
		
		Artnet artnet = show.getArtnet();
		if (artnet != null) {
			return new ArtnetDmxStream(artnet.getAddress(), artnet.getSubnet());
		}
		
		return new OlaClientDmxStream();
	}
}
