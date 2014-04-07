FormulaMorph Hardware Interface
============

This software connects the physical inputs of the Formula Morph installation to the visualization. The hardware includes 12 x 360 rotrary encoders, 1 joystick, and outputs to 12 controllable LED strip lights. This Java software connects all the hardware inputs (mostly Phidgets) to other piece of software, also in Java, that drives the visualization.

![FormulaMorph Hardware Interface screen shot](https://github.com/ahrv/FormulaMorph/blob/master/screenshot.png)

By using a socket connection, we were able to develop each part of the installation seperately, and in different countries.

Usage
-----

To run this software, import the class files into Eclipse and 'Run as Application'. The main class is "FormulaMorph.java"
To export this project as a runnable .jar, from Eclipse:
- select FormulaMorph.java from the formulaMorph.app package
- right-click and select "export"
- select "Java > Runnable Jar"

Build Your Own
-----

1. Build your own combination of [Phidgets](http://www.phidgets.com/) together with [Link to AH's Phidget controller (not yet online)]. This has been used at the MoMath NYC:
   ![FormulaMorph exhibit at the MoMath](https://raw.github.com/IMAGINARY/FormulaMorph/gh-pages/images/FormulaMorphAtMoMath.jpg)
2. Use whatever input devices you like and communicate with FormulaMorph via the simple [network protocol](#network-protocol) defined below. This requires to implement your own software layer which abstracts from the physical devices. You don't need to modify the FormulaMorph soruce code.
3. Add support for other devices directly into the ForumulaMorph code.

Network protocol
----------------

Here's an overview of the network protocol that is used to communicate between the two applications, as posted by [https://github.com/IMAGINARY/FormulaMorph](IMAGINARY). FormulaMorph can also be controlled via the network. It acts as a client that connects to the server and port given in the file [settings.properties](settings.properties). The protocol itself is best explained using an example session.

```
# a comment
# S: message sent by the server
# C: message sent by the client (FormulaMorph)

C: # send a heart beat comment every second to recognize broken connections
S: # send a heart beat comment every second to recognize broken connections

S: FS,1,3     # select the formula from the list on the left which is 3 formulas ahead of the current one 
C: LD,1,1     # let server know that the new (left) fomula contains a parameter 'a',
              # (allows server to provide feedback to the user, e.g. by enabling the knob controlling this parameter)
C: LD,2,0     # new (left) formula does not contain parameter 'b' 
C: LD,3,0     # new (left) formula does not contain parameter 'c' 
C: LD,4,0     # new (left) formula does not contain parameter 'd' 
C: LD,5,0     # new (left) formula does not contain parameter 'e' 
C: LD,6,0     # new (left) formula does not contain parameter 'f'

C: # heart beat comment
S: # heart beat comment

S: RE,1,10    # increase the value of left parameter 'a' by 10/360 of its range
S: JW,1,5     # rotate the surfaces by moving 5 steps along the rotation path

S: FS,2,-1    # select the formula from the list on the right which is 3 formulas before the current one 
C: LD,7,1     # new (right) formula contains parameter 'a' 
C: LD,8,0     # new (right) formula does not contain parameter 'b' 
C: LD,9,1     # new (right) formula contains parameter 'c' 
C: LD,10,0    # new (right) formula does not contain parameter 'd' 
C: LD,11,0    # new (right) formula does not contain parameter 'e' 
C: LD,12,0    # new (right) formula does not contain parameter 'f'

C: # heart beat comment
S: # heart beat comment

S: RE,9,20    # increase the value of right parameter 'c' by 20/360 of its range

S: JS,1,0.125 # set the morph parameter to 0.125

S: SW,1,1     # save screenshot for the left surface
S: SW,1,2     # save screenshot for the right surface (currently the same as for the left surface)

C: # heart beat comment
S: # heart beat comment

...
```

Dependencies
------------------------

You will need to import the following .jars into your workspace.
core.jar from Processing
net.jar from Processing)
phidget21.jar from [http://www.phidgets.com/](Phidget's) website

Contribute & Collaborate
------------------------

FormulaMorph is part of [IMAGINARY](http://www.imaginary.org) by the [Mathematisches Forschungsinstitut Oberwolfach](http://www.mfo.de). It was originally developed for and in collaboration with the [National Museum of Mathematics, NYC](http://www.momath.org).

The design of the program was done in collaboration with [Moey Inc](http://moeyinc.com/), the company who also produced the hardware and hardware-software connection of the first exhibit.

If you are interested in showing FormulaMorph at your museum or exhibition, you may [contact us and ask for support](http://http://www.imaginary.org/contact).

If you are interested in custom software/hardware development for you installation contact [my studio](http://ahprojects.com/about).



Formula Morph Java app

FormulaMorph.app is a middleware GUI app to connect the Phidget hardware to the Java Surfer MoMath display over a socket connection

This app makes use of the following .jar files which must be linked to as external jars when compiling.




To import this project, in Eclipse:
- select File > import
- select "Existing Project"
- make sure the FormulaMorph.zip file is expaned
- select the root of this directory
  
 
