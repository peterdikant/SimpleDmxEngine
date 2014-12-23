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
 * This bean stores all settings for a single scene.
 */
public class Scene {
	// name of the scene
	private String name;
	// a list of keycodes that will trigger this scene
	private List<Integer> triggerKeys;
	// should the scene repeat?
	private boolean repeat;
	// order can be 'linear' or 'random'
	private String order;
	// a list of steps within this scene
	private List<Step> steps;
	// a list of DMX addresses that will not fade when fade time is greater than zero
	private List<Short> switchChannels;
	
	public List<Integer> getTriggerKeys() {
		return triggerKeys;
	}
	
	public void setTriggerKeys(List<Integer> triggerKeys) {
		this.triggerKeys = triggerKeys;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public List<Step> getSteps() {
		return steps;
	}
	
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	public List<Short> getSwitchChannels() {
		return switchChannels;
	}
	
	public void setSwitchChannels(List<Short> fadeChannels) {
		this.switchChannels = fadeChannels;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
