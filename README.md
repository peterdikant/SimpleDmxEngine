SimpleDmxEngine
===============

This is a basic DMX engine written in Java for running small to medium complexity light shows. It 
uses [OLA](https://github.com/OpenLightingProject/ola) for outputting DMX via an USB interface. 
The engine is very light weight so that it can run on a [Raspberry Pi](http://www.raspberrypi.org/).

This is a follow up project to my python based [OLA controller]
(https://github.com/peterdikant/rpi-ola-controller).

Light shows are configured in an YAML file. You can switch to different lighting scenes by pressing
keys on a keyboard.

Installation
------------

The following steps describe how to setup a Raspberry Pi to run a light show.

First you need to install a Java Development Kit. If you are running on a Raspberry Pi please use 
Oracle JDK in version 8. This will ensure the best performance.

Setup OLA as described on the following page: https://wiki.openlighting.org/index.php/OLA_Raspberry_Pi

Make sure that you have OLA set up and running. Create an OLA universe and make sure that your 
light fixtures work.

The OLA daemon needs to be running for SimpleDmxEngine to work.

When OLA is setup and configured and you can control your lighting fixtures with the included OLA 
tools, you can compile the SimpleDmxEngine by running the following command within the project 
folder:

```bash
mvn install
```

This command will download dependencies, compile the application and build a single JAR file 
containing all dependencies.

You can now start the application with the included example show by running:

```bash
java -jar target/SimpleDmxEngine.jar -s shows/example-show.yml
```

The application will now play the first scene. You should see the DMX values change in 
ola_dmxmonitor. Try changing scenes with the number keys. If you press a key that is not mapped 
to a scene, the key code will be displayed, so that you can configure this key in the yaml file.

Press 'q' to quit.

You can change the overall intensity of all dimmer channels with the + and - keys.

Configuration
-------------

Use the example show file as a starting point. First you need to define some settings 
that apply to all scenes:

* `name` the name of your show.

* `universe` should match the universe id you have created in OLA. 

* `frameDuration` is the length of a single DMX frame in milliseconds. A value of `40` means 
  that the controller will send 25 frames per second to your lighting rig. Cheap DMX interfaces 
  are often not able to send more the 40 frames per second. You should try different settings to 
  find the optimal frame rate for your setup.

* `startScene` defines the scene that will be played when the light controller starts. Use a scene 
  with some lights on to see that your Raspberry Pi has finished booting. Scene numbers start 
  with 1.

* `dimmerChannels` is a list of DMX-Channels that are used to control dimmer settings. These 
  channels are affected by the master dimmer value so that you can control the overall brightness 
  of your light show.

Each scene can consist of multiple steps that can be played in linear or random order. Common to 
all steps are the following parameters:

* `name` will be displayed in the console when playing this scene.

* `triggerKeys` is a list of all key codes that will trigger this scene. The easiest way to find 
  key codes is to start the controller and press the keys you want to use. If these keys are not 
  already mapped to a scene, the controller will display the key code. You can then enter this 
  code into the configuration file.

* `repeat` defines if the steps of this scene should be repeated. If this is set to `no` then the 
  last step will hold until you trigger a new scene.

* `order` can be `linear` or `random` and defines the order in which the steps will be played.

* `switchChannels` can be a list of DMX addresses that will not fade from one value to another. 
  Instead these channels will immediately switch to the target value when transitioning from one 
  step to another.

Each step has the following settings:

* `fade` is the time in milliseconds to fade into this scene. If this time is set to `0` then the 
  lights will switch to the new values, else the DMX values will fade to the new settings.

* `hold` will hold the current scene values for the specified time in milliseconds. The hold time 
  starts after the fade time. So if you have a fade time of 500 and a hold time of 1500, the 
  current step will take 2 seconds.

* `values` this represents the DMX values in the current step. The first value is the one at DMX 
  address 1. You need to define the DMX values for all your lighting fixtures in this list.

Performance
-----------

The Raspberry Pi has a very slow CPU. To achieve the best performance make sure you are running 
the Oracle JVM. The startup of the VM is a bit slow and the VM needs the first couple of frames 
to warm up. But once warmed up, you can run a full DMX universe on the Raspberry Pi with 40 frames 
per second. The calculation of a full DMX universe with 512 used DMX addresses needs about 4 
milliseconds on the Raspberry Pi. There might be occasional drops in frame rate whenever the
Raspberry Pi starts some background tasks. But this should not impact your light show.



