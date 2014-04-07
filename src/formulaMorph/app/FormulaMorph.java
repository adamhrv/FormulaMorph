package formulaMorph.app;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.net.Client;

import com.phidgets.EncoderPhidget;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.EncoderPositionChangeEvent;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.OutputChangeEvent;
import com.phidgets.event.SensorChangeEvent;

import formulaMorph.controller.BasicPhidgetController;
import formulaMorph.controller.EncoderPhidgetQuadController;
import formulaMorph.controller.IOKitPhidgetController;
import formulaMorph.controller.PhidgetSystemMonitor;
import formulaMorph.model.InstallationSettings;
import formulaMorph.model.NetworkSettings;
import formulaMorph.model.PacketCodes;
import formulaMorph.model.PhidgetSerials;
import formulaMorph.net.FMServer;
import formulaMorph.view.PhidgetGUI;

public class FormulaMorph extends PApplet {

	public static void main(String _args[]) {
		PApplet.main(new String[] { FormulaMorph.class.getName() });
	}

	// ArrayList of all Phidgets Controller Objects
	ArrayList<BasicPhidgetController> phidgets;

	// A system monitor
	PhidgetSystemMonitor phidgetSystemMonitor;

	// A client/server connection
	Client client;
	int clientReconnectAttempts = 0, maxClientReconnectAttempts = 3;
	int clientWriteAttempts = 0, maxClientWriteAttempts = 2;
	int failedClientWriteAttempts = 0, failedClientWriteAttemptsMax = 2;
	int serverReconnectAttempts = 0, maxServerReconnectAttempts = 100;
	FMServer server;
	boolean networkAlive = false;
	int counter = 0;
	NetworkSettings networkSetting = NetworkSettings.LOCAL; // LOCAL,REMOTE
	String inPacket;
	long checkupInterval = 1000, lastCheckupTime;
	long fmPingTimeMax = 4000, lastFMPingReceived;
	long phidgetControllerTimeoutMax = 2500, lastPCPingReceived;
	PFont font;

	// The interface and background
	PhidgetGUI phidgetGUI;

	// Phidgets
	public EncoderPhidgetQuadController phidgetA;
	public EncoderPhidgetQuadController phidgetB;
	public EncoderPhidgetQuadController phidgetC;
	public EncoderPhidgetQuadController phidgetD;
	public IOKitPhidgetController phidgetE;
	public IOKitPhidgetController phidgetF;

	//

	public void setup() {

		size(640, 480);

		startServer(); // Create the server
		startClient();
		String[] settingLines = loadStrings(System.getProperty("user.dir") + "/data/settings.txt");
		for (int i = 0; i < settingLines.length; i++) {
			if (settingLines[i].charAt(0) == '#')
				continue;
			String[] pieces = split(settingLines[i], ',');
			String label = trim(pieces[0]);
			String val = trim(pieces[1]);
			if (label.equals("reverseScroll")) {
				boolean b = (val.equals("1")) ? true : false;
				InstallationSettings.setScrollDir(b);
				println("set scroll dir: " + b);
			} else if (label.equals("innerSensitivity")) {
				int v = Integer.parseInt(val);
				InstallationSettings.setSensitivityInner(v);
				println("set inner sense: " + v);
			} else if (label.equals("outerSensitivity")) {
				int v = Integer.parseInt(val);
				InstallationSettings.setSensitivityOuter(v);
				println("set outer sense: " + v);
			}
		}

		try {
			Thread.sleep(1000L); // one second
		} catch (Exception e) {
		}

		initPhidgets(); // Create all Phidgets
		phidgetSystemMonitor = new PhidgetSystemMonitor(); // Monitor
		initGUI(); // GUI

	}

	private void initGUI() {
		phidgetGUI = new PhidgetGUI(this);
		font = createFont("Arial", 14);
	}

	private void startServer() {
		if (networkSetting == networkSetting.REMOTE) {
			return;
		}

		println("startServer, attempt: " + serverReconnectAttempts);

		if (serverReconnectAttempts > maxServerReconnectAttempts) {
			println("No connections possible.");
			println("Sorry. Exiting.");
			exit();
		}

		try {
			server = new FMServer(4767); // Start a simple server on a port
			server.start();
			serverReconnectAttempts = 0;
			clientReconnectAttempts = 0;
			// runs right away
		} catch (Exception e) {
			serverReconnectAttempts++;
			System.out
					.println("ERROR: Could not reconnect to local server. Attempt #"
							+ serverReconnectAttempts);
		}

	}

