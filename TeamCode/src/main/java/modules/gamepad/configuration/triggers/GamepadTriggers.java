package modules.gamepad.configuration.triggers;


import modules.gamepad.triggers.BinaryTrigger;
import modules.gamepad.triggers.DefaultTrigger;
import modules.gamepad.triggers.Trigger;

public class GamepadTriggers {

    public static TriggerInput left_trigger  = TriggerInput.DEFAULT;

    public static TriggerInput right_trigger = TriggerInput.DEFAULT;

    public static Class<? extends Trigger> getTriggerType(TriggerInput trigger) {

        switch(trigger) {
            case BINARY:
                return BinaryTrigger.class;
            case DEFAULT:
                return DefaultTrigger.class;
        }
        return DefaultTrigger.class;
    }
}
