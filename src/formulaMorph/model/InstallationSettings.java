package formulaMorph.model;

public class InstallationSettings {
	// 0 threshold is full resolution (1440)

	public static int CLICKS_PER_REVOLUTION_OUTER = 10; // default is 5
	public static int CLICKS_PER_REVOLUTION_INNER = 72; // default is 36
	public static int ROTARY_THRESHOLD_OUTER = 1440 / CLICKS_PER_REVOLUTION_OUTER;
	public static int ROTARY_THRESHOLD_INNER = 1440 / CLICKS_PER_REVOLUTION_INNER;

	public static boolean REVERSE_SCROLL = true;
	// Network
	public static final int PORT_NUMBER = 4767;

	// Joystick
	public static final float JOYSTICK_LOW = 15f;
	public static final float JOYSTICK_HIGH = 990f;

	public static void setScrollDir(boolean b) {
		REVERSE_SCROLL = b;
	}

	public static void setSensitivityOuter(int i) {
		CLICKS_PER_REVOLUTION_OUTER = i;
		ROTARY_THRESHOLD_OUTER = 1440 / CLICKS_PER_REVOLUTION_OUTER;
	}
	public static void setSensitivityInner(int i) {
		CLICKS_PER_REVOLUTION_INNER = i;
		ROTARY_THRESHOLD_INNER = 1440 / CLICKS_PER_REVOLUTION_INNER;
	}

}
