package com.amphibian.ffz;

import tv.ouya.console.api.OuyaController;

/**
 * This class is the InputSource implementation for the OUYA
 * game controller.
 * 
 * The left stick is the "default" stick that will produce
 * values for {@link #getStickX()} and {@link #getStickY()}.
 * 
 * It maps the O, U, Y, and A buttons to
 * buttons 3, 4, 1, and 2 (starts with button 1 as being at the
 * top and continuing to number clockwise).
 * 
 * The sticks have a "dead zone" that is 0.25 by default
 * but can be changed if desired.
 * 
 * @author Casey
 *
 */
public class OuyaInputSource implements InputSource {

	private final static float DEFAULT_DEAD_ZONE = 0.25f;
	
	private OuyaController c;
	private float deadZone;
	
	public OuyaInputSource(OuyaController oc) {
		this.c = oc;
		deadZone = DEFAULT_DEAD_ZONE;
	}
	
	public float getDeadZone() {
		return deadZone;
	}

	public void setDeadZone(float deadZone) {
		this.deadZone = deadZone;
	}

	private float filterStick(float f) {
		if (Math.abs(f) < this.deadZone) {
			f = 0;
		}
		return f;
	}
	
	@Override
	public float getStickX() {
		if (c != null) {
			if (c.getButton(OuyaController.BUTTON_DPAD_LEFT)) {
				return -1.0f;
			} else if (c.getButton(OuyaController.BUTTON_DPAD_RIGHT)) {
				return 1.0f;
			} else {
				return filterStick(c.getAxisValue(OuyaController.AXIS_LS_X));
			}
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getStickY() {
		if (c != null) {
			if (c.getButton(OuyaController.BUTTON_DPAD_DOWN)) {
				return -1.0f;
			} else if (c.getButton(OuyaController.BUTTON_DPAD_UP)) {
				return 1.0f;
			} else {
				return filterStick(-c.getAxisValue(OuyaController.AXIS_LS_Y));
			}
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getStick2X() {
		if (c != null) {
			return filterStick(c.getAxisValue(OuyaController.AXIS_RS_X));
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getStick2Y() {
		if (c != null) {
			return filterStick(-c.getAxisValue(OuyaController.AXIS_RS_Y));
		} else {
			return 0.0f;
		}
	}

	@Override
	public boolean isButton1Pressed() {
		return (c != null ? c.getButton(OuyaController.BUTTON_Y) : false);
	}

	@Override
	public boolean isButton2Pressed() {
		return (c != null ? c.getButton(OuyaController.BUTTON_A) : false);
	}

	@Override
	public boolean isButton3Pressed() {
		return (c != null ? c.getButton(OuyaController.BUTTON_O) : false);
	}

	@Override
	public boolean isButton4Pressed() {
		return (c != null ? c.getButton(OuyaController.BUTTON_U) : false);
	}

	@Override
	public boolean isLeftTriggerPressed() {
		return (c != null ? c.getButton(OuyaController.BUTTON_L1) : false);
	}

}
