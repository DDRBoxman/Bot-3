Bot Cubed
=====

A Rep Rap host for Android

Operating Modes
---------------
* Full Host
  * Replaces a desktop or laptop computer
  * Hooks up directly over USB with an OTG cable.
* Panel interface
  * Work in conjunction with a PC or SD card
  * Hooks up to a secondary serial port on the rep rap controller board.
  * Works similarly to [Panelolu](http://www.reprap.org/wiki/Panelolu) or [RepRap Touch](http://www.thingiverse.com/thing:38749)

Tested Devices
--------------

Currently requires **Android 3.1** and up. This is the first version with the [USB host api](http://developer.android.com/guide/topics/connectivity/usb/host.html).

If enough users want it to work on older versions of Android I'll look into using the [ioio](https://www.sparkfun.com/products/11343)

### Printer Controllers
* Working
  * Printrboard
  * RAMPS
* Not Working 

### Phones
* Working
 * Xoom
* Not Working
 * Nexus 4 (http://code.google.com/p/android/issues/detail?id=40087)

Building
--------
Requires [aFileChooser](https://github.com/DDRBoxman/aFileChooser) installed to your maven repo. clone and run mvn install

To build and install BotCubed

    mvn install
    mvn android:deploy