	private void startClient() {

		println("startClient, attempt: " + clientReconnectAttempts);
		clientReconnectAttempts++;

		if (clientReconnectAttempts > maxClientReconnectAttempts
				&& networkSetting == networkSetting.LOCAL) {
			println("too many client reconnect attempts("
					+ maxClientReconnectAttempts + "), restart server ");
			if (networkSetting == networkSetting.LOCAL) {
				restartServer();
			}
		} else {
			if (networkSetting == networkSetting.LOCAL) {
				try {
					if (server == null) {
						println("sever is null and local, try restarting");
						restartServer();
					} else {
						client = null;
						println("creating new client here");

						client = new Client(this,
								networkSetting.getIPAddress(),
								networkSetting.getSocketNumber());

						clientReconnectAttempts++;
					}
				} catch (Exception e) {
					println("ERROR: Couldn't connect client.");
				}
			} else {
				// remote
				try {
					client = new Client(this, networkSetting.getIPAddress(),
							networkSetting.getSocketNumber());
					clientReconnectAttempts++;

				} catch (Exception e) {
					println("ERROR: Couldn't connect client.");
				}
			}
		}
	}

	private void stopServer() {
		println("STOPPING server");
		clientReconnectAttempts = 0;
		if (server != null)
			server.quit();
	}

	private void stopClient() {
		println("stopCleint");
		try {
			System.out.println("Restarting client...");
			if (client != null) {
				client.stop();
				client = null;
			}
		} catch (Exception e) {
			System.out.println("ERROR: stopping client");
		}
	}

	private void restartServer() {
		clientReconnectAttempts = 0;
		stopServer();
		startServer();
	}

	private void restartClient() {
		stopClient();
		startClient();
	}

	private void initPhidgets() {

		phidgets = new ArrayList<BasicPhidgetController>();

		phidgetE = new IOKitPhidgetController(this,
				PhidgetSerials.IOKIT_E.getSerialNumber());

		// Rotary Encoders
		int tInner = InstallationSettings.ROTARY_THRESHOLD_INNER;
		int tOuter = InstallationSettings.ROTARY_THRESHOLD_OUTER;

		int[] thresholdsA = { tInner, tInner, tInner, tInner };
		phidgetA = new EncoderPhidgetQuadController(this,
				PhidgetSerials.ENCODER_4X_A.getSerialNumber(), thresholdsA);

		int[] thresholdsB = { tInner, tInner, tInner, tInner };
		phidgetB = new EncoderPhidgetQuadController(this,
				PhidgetSerials.ENCODER_4X_B.getSerialNumber(), thresholdsB);

		int[] thresholdsC = { tInner, tInner, tInner, tInner };
		phidgetC = new EncoderPhidgetQuadController(this,
				PhidgetSerials.ENCODER_4X_C.getSerialNumber(), thresholdsC);
		int[] thresholdsD = { tOuter, tOuter, tInner, 0 };// FS1,FS1,JW1
		phidgetD = new EncoderPhidgetQuadController(this,
				PhidgetSerials.ENCODER_4X_D.getSerialNumber(), thresholdsD);

		// TODO
		// Add analog Phidget
		phidgetF = new IOKitPhidgetController(this,
				PhidgetSerials.ANALOG_F.getSerialNumber());

		phidgets.add(phidgetA);
		phidgets.add(phidgetB);
		phidgets.add(phidgetC);
		phidgets.add(phidgetD);
		phidgets.add(phidgetE);
		phidgets.add(phidgetF);
	}

