package formulaMorph.controller;

import processing.core.PApplet;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.InputChangeListener;
import com.phidgets.event.OutputChangeEvent;
import com.phidgets.event.OutputChangeListener;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;

import formulaMorph.app.FormulaMorph;

public class IOKitPhidgetController extends BasicPhidgetController implements
		IPhidgetController {

	public InterfaceKitPhidget phidget;

	public IOKitPhidgetController(PApplet parent, int serialNumber) {
		super(parent, serialNumber);
		try {
			initPhidget();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error init IOKIt");
		}
	}

	public void runPhidget() {
		// tasks, overridden
	}

	public void displayPhidget() {
		// draw phidget to parent screen
	}

	public void setOutputState(int index, boolean state) {
		try {
			phidget.setOutputState(index, state);
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void initPhidget() throws Exception {
		// System.out.println("initPhidget IOKit");

		phidget = new InterfaceKitPhidget();

		// type = phidget.getDeviceType();

		phidget.addAttachListener(new AttachListener() {
			public void attached(AttachEvent e) {
				((FormulaMorph) parent).onPhidgetAttached(e);
			}
		});

		phidget.addDetachListener(new DetachListener() {
			// Called when this Phidget is detached
			public void detached(DetachEvent e) {
				((FormulaMorph) parent).onPhidgetDetached(e);
			}
		});
		phidget.addErrorListener(new ErrorListener() {
			// Called when there is an error with this Phidget
			public void error(ErrorEvent e) {

			}
		});
		phidget.addInputChangeListener(new InputChangeListener() {
			// called when input state changes
			public void inputChanged(InputChangeEvent e) {
				((FormulaMorph) parent).onPhidgetInputChanged(e);
			}
		});
		phidget.addOutputChangeListener(new OutputChangeListener() {
			// called when output state changes
			public void outputChanged(OutputChangeEvent e) {
				// Redirect
				((FormulaMorph) parent).onPhidgetOutputChanged(e);
			}
		});
		phidget.addSensorChangeListener(new SensorChangeListener() {
			// / called when sensor value is changed? hmmm
			public void sensorChanged(SensorChangeEvent e) {
				((FormulaMorph) parent).onPhidgetSensorChanged(e);
			}
		});

		// System.out.println("waiting for InterfaceKit ID# " + serialNumber);
		phidget.open(serialNumber);

		// phidget.waitForAttachment();
	}

	public InterfaceKitPhidget getPhidget() {
		return phidget;
	}
}
