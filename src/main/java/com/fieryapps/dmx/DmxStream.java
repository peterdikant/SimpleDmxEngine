package com.fieryapps.dmx;

public interface DmxStream extends AutoCloseable {
	void streamDmx(short universe, short[] currentFrame);
}
