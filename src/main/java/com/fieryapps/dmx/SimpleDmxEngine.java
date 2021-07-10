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
package com.fieryapps.dmx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fieryapps.dmx.beans.Show;
import com.fieryapps.dmx.engine.DmxStreams;
import com.fieryapps.dmx.engine.Engine;

/**
 * Entry point for the SimpleDmxEngine. Parses command line parameters
 * and starts the DMX engine.
 */
public class SimpleDmxEngine 
{
	
	@Parameter(names = {"-s", "--show"}, description = "Full path to the YAML show file to use", 
			required = true)
	private String showFile;
	
	/**
	 * Open the show file and read the settings from the show file into Java Beans. Once the show
	 * file is loaded, the engine is started.
	 */
	public void run() {
		Show show = null;

		try {
			InputStream input = new FileInputStream(showFile);
			Yaml yaml = new Yaml();
			show = yaml.loadAs(input, Show.class);
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error opening show file '" + showFile + "': " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error starting application: " + e.getMessage());
		}
		
		try (DmxStream dmxStream = DmxStreams.createStreamFor(show)) {
			Engine engine = new Engine(show, dmxStream);
			engine.run();
		} catch (Exception e) {
			System.err.println("Error starting application: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	/**
	 * Entry point for the application.
	 * 
	 * @param args command line arguments
	 */
	public static void main( String[] args )
	{
		
		SimpleDmxEngine app = new SimpleDmxEngine();
		new JCommander(app, args);
	    app.run();
	}
}
