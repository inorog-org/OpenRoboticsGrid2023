package manipulation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Arm {
    private static final boolean MODE_GOTO = true;
    private static final boolean MODE_MANUAL = false;

    private static final int LEVEL0TICKS = 10;
    private static final int LEVEL1TICKS = 410;
    private static final int LEVEL2TICKS = 1800;
    private static final int LEVEL3TICKS = 2300;
    private static boolean prevstate = true;

    private final DcMotor motor;
    private final LinearOpMode opMode;
    private TouchSensor magneticSwitch;
    // level 1, 2 au 3
    public int level = 0;
    private boolean state = MODE_MANUAL;
    private static final double SPEED = 0.6;

    public Arm(LinearOpMode opMode) {
        motor = opMode.hardwareMap.get(DcMotor.class, "arm");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.magneticSwitch = opMode.hardwareMap.get(TouchSensor.class, "magneticSwitch");
        this.opMode = opMode;
    }

    /**
     * Functia de control a bratului apelata in bucla principala de OpMode
     */
    public void fullArm() {

        opMode.telemetry.addData("Arm Position", motor.getCurrentPosition());
        opMode.telemetry.addLine();
        opMode.telemetry.addData("Level ", level);
        opMode.telemetry.addLine();
        opMode.telemetry.addData("State  ", state);

        levelAndModeUpdate();
        opMode.telemetry.addData("Mode= ",state);
        if (state == MODE_GOTO) {
            // GoTo mode
            executeLevelControlled(level);
        } else {
            // Manual mode
            actionArm();
        }
    }
    /**
     * Citim gamepad pentru a stabili modul si level
     */
    private void levelAndModeUpdate() {

        // Test GoTo Ground Level
        if (opMode.gamepad2.right_stick_button) {
            if (prevstate) {
                level = 0;
                state = MODE_GOTO;
                prevstate=false;
            }
        }
    }
    // Numarul de ticks pe final de drum pentru a merge mai usor cu motorul
    public static final int DELTA_TICKS = 10;
    public void executeLevelControlled(int level) {

        int x = getPositionByLevel(level) - motor.getCurrentPosition();
        int sign = (x > 0 ? 1 : -1);
        if (x < 0) {
            x = -x;
        }
        if (x >= -30 && x <= 30) {
            motor.setPower(0);
            state = MODE_MANUAL;
            return;
        }
        double power;
        if (x <= DELTA_TICKS) {
            // Am ajus pe final si mergem proportional mai usor
            power = x / DELTA_TICKS;
            power = (power < 0.07 ? 0.07 : power);
        } else {
            // Max speed
            power = 0.7;
        }
        if (sign == -1)
            motor.setPower(-power);
        else
            motor.setPower(power);
        prevstate=true;

    }


    public int getPositionByLevel(int level) {

        switch (level) {
            case 0: return LEVEL0TICKS;
            case 1: return LEVEL1TICKS;
            case 2: return LEVEL2TICKS;
            case 3: return LEVEL3TICKS;
        }

        return 0;
    }

    public void actionArm() {

        double magnitude = -opMode.gamepad2.right_stick_y * SPEED;

        if (magneticSwitch.isPressed()) {
            if (-opMode.gamepad2.right_stick_y >= 0)
            {motor.setPower(0);
                opMode.telemetry.addLine("Limit");}
            else {
                motor.setPower(magnitude);
            }
        }
        else motor.setPower(magnitude);
    }

    /**
     * Pozitionare arm pe level
     * Are grija ca sa opreasca motorul cand a ajuns la pozitia corecta
     */
    public void goLevel(int level) {


        motor.setTargetPosition(getPositionByLevel(level));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.6);

        while (motor.isBusy()) {
            opMode.telemetry.addData("Ticks:", motor.getCurrentPosition());
            opMode.telemetry.update();
        }

    }
    public void primitive() {
        opMode.telemetry.addData("Switch State", magneticSwitch.isPressed());
        opMode.telemetry.addData("Arm Ticks", motor.getCurrentPosition());
        double power = -opMode.gamepad2.right_stick_y * SPEED;
        motor.setPower(power);
    }


}
