package formulaMorph.controller;

import java.awt.Rectangle;

import processing.core.PApplet;

public class BasicPhidgetController {
	public String label;

	public int value;
	public String source;
	public int index;
	public int serialNumber;

	public Rectangle bg;
	int bgColor = 0x333333;
	public PApplet parent;

	BasicPhidgetController(PApplet parent, int serialNumber) {
		this.parent = parent;
		this.serialNumber = serialNumber;
	}

	public void displayPhidget() {
		// draw phidget to parent screen
	}

	public void runPhidget() {
		// draw phidget to parent screen
	}

}
