package formulaMorph.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Thanks to:
 * @author Dan O'Sullivan A thread that listens text and relays it to other
 *         people Try to add and introduction that asks for their name and then
 *         preppends name to all comments Try adding functionality where you can
 *         wisper to a specific person
 */
public class ServerThread extends Thread {
	Socket connection;
	BufferedReader brin;
	PrintWriter pout;
	String uniqueName;
	boolean stillRunning = true;

	ServerThread(Socket _connection) {
		connection = _connection;
		uniqueName = "Conn" + _connection.getRemoteSocketAddress();
		// trade the standard byte input stream for a fancier one that allows
		// for more than just bytes
		try {
			brin = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			pout = new PrintWriter(connection.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("couldn't get streams" + e);
		}
	}

	public void run() {
		while (stillRunning) {
			String text = null;
			try {
				text = brin.readLine(); // find out how big the package is
			} catch (IOException e) {
				killIt();
				System.out.println("couldn't listen" + e);
				break;
			}
			// go through all the other connections and relay this input to them
			if (text == null) {
				killIt();
				break;
			}
			FMServer.tellEveryone(text);
		}

	}

	public void killIt() {
		System.out.println("Removing Connection");
		stillRunning = false;
		RunnableServer.removeMe(this);
	}

	synchronized public void sendToThisClient(String _what) {
		pout.println(_what);
		pout.flush();
	}
}
