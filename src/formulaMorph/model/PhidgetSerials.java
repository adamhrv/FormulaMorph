package formulaMorph.model;

public enum PhidgetSerials {
	ENCODER_1X_1(271189), IOKIT_E(255657), ENCODER_4X_A(301497), ENCODER_4X_B(
			301237), ENCODER_4X_C(300563), ENCODER_4X_D(301189), ANALOG_F(
			267529);


	private final int serialNumber;

	private PhidgetSerials(int _serialNumber) {
		serialNumber = _serialNumber;

	}

	public final int getSerialNumber() {
		return serialNumber;
	}

}
