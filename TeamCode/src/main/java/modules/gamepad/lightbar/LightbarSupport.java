package modules.gamepad.lightbar;

import com.qualcomm.robotcore.hardware.Gamepad;

import modules.gamepad.configuration.lightbar.GamepadLightbar;
import modules.gamepad.lightbar.color.Color;
import modules.gamepad.lightbar.color.RGB;

public class LightbarSupport {

    private final Gamepad gamepad;
    public final RGB color;

    public double HUE;

    public LightbarSupport(Gamepad gamepad) {
        this.gamepad = gamepad;
        this.color   = new RGB();
    }

    public void updateLightbarColor(double value) {

         HUE = Color.linearHue(value, GamepadLightbar.START_HUE, GamepadLightbar.FINISH_HUE);
         Color.HSVtoRGB(HUE, 1.0, 1.0, color);

         gamepad.setLedColor(color.r, color.g, color.b, Gamepad.LED_DURATION_CONTINUOUS);
    }

    public void updateLightbarColor(Color.ColorHue color) {
         HUE = Color.colorToHue(color);
         Color.HSVtoRGB(HUE, 1.0, 1.0, this.color);

        gamepad.setLedColor(this.color.r, this.color.g, this.color.b, Gamepad.LED_DURATION_CONTINUOUS);
    }

    public void turnOff() {

        gamepad.setLedColor(0.0f, 0.0f, 0.0f, Gamepad.LED_DURATION_CONTINUOUS);
    }

}
