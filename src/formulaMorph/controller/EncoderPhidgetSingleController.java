package formulaMorph.controller;

import processing.core.PApplet;

import com.phidgets.EncoderPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.EncoderPositionChangeEvent;
import com.phidgets.event.EncoderPositionChangeListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;

import formulaMorph.app.FormulaMorph;
import formulaMorph.model.InstallationSettings;

public class EncoderPhidgetSingleController extends BasicPhidgetController
		implements IPhidgetController {

	public EncoderPhidget phidget;
	int clickThreshold = InstallationSettings.ROTARY_THRESHOLD_INNER; // In
																		// degrees
	int totalDegreesMoved = 0;
	int rotaryClicksMade = 0;

	public EncoderPhidgetSingleController(PApplet parent, int serialNumber) {
		super(parent, serialNumber);
		try {
			initPhidget();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void runPhidget() {
		// tasks, overridden
	}

	public void displayPhidget() {
		// draw phidget to parent screen
	}

	public void updateValue(int deg) {
		totalDegreesMoved += deg;
		int tempInc = (int) Math.floor(totalDegreesMoved / clickThreshold);

		if (tempInc != 0) {
			rotaryClicksMade += tempInc;
			totalDegreesMoved %= clickThreshold;
		}
	}

	private void flushRotation() {
		rotaryClicksMade = 0;
	}

	private void initPhidget() throws Exception {
		System.out.println("initPhidget Encoder");
		phidget = new EncoderPhidget();

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

		phidget.addEncoderPositionChangeListener(new EncoderPositionChangeListener() {
			public void encoderPositionChanged(EncoderPositionChangeEvent e) {
				int time = e.getTime();
				if (time < 7000) {
					int value = e.getValue();
					updateValue(value);
					if (rotaryClicksMade != 0)
						((FormulaMorph) parent)
								.onEncoderPositionChange(e, rotaryClicksMade);
					flushRotation();
				}

			}
		});

		System.out.println("waiting for InterfaceKit ID# " + serialNumber);
		phidget.open(serialNumber);

		// phidget.waitForAttachment();
	}
}
