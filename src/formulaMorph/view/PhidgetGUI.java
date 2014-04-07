package formulaMorph.view;

import formulaMorph.model.InstallationSettings;
import processing.core.PApplet;
import processing.core.PFont;

public class PhidgetGUI extends PApplet {

	PApplet parent;
	int bgColor = 0xebebeb3;
	PFont fontH1, fontH2, fontH3, fontP;
	int fontH1Height, fontH2Height, fontH3Height, fontPHeight;
	int fontH1Size, fontH2Size, fontH3Size, fontPSize;

	String msg = "";
	String title = "Formula Morph";
	String subtitle = "Phidget Controller V1";

	int counter = 0;
	int margin = 60;

	int textFadeCounter = 0;
	int textFadeMax = 220; // light grey
	long textFadeInterval = 2000;
	long lastTextFadeInterval;

	public PhidgetGUI(PApplet parent) {

		fontH1Size = 30;
		fontH2Size = 22;
		fontH3Size = 16;
		fontPSize = 12;

		fontH1Height = fontH1Size;
		fontH2Height = fontH2Size;
		fontH3Height = fontH3Size;
		fontPHeight = fontPSize;

		fontH1 = createFont("Arial", fontH1Size);
		fontH2 = createFont("Arial", fontH2Size);
		fontH3 = createFont("Arial", fontH3Size);
		fontP = createFont("Arial", fontPSize);

		this.parent = parent;
	}

	public void display() {
		parent.background(bgColor);

		parent.pushMatrix();

		// Title
		parent.translate(margin, margin);
		parent.textFont(fontH1);
		parent.fill(255);
		parent.text(title, 0, 0);

		// Subtitle
		parent.textFont(fontH2);
		parent.fill(100);
		parent.translate(0, fontH1Height + 10);
		parent.text(subtitle, 0, 0);

		// Message
		// updateTextFadeColor();
		parent.textFont(fontH1);
		parent.fill(50);
		parent.translate(0, fontH2Height + 20);
		parent.text(msg, 0, 0);

		parent.popMatrix();

		parent.pushMatrix();
		parent.translate(margin + 350, margin + 110);
		parent.textFont(fontP);
		parent.fill(100);
		parent.text("Reverse Scroll: " + InstallationSettings.REVERSE_SCROLL,
				0, 0);
		parent.text("Sensitivity Inner: "
				+ InstallationSettings.CLICKS_PER_REVOLUTION_INNER, 0, 20);
		parent.text("Sensitivity Outer: "
				+ InstallationSettings.CLICKS_PER_REVOLUTION_OUTER, 0, 40);
		parent.translate(0, fontH2Height + 20);
		parent.popMatrix();

	}

	public void updateTextFadeColor() {

	}

	public void setMessage(String s) {
		lastTextFadeInterval = millis();
		msg = s;
	}
}
