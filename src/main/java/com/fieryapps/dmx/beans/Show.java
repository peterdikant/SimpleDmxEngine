/*
 * Copyright (c) 2014 Peter Dikant
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fieryapps.dmx.beans;

import java.util.List;

/**
 * A Bean to store all settings for a complete light show.
 */
public class Show {
	// name of the light show
	private String name;
	// the OLA universe id to send DMX values to
	private short universe;
	// duration of a frame in milliseconds
	private short frameDuration;
	// the id of the first scene to play after starting SimpleDmxEngine
	private short startScene;
	// list of all DMX addresses that control a dimmer
	private List<Short> dimmerChannels;
	// list of all scenes within the show
	private List<Scene> scenes;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public short getUniverse() {
		return universe;
	}
	
	public void setUniverse(short universe) {
		this.universe = universe;
	}
	
	public short getFrameDuration() {
		return frameDuration;
	}
	
	public void setFrameDuration(short frameDuration) {
		this.frameDuration = frameDuration;
	}
	
	public short getStartScene() {
		return startScene;
	}
	
	public void setStartScene(short startScene) {
		this.startScene = startScene;
	}
	
	public List<Scene> getScenes() {
		return scenes;
	}
	
	public void setScenes(List<Scene> scenes) {
		this.scenes = scenes;
	}
	
	public List<Short> getDimmerChannels() {
		return dimmerChannels;
	}
	
	public void setDimmerChannels(List<Short> dimmerChannels) {
		this.dimmerChannels = dimmerChannels;
	}
}
