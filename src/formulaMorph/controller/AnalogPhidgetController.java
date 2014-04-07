package formulaMorph.controller;

import processing.core.PApplet;

public class AnalogPhidgetController extends BasicPhidgetController implements
		IPhidgetController {
	public AnalogPhidgetController phidget;

	AnalogPhidgetController(PApplet parent, int serialNumber) {
		super(parent, serialNumber);
	}

	public void runPhidget() {
		// tasks, overridden
	}

	public void displayPhidget() {
		// draw phidget to parent screen
	}

}
