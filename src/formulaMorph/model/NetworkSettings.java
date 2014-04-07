package formulaMorph.model;

public enum NetworkSettings {
	// enter remote server IP address for testing remotely in real time
	REMOTE("111.111.1.1", 4767), LOCAL("127.0.0.1", 4767);

	private int socketNumber;
	private String ipAddress;

	private NetworkSettings(String _ipAddress, int _socketNumber) {
		socketNumber = _socketNumber;
		ipAddress = _ipAddress;
	}

	public final int getSocketNumber() {
		return socketNumber;
	}

	public final String getIPAddress() {
		return ipAddress;
	}

}
