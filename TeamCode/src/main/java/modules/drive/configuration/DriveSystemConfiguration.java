package modules.drive.configuration;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.function.Function;

import modules.drive.configuration.modes.BrakingMode;
import modules.drive.configuration.modes.CentricMode;
import modules.drive.configuration.modes.DriveMode;
import modules.drive.configuration.modes.MecanumWheelsConfiguration;
import modules.drive.configuration.modes.PowerMode;
import modules.drive.configuration.modes.RealignMode;

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
