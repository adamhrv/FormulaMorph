package formulaMorph.model;

public enum PacketCodes {

	SWITCH("SW"), LED("LD"), ENCODER("RE"), JOYSTICK("JS"), BIG_WHEEL("FS"), JOG_WHEEL(
			"JW");

	private final String code;

	private PacketCodes(String _code) {
		code = _code;
	}

	public final String getCode() {
		return code;
	}

}