	public void draw() {

		// Look and feel
		phidgetGUI.display();

		pushMatrix();
		translate(60, 150);

		// show network status
		noStroke();
		fill(100);
		textFont(font);
		text("Network status: ", 0, 0);
		if (networkAlive) {
			fill(0, 255, 0, 100);

		} else {
			fill(255, 0, 0, 100);
		}

		ellipse(120, -5, 20, 20);

		fill(100);
		textFont(font);
		translate(0, 40);
		int textHeight = 20;
		boolean attached = false;
		// for each Phidget, display its GUI
		for (BasicPhidgetController p : phidgets) {
			p.runPhidget();
		}

		// Dsiplay attachments
		try {
			attached = phidgetA.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetA: " + attached, 0, 0);
		translate(0, textHeight);

		// B
		try {
			attached = phidgetB.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetB: " + attached, 0, 0);
		translate(0, textHeight);

		// C
		try {
			attached = phidgetC.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetC: " + attached, 0, 0);
		translate(0, textHeight);

		// D
		try {
			attached = phidgetD.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetD: " + attached, 0, 0);
		translate(0, textHeight);

		// E
		try {
			attached = phidgetE.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetE: " + attached, 0, 0);
		translate(0, textHeight);

		// F
		try {
			attached = phidgetF.getPhidget().isAttached();
		} catch (PhidgetException e) {
		}
		text("PhidgetF: " + attached, 0, 0);
		translate(0, 18);

		popMatrix();

		// How we doing?
		phidgetSystemMonitor.checkup();

		// Tell software we're alive, if we are
		monitorState();

	}

	/*************************************************
	 * Incoming Data from TCP/IP Socket
	 *************************************************/
	public void clientEvent(Client c) {
		// Callback from the Client object
		if (c != null) {
			inPacket = c.readStringUntil('\n'); // \n
			if (inPacket != null) {
				if (inPacket.endsWith("#AH\n")) {
					// ignore this one, it's my ping
					onReceivePCPing();
				} else if (inPacket.endsWith("#FM\n")) {
					if (inPacket.length() > 3) {
						parseInputPacket(inPacket);
					}
					onReceiveFMPing();
				}
			}
		}
	}

	public void parseInputPacket(String s) {
		print("parseInputPacket\t " + s);

		String code = "";
		String[] pieces = {};

		try {
			pieces = split(s, ",");
		} catch (Exception e) {
		}

		try {
			code = pieces[0];
		} catch (Exception e) {
		}

		if (code.equals(PacketCodes.LED.getCode()) && pieces.length == 3) {
			// LD,6,1#FM
			String[] valuePieces = split(pieces[2], '#');
			if (valuePieces.length == 2) {
				int id = Integer.parseInt(pieces[1]); // LED index
				boolean state = (Integer.parseInt(trim(valuePieces[0])) == 1) ? true
						: false; // On or Off
				try {
					// Turn On/Off LEDs
					// 1 - 12
					id = remapLEDId(id);
					// LED phidget is using 0 index, subtract 1
					id--;
					phidgetE.phidget.setOutputState(id, state);
				} catch (Exception e) {
				}
			}
		}
	}

	/*************************************************
	 * Outgoing Data
	 *************************************************/
	String packetSuffix = "#AH";

	public void sendPacket(String s) {

		String packet = s + packetSuffix + '\n';
		if (!(networkSetting == NetworkSettings.REMOTE) && server == null) {
			println("ERROR: could not sendPacket\t" + packet
					+ " because server is null and local");
			return;
		} else if (client == null) {
			println("ERROR: could not sendPacket\t" + packet
					+ " because client is null");
			failedClientWriteAttempts++;
			return;
		} else if (!client.active()) {
			println("ERROR: could not sendPacket\t" + packet
					+ " because client is not active.");
			failedClientWriteAttempts++;
			return;
		} else {
			try {
				client.write(s + packetSuffix + '\n');
				System.out.print("Sending packet\t\t" + packet);
				failedClientWriteAttempts = 0;
				phidgetGUI.setMessage(s);
			} catch (Exception e) {
				println("ERROR: could not write\t" + packet
						+ " because of exception");
			}
		}
	}

	/*************************************************
	 * Monitor the Socket Server
	 *************************************************/

	public void monitorState() {

		if (millis() - lastCheckupTime > checkupInterval) {

			// perform checkup
			lastCheckupTime = millis();

			// Check local return pings

			if (failedClientWriteAttempts > failedClientWriteAttemptsMax) {
				restartClient();
				networkAlive = false;
			} else if (millis() - lastPCPingReceived > phidgetControllerTimeoutMax) {
				System.out.println("Timeout on client/server communication!");

				System.out.print("Client status: ");
				if (client == null) {
					System.out.println("null");
				} else if (client.active()) {
					System.out.println("active");
				} else {
					System.out.println("Not null, not active");
				}

				System.out.print("Server status: ");
				if (server == null) {
					System.out.println("null");
				} else {
					System.out.println("OK");
				}

				println("RECONNECTING client...");
				println("clientReconnectAttempts\t" + clientReconnectAttempts);
				try {
					restartClient();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					println("could not start client");
				}
				networkAlive = false;
			} else {
				networkAlive = true;
			}

			// Send out a heartbeat
			sendPacket("");
		}

	}

	public void onReceiveFMPing() {
		// Network must still be alive
		// Ping received from Formula Morph
		lastFMPingReceived = millis();
	}

	public void onReceivePCPing() {
		lastPCPingReceived = millis();
		// Ping received from Phidget Controller software (me)
	}

	/*************************************************
	 * Phidget Handlers, Handle all events here!
	 *************************************************/

	public void onPhidgetInputChanged(InputChangeEvent e) {

		String packet = "";
		String code;
		Phidget sourcePhidget;
		boolean state;
		int stateInt;
		int index = 0, id = 0;
		boolean dataReady = false;

		sourcePhidget = e.getSource();
		state = e.getState();
		stateInt = (state) ? 1 : 0;
		index = e.getIndex() + 1;

		try {
			id = sourcePhidget.getDeviceID();
		} catch (PhidgetException e1) {
		}

		// When the output changes, broadcast it to the client
		switch (id) {
		case Phidget.PHIDID_INTERFACEKIT_0_16_16:
			if (state == true) {
				println("OK source: " + sourcePhidget + ", class: " + id
						+ " state: " + state + ", index: " + index);
				if (index < 9) {
					// All inputs have index < 9
					code = PacketCodes.SWITCH.getCode();
					packet = code + "," + index + "," + stateInt;
					dataReady = true;
				}
			}
			break;

		default:
			println("nothing to process");
			break;
		}

		if (dataReady) {
			sendPacket(packet);
		}

	}

	public void onPhidgetOutputChanged(OutputChangeEvent e) {
		// switch on device type

	}

	public void onPhidgetSensorChanged(SensorChangeEvent e) {
		// switch on device type
		println("onPhidgetSensorChanged: " + e);
		// When the sensor value changes (on joystick) send it to the client

		String packet = "";
		String code;
		InterfaceKitPhidget sourcePhidget;
		boolean state;
		int stateInt;
		int index = 0, id = 0;
		boolean dataReady = false;

		sourcePhidget = (InterfaceKitPhidget) e.getSource();
		int sensorValue = 0;
		try {
			sensorValue = sourcePhidget.getSensorValue(0);
		} catch (PhidgetException e2) {
		}
		// normalize between 0 and 1
		float sensorMax = InstallationSettings.JOYSTICK_HIGH;
		float sensorMin = InstallationSettings.JOYSTICK_LOW;
		float sensorValueFl = map(sensorValue, sensorMin, sensorMax, 0, 1);
		if (sensorValueFl < 0)
			sensorValueFl = 0;
		if (sensorValueFl > 1)
			sensorValueFl = 1;

		double sensorValNorm = (double) Math.round(sensorValueFl * 100000) / 100000;
		if (sensorValNorm > .46 && sensorValNorm < .54) {
			sensorValNorm = .5;// make it stick in the middle
		}
		index = e.getIndex() + 1;

		try {
			id = sourcePhidget.getDeviceID();
		} catch (PhidgetException e1) {
		}

		code = PacketCodes.JOYSTICK.getCode();
		packet = code + "," + index + "," + sensorValNorm;
		dataReady = true;

		sendPacket(packet);

	}

	public void onAddSensorChangeListener(SensorChangeEvent e) {
		// When a new sensor is added...
	}

	public void onPhidgetAttached(AttachEvent e) {
		// When a new sensor is added...
	}

	public void onPhidgetDetached(DetachEvent e) {
		// When a new sensor is added...
	}

	public void onEncoderPositionChange(EncoderPositionChangeEvent e, int clicks) {
		println("onEncoderPositionChange");

		String packet = "";
		int idx = 0;
		int id = 0, encoderPos = 0, indexPos = 0, pos = 0, value = 0;
		boolean dataReady = false;
		String code = PacketCodes.ENCODER.getCode();
		EncoderPhidget sourcePhidget = (EncoderPhidget) e.getSource();

		idx = e.getIndex();
		int time = e.getTime();
		value = e.getValue();

		try {
			pos = sourcePhidget.getPosition(idx);
			println("pos: " + pos);
		} catch (PhidgetException e2) {
		}
		
		try {
			id = sourcePhidget.getDeviceID();
		} catch (PhidgetException e1) {
		}

		String label = "";
		try {
			label = sourcePhidget.getDeviceLabel();
		} catch (PhidgetException e1) {
		}

		switch (id) {
		case Phidget.PHIDID_ENCODER_HS_1ENCODER:
			break;
		case Phidget.PHIDID_ENCODER_HS_4ENCODER_4INPUT:
			// get ID
			println("id: " + id + ", index: " + idx + ", label: " + label
					+ ", value: " + value);

			if (label.equals("morphA")) {
				// offset is zero
				code = "RE";
				idx += 1;
			} else if (label.equals("morphB")) {
				idx += 5;
				code = "RE";
			} else if (label.equals("morphC")) {
				idx += 9;
				code = "RE";
			} else if (label.equals("morphD")) {

				if (idx == 0) {
					// FS,1
					idx = 1;
					code = "FS";
					if (InstallationSettings.REVERSE_SCROLL) {
						value = 1 * clicks;
					} else {
						value = -1 * clicks;
					}
				} else if (idx == 1) {
					// FS,2
					idx = 2;
					code = "FS";
					if (InstallationSettings.REVERSE_SCROLL) {
						value = -1 * clicks; // reversed,wheel is upside down
					} else {
						value = 1 * clicks; // wheel is normal
					}

				} else if (idx == 2) {
					// incoming packet example: JW,1
					idx = 1;
					code = "JW";
					value = clicks;
				}
			}
			if (code.equals("RE")) {
				idx = remapRotaryIndex(idx);
			}
			packet = code + "," + idx + "," + value;
			sendPacket(packet);
			break;
		default:
			break;
		}
	}

	private int remapLEDId(int idx) {
		// first, convert id to an index
		switch (idx) {
		case 1:
			// Phidget Index 0
			idx = 2;
			break;
		case 2:
			// Phidget Index 1
			idx = 4;
			break;
		case 3:
			// Phidget Index 2
			idx = 3;
			break;
		case 4:
			// Phidget Index 3
			idx = 1;
			break;
		case 5:
			// Phidget Index 4
			idx = 5;
			break;
		case 6:
			// Phidget Index 5
			idx = 6;
			break;
		case 7:
			// Phidget Index 6
			idx = 11;
			break;
		case 8:
			// Phidget Index 7
			idx = 9;
			break;
		case 9:
			// Phidget Index 8
			idx = 10;
			break;
		case 10:
			// Phidget Index 9
			idx = 12;
			break;
		case 11:
			// Phidget Index 10
			idx = 8;
			break;
		case 12:
			// Phidget Index 11
			idx = 7;
			break;
		default:
			break;
		}
		return idx;
	}

	private int remapRotaryIndex(int idx) {
		switch (idx) {
		// for when we needed to move the switches around
		case 1:
			// Phidget Index 0
			idx = 4;
			break;
		case 2:
			// Phidget Index 1
			idx = 1;
			break;
		case 3:
			// Phidget Index 2
			idx = 3;
			break;
		case 4:
			// Phidget Index 3
			idx = 2;
			break;
		case 5:
			// Phidget Index 4
			idx = 5;
			break;
		case 6:
			// Phidget Index 5
			idx = 6;
			break;
		case 7:
			// Phidget Index 6
			idx = 12;
			break;
		case 8:
			// Phidget Index 7
			idx = 11;
			break;
		case 9:
			// Phidget Index 8
			idx = 8;
			break;
		case 10:
			// Phidget Index 9
			idx = 9;
			break;
		case 11:
			// Phidget Index 10
			idx = 7;
			break;
		case 12:
			// Phidget Index 11
			idx = 10;
			break;
		default:
			break;
		}
		return idx;
	}
}
