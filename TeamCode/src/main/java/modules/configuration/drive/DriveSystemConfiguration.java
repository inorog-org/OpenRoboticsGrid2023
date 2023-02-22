package modules.configuration.drive;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.function.Function;

import modules.configuration.drive.modes.BrakingMode;
import modules.configuration.drive.modes.CentricMode;
import modules.configuration.drive.modes.DriveMode;
import modules.configuration.drive.modes.MecanumWheelsConfiguration;
import modules.configuration.drive.modes.PowerMode;
import modules.configuration.drive.modes.RealignMode;

public class DriveSystemConfiguration {

    /// === Heading Settings for Mecanum Drive System === ///
    public static DcMotor.Direction directionMotors = DcMotor.Direction.FORWARD;

    /// === Centric Mode for Controlling Robot === ///
    public static CentricMode centricMode = CentricMode.ROBOT_CENTRIC;

    /// === Centric Mode for Controlling Robot from Triggers === ///
    public static CentricMode centricModeSpin = CentricMode.ROBOT_CENTRIC;

    /// === Mecanum Configuration - the Rollers can be Oriented IN or OUT === ///
    public static MecanumWheelsConfiguration mecanumWheelsConfiguration = MecanumWheelsConfiguration.IN_ORIENTED_CONFIG;

    /// === Brake Configuration === ///
    public static BrakingMode brakeConfiguration = BrakingMode.BRAKE;

    /// === Mecanum Max Power Mode === ///
    public static PowerMode powerMode = PowerMode.MAXIMUM;

    /// === Realign Mode === ///
    public static RealignMode realignMode = RealignMode.WITH_MOVEMENT;

    /// === Drive Mode === ///
    public static DriveMode driveMode = DriveMode.NORMAL;

    /// === Magnitude Alteration === ///
    public static final Function<Double, Double> alterMovementMagnitude = (x) -> x;

    public static final Function<Double, Double> alterRotationMagnitude = (x) -> x;

}
