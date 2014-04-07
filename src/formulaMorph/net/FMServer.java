package formulaMorph.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import formulaMorph.model.NetworkSettings;

public class FMServer extends Thread {

	/*
	 * Created on Feb 7, 2005
	 */

	/*
	 * 1. Right-click the src directory of the project (so that you export all
	 * its packages) 2. Choose Export, and select "JAR file" under "Java" 3.
	 * Click Next to the third (last) page 4. Select
	 * "Generate the manifest file" 5. At the bottom, browse for the
	 * "Main class" (it shows only those classes with a main method) 6. Finish
	 */

	/** Thanks to:
	 * @author Dan O'Sullivan
	 * 
	 *         Allows conections to come in and spawns thread to take care of
	 *         them
	 */
	static ArrayList allConnections = new ArrayList();
	static int portNum;
	boolean running; // Is the thread running? Yes or no?
	int wait; // How many milliseconds should we wait in between executions?

	public FMServer(int portNum) {
		this.portNum = portNum;

	}

	// Overriding "start()"
	// http://wiki.processing.org/w/Threading
	public void start() {
		// Set running equal to true
		running = true;
		// Print messages
		System.out.println("Starting FMServer thread");
		// Do whatever start does in Thread, don't forget this!
		super.start();
	}

	// Our method that quits the thread
	public void quit() {
		System.out.println("Quitting.");
		running = false; // Setting running to false ends the loop in run()
		// IUn case the thread is waiting. . .
		interrupt();
	}

	public void run() {
		// run is called because it's a Thread
		System.out.println("Running FMServer");
		ServerSocket frontDoor;

		try {
			// frontDoor = new ServerSocket(portNum);
			InetAddress in = InetAddress.getLocalHost();
			try {
				in = InetAddress
						.getByName(NetworkSettings.LOCAL.getIPAddress());
			} catch (UnknownHostException e) {
				// .. whatever
			}
			frontDoor = new ServerSocket(portNum);
			frontDoor.setSoTimeout(0);
			System.out.println("InetAddress.getLocalHost(): " + in);

			System.out.println("Set up a door at " + in + "  "
					+ frontDoor.getLocalPort());
			while (running) {
				System.out.println("Waiting for a new connection");
				Socket connection = frontDoor.accept();// sits here and waits
														// for // someone to
														// knock on the door
				System.out.println(connection.getRemoteSocketAddress()
						+ " knocked on the door and I let them in");
				// farm it out immediately to another thread
				ServerThread newConnection = new ServerThread(connection);
				newConnection.start();// this calls the run method in the thread
				allConnections.add(newConnection); // add it to the list of
													// connections
			}
		} catch (IOException e) {
			String msg = e.getMessage();
			System.out.println("Had a problem making a front door. Message: "
					+ msg);
		}
	}

	static public void tellEveryone(String _text) {
		for (int i = 0; i < allConnections.size(); i++) {
			ServerThread thisConnection = (ServerThread) allConnections.get(i);
			thisConnection.sendToThisClient(_text);
		}
	}

	static public void removeMe(ServerThread _which) {
		allConnections.remove(_which);

	}

}
