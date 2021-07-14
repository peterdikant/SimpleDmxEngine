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
package com.fieryapps.dmx.engine;


import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import com.fieryapps.dmx.DmxStream;

import jline.TerminalFactory;
import jline.console.ConsoleReader;

import com.fieryapps.dmx.beans.Scene;
import com.fieryapps.dmx.beans.Show;
import com.fieryapps.dmx.beans.Step;

/**
 * This is the main DMX engine. It will loop with the configured frame rate and render DMX 
 * frames. These frames are then sent to OLA for hardware output.
 *
 */
public class Engine {
	
	private final Show show;
	private final DmxStream dmxStream;
	
	private final short[] currentFrame = new short[512];
	private long fadeFrames;
	private long holdFrames;
	private short nextStep;
	// indicates whether the current step already has been rendered
	private boolean rendered;
	// master dimmer to change overall brightness in 10 steps
	private short dimmer;
	private Scene currentScene;
	private final ConcurrentLinkedQueue<Integer> keyQueue;
	private final KeyboardReader reader;
	private boolean stop;
	
	/**
	 * Initialize the engine with a loaded show file, a DmxStream and start
	 * a new thread to monitor keyboard input.
	 * 
	 * @param show represents the show file
	 */
	public Engine(Show show, DmxStream dmxStream) {
		this.show = show;
		for (int i = 0; i < 512; i++) {
			currentFrame[i] = 0;
		}
		fadeFrames = 1000;
		holdFrames = 0;
		nextStep = 0;
		dimmer = 10;
		rendered = false;
		stop = false;
		currentScene = show.getScenes().get(show.getStartScene() - 1);
		progressStep(true);
		
		this.dmxStream = dmxStream;
		
		keyQueue = new ConcurrentLinkedQueue<Integer>();
		reader = new KeyboardReader(keyQueue);
		Thread t = new Thread(reader);
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * The main execution loop for the DMX engine.
	 * 
	 * <p>It will check for new key presses by the user, calculate the DMX 
	 * values for a frame and then wait for the remainder of the frame time.</p>
	 */
	public void run() {
		long startTime;
		long timeDelta;
		
		while (!stop) {
			startTime = System.nanoTime();
			checkUserInput();
			computeCurrentFrame();
			dmxStream.streamDmx(show.getUniverse(), currentFrame);
			// time is converted to ms
			timeDelta = Math.round((System.nanoTime() - startTime) / 1000000d);
			if (timeDelta < show.getFrameDuration()) { 
				try {
					Thread.sleep(show.getFrameDuration() - timeDelta);
				} catch(InterruptedException e) {
					e.printStackTrace(System.err);
				}
			} else {
				System.out.print("\rSlow frame encountered: " + timeDelta + " ms");
			}
		}
	}

	/**
	 * Switch to a new step in the current scene or in a new scene. 
	 * 
	 * <p>If <code>newScene</code> is <code>true</code> which
	 * means that the user has selected a new scene, we will activate the first step in that
	 * scene.</p>
	 * 
	 * <p>Else continue with the next step within the current scene. If this scene is set to
	 * repeating mode, we will jump back to step 0 once the last step has finished playing. If
	 * repeat is not enabled, the last step will hold until the user selects a new scene.</p>
	 * 
	 * @param newScene indicates whether the user has triggered a new scene
	 */
	private void progressStep(boolean newScene) {
		if (newScene) {
			// render first step in new scene
			nextStep = 0;
		} else {
			// progress to next step within the current scene
			if (currentScene.getOrder().equalsIgnoreCase("random")) {
				// select a random next step excluding the current step
				short step = (short)ThreadLocalRandom.current().nextInt(currentScene.getSteps()
						.size());
				while (nextStep == step) {
					step = (short)ThreadLocalRandom.current().nextInt(currentScene.getSteps()
							.size());
				}
				nextStep = step;
			} else {
				if (nextStep < (currentScene.getSteps().size() - 1)) {
					// progress to next step
					nextStep++;
				} else if (currentScene.isRepeat()) {
					// repeat the scene starting with the first step
					nextStep = 0;
				} else {
					// remain in the last step of the scene
					return;
				}
			}
		}
		
		// we have a new step determined
		rendered = false;
		// how many frames do we need to hold and fade in this step?
		holdFrames = Math.round(currentScene.getSteps().get(nextStep).getHold() 
				/ (1d * show.getFrameDuration()));
		fadeFrames = Math.round(currentScene.getSteps().get(nextStep).getFade() 
				/ (1d * show.getFrameDuration()));
		
		System.out.format("\rPlaying scene: %-20s Step: %02d/%02d",
				currentScene.getName(), nextStep + 1, currentScene.getSteps().size());
	}
	
	/**
	 * Calculate the current DMX value for each DMX address in the current frame.
	 */
	private void computeCurrentFrame() {
		Step targetStep = currentScene.getSteps().get(nextStep);
		
		if (fadeFrames > 0) {
			// we are still fading to the target values
			for (int i = 0; i < targetStep.getValues().size(); i++) {	
				if (show.getDimmerChannels().contains((short) (i + 1))) {
					// this is a dimmer channel, the target value needs to be modified 
					// by the dimmer value
					short targetValue = (short)Math.round(targetStep.getValues().get(i) 
							* dimmer / 10f);
					currentFrame[i] += Math.round((targetValue - currentFrame[i]) / (1f * fadeFrames));
				} else {
					if (currentScene.getSwitchChannels().contains((short)(i + 1))) {
						// don't fade this channel
						currentFrame[i] = targetStep.getValues().get(i);
					} else {
						// this is a fader channel, fade value
						currentFrame[i] += Math.round((targetStep.getValues().get(i) 
								- currentFrame[i]) / (1f * fadeFrames));
					}
				}
			}
			fadeFrames--;
		} else {
			// we are not fading
			if (!rendered) {
				// frame has not yet been rendered, copy values from show file
				for (int i = 0; i < targetStep.getValues().size(); i++) {
					currentFrame[i] = targetStep.getValues().get(i);
				}
				rendered = true;
			}
			// modify dimmer values even if the frame has already been rendered because
			// user might change the dimmer setting at any time
			for (short dmxAddress: show.getDimmerChannels()) {
				currentFrame[dmxAddress - 1] = (short)Math.round(
						targetStep.getValues().get(dmxAddress - 1) * dimmer / 10f);
			}
			if (holdFrames > 0) {
				holdFrames--;
			} else {
				// hold time has elapsed, progress with next step
				progressStep(false);
			}
		}
	}
	
	/**
	 * Read the next key press from the keyboard queue and trigger appropriate actions.
	 */
	private void checkUserInput() {
		Integer key = keyQueue.poll();
		if (key != null) {
			switch (key) {
				case (int) 'q':
					System.out.println("\nShutting down...");
					stop = true;
					reader.stopThread();
					break;
				
				case (int) '+':
					if (dimmer < 10) {
						dimmer++;
						System.out.print("\rDimmer: " + 10 * dimmer + "%");
					}
					break;
				
				case (int) '-':
					if (dimmer > 0) {
						dimmer--;
						System.out.print("\rDimmer: " + 10 * dimmer + "%");
					}
					break;
				
				default:
					boolean actionTriggered = false;
					for (Scene scene : show.getScenes()) {
						if (scene.getTriggerKeys().contains(key)) {
							currentScene = scene;
							progressStep(true);
							actionTriggered = true;
							break;
						}
					}
					if (!actionTriggered) {
						System.out.print("\rUnknown key pressed: " + key);
					}
					break;
			}
		}
	}
	
	/**
	 * Helper class to run in a separate thread as JLine does not support non 
	 * blocking keyboard reading.
	 * 
	 * <p>Keyboard is captured via a {@link ConsoleReader} and key presses are stored in 
	 * a {@link ConcurrentLinkedQueue} which is shared with the main thread.</p>
	 */
	private static class KeyboardReader implements Runnable {
		
		private final ConcurrentLinkedQueue<Integer> keyQueue;
		private volatile boolean stop;
		private ConsoleReader console;
		
		/**
		 * Setup Keyboard input storing key presses in the supplied {@link ConcurrentLinkedQueue}.
		 * 
		 * @param keyQueue queue to store key presses in
		 */
		public KeyboardReader(ConcurrentLinkedQueue<Integer> keyQueue) {
			this.keyQueue = keyQueue;
			stop = false;
			try {
				TerminalFactory.get().init();
				console = new ConsoleReader();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		/**
		 * Capture key presses using blocking operations within an endless loop.
		 */
		public void run() {
			try {
				while (!stop) {
					int key = console.readCharacter();
					if (key >= 0) {
						keyQueue.offer(key);
					}
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
			} finally {
				try {
					TerminalFactory.get().restore();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
		/**
		 * Stop the keyboard reader thread.
		 */
		public void stopThread() {
			stop = true;
		}
		
	}

}
