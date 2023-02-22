package modules.drive;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import modules.configuration.drive.modes.ControlMode;
import modules.drive.main.Motors;
import modules.drive.subsystems.kinematic.KinematicBase;
import modules.drive.subsystems.pursuit.PurePursuit;
import modules.drive.subsystems.teleop.Drivebase;
import modules.odometry.Heading;
import modules.odometry.OdometryHandler;

public class MecanumHandler {

  private LinearOpMode opMode;

  private Motors motors;
  private Heading heading;

  private Drivebase drivebase;
  private KinematicBase kinematicBase;
  private PurePursuit purePursuit;

  @RequiresApi(api = Build.VERSION_CODES.N)
  public MecanumHandler(LinearOpMode opMode, Heading heading, ControlMode controlMode) {

      this.opMode = opMode;

      this.motors  = new Motors(opMode.hardwareMap);
      this.heading = heading;

      initControlMode(controlMode);

      motors.stopPower();
  }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initControlMode(ControlMode controlMode){
        switch (controlMode){
            case DRIVE:
                this.drivebase = new Drivebase(opMode, heading);
                break;
            case AUTONOMOUS_KINEMATIC:
                this.kinematicBase = new KinematicBase(motors, opMode, heading);
            case AUTONOMOUS_PURSUIT:
                purePursuit = new PurePursuit(opMode, motors, new OdometryHandler(opMode, heading));
                break;
        }
    }

    private void mecanumControlledNotInit(){
        opMode.telemetry.clearAll();
        opMode.telemetry.addLine("Mecanum Controlled Drive System was not initiated!");
        opMode.telemetry.update();
    }

    private void mecanumAutonomousNotInit(){
        opMode.telemetry.clearAll();
        opMode.telemetry.addLine("Mecanum Autonomous Drive System was not initiated!");
        opMode.telemetry.update();
    }

    /// === Controlled Period === ///

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void control(){
        if(drivebase != null)
            drivebase.control();
        else mecanumControlledNotInit();
    }

    /// === Autonomous Period === ///
// 0-Fata   90-Stanga  180-Spate   270-Dreapta
    public void setMovement2D(double distance, double angle, double speed){
        if(kinematicBase != null)
            kinematicBase.setMovement2D(distance, angle, speed);
        else mecanumAutonomousNotInit();
    }

    public void setRotationFromCurrentHeading(double angle ,double speed){
        if(kinematicBase != null)
            kinematicBase.setRotationFromCurrentHeading(angle, speed);
        else mecanumAutonomousNotInit();
    }

    public void setRotationFromStartHeading(double angle ,double speed){
        if(kinematicBase != null)
            kinematicBase.setRotationFromStartHeading(angle, speed);
        else mecanumAutonomousNotInit();
    }

    /// === Telemtry for Motors === ///

    public void telemetryTeleOpMotors(){

        opMode.telemetry.addData("FL ", motors.frontLeft.getPower());
        opMode.telemetry.addData("FR ", motors.frontRight.getPower());
        opMode.telemetry.addData("BL ", motors.backLeft.getPower());
        opMode.telemetry.addData("BR ", motors.backRight.getPower());
    }

}
