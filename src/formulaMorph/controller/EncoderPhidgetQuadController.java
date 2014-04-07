package formulaMorph.controller;

import processing.core.PApplet;

import com.phidgets.EncoderPhidget;
import com.phidgets.PhidgetException;
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

public class EncoderPhidgetQuadController extends BasicPhidgetController
		implements IPhidgetController {

	public EncoderPhidget phidget;
	int clickThreshold = InstallationSettings.ROTARY_THRESHOLD_INNER; // In
																		// degrees
	long[] totalDegreesMoved;
	int[] rotaryClicksMade;
	long[] values;
	int[] thresholds;

	public EncoderPhidgetQuadController(PApplet parent, int serialNumber,
			int[] thresholds) {
		super(parent, serialNumber);
		this.thresholds = thresholds;
		totalDegreesMoved = new long[4];
		rotaryClicksMade = new int[4];
		for (int i = 0; i < thresholds.length; i++) {
			totalDegreesMoved[i] = 0;
			rotaryClicksMade[i] = 0;
		}
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

	public void updateValue(int deg, int idx) {

		totalDegreesMoved[idx] += deg;

		int tempInc = (int) Math
				.floor(totalDegreesMoved[idx] / thresholds[idx]);

		if (tempInc != 0) {
			rotaryClicksMade[idx] += tempInc;
			totalDegreesMoved[idx] %= thresholds[idx];
		}
	}

	private void flushRotation(int idx) {
		rotaryClicksMade[idx] = 0;
	}

	private void initPhidget() throws Exception {
		// System.out.println("initPhidget Encoder");
		phidget = new EncoderPhidget();

		// type = phidget.getDeviceType();

		phidget.addAttachListener(new AttachListener() {
			public void attached(AttachEvent e) {
				((FormulaMorph) parent).onPhidgetAttached(e);
				enableQuadInputs();
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
				boolean a = false;
				int index = e.getIndex();
				if (thresholds[index] == 0) {
					((FormulaMorph) parent).onEncoderPositionChange(e, 0);
				} else {
					int value = e.getValue();
					updateValue(value, index);
					if (rotaryClicksMade[index] != 0) {
						((FormulaMorph) parent).onEncoderPositionChange(e,
								rotaryClicksMade[index]);

					}
					flushRotation(index);
				}

			}
		});

		phidget.open(serialNumber);
	}

	public EncoderPhidget getPhidget() {
		return phidget;
	}

	private void enableQuadInputs() {

		try {
			phidget.setEnabled(0, true);
			phidget.setEnabled(1, true);
			phidget.setEnabled(2, true);
			phidget.setEnabled(3, true);
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
