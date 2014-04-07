FormulaMorph Hardware Interface
============

This software connects the physical inputs of the MoMATH Formula Morph installation to the visualization. The hardware includes 15 x 360 rotrary encoder wheels, 1 joystick, 2 momentary buttons, and outputs to 12 controllable LED strip lights. This Java applications connects all the hardware inputs (mostly Phidgets) to the visualization software, also in Java, that creates the visualization.

![FormulaMorph Hardware Interface screen shot](https://raw.githubusercontent.com/ahrv/FormulaMorph/master/screenshot.png)
Screenshot of hardware interface software

![IMAGINARY'S post](https://raw.github.com/IMAGINARY/FormulaMorph/gh-pages/images/FormulaMorphAtMoMath.jpg)
Formular Morph installation at MoMATH NYC

Usage
-----

To run this software, import the class files into Eclipse and 'Run as Application'. The main class is "FormulaMorph.java"
To export this project as a runnable .jar, from Eclipse:

1. select FormulaMorph.java from the formulaMorph.app package

2. right-click and select "export"

3. select "Java > Runnable Jar"


Build Your Own
-----
Follow the guide on [IMAGINARY's post](http://imaginary.github.io/FormulaMorph/) for how to build your own version of this installation. Phidgets can be purchased from [Phidgets.com](http://www.phidgets.com/).


Network protocol
----------------

Here's an overview of the network protocol that is used to communicate between the two applications, as posted by [IMAGINARY](http://imaginary.github.io/FormulaMorph/). FormulaMorph can also be controlled via the network. The protocol itself is best explained using an example session.

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
net.jar from Processing
phidget21.jar from [http://www.phidgets.com/](Phidget's) website

Contribute & Collaborate
------------------------

FormulaMorph is part of [IMAGINARY](http://www.imaginary.org) by the [Mathematisches Forschungsinstitut Oberwolfach](http://www.mfo.de). It was originally developed for and in collaboration with the [National Museum of Mathematics, NYC](http://www.momath.org).

The design of the program was done in collaboration with [Moey Inc](http://moeyinc.com/), the company who also produced the hardware and hardware-software connection of the first exhibit.

If you are interested in showing FormulaMorph at your museum or exhibition, [contact IMAGINARY and ask for support](http://http://www.imaginary.org/contact).

If you are interested in custom software/hardware development for you installation contact [Adam Harvey's studio in Brooklyn](http://ahprojects.com/about).

MoMATH Museum NYC
------------------------

If you are in NYC you can see this very same code running on the Formula Morph installation. Enjoy.
 
