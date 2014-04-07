package formulaMorph.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class RunnableServer {

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

	public static void main(String[] args) {

		ServerSocket frontDoor;
		int portNum = 4767;
		try {
			frontDoor = new ServerSocket(portNum);
			frontDoor.setSoTimeout(0);
			System.out.println("Set up a door at " + InetAddress.getLocalHost()
					+ "  " + frontDoor.getLocalPort());
			while (true) {
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
			System.out.println("Had a problem making a front door" + e);
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
