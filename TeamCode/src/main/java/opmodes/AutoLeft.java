package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;


@Autonomous(name = "Autonomia Oficiala Left", group = "Autonomie")
@Disabled
public class AutoLeft extends LinearOpMode {
    public Robot robot;

    private FtcDashboard dashboard;
    @Override
    public void runOpMode() {
        //dashboard = FtcDashboard.getInstance();
        //dashboard.setTelemetryTransmissionInterval(100);
        telemetry = new MultipleTelemetry(telemetry);
        //Telemetry dashboardTelemetry = dashboard.getTelemetry();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    /*    robot = new Robot(this, Robot.ControlMode.AUTONOMOUS);
        Detection.Circles circles = Detection.Circles.UNKNOWN;

        while (opModeInInit()) {
            circles = robot.camera.detect();
            telemetry.addData("numberOfDetection : ", robot.camera.pipeline.numberOfDetection);
            telemetry.update();
            robot.claw.openClawAuto();
            //telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        }

        waitForStart();
        if (opModeIsActive()) {
            robot.claw.closeClaw();
            sleep(250);
            robot.arm.goLevel(1);
            robot.motors.setMovement2D(72, -90, 0.7);  //DREAPTA din pozitia de start

            //Robert este un Raptor.
            robot.motors.setMovement2D(82, 0, 0.7);//FATA-primul con
            robot.motors.setRotationFromStartHeading(0,0.7);//rotire turela -MEDIUM JUNCTION
            robot.arm.goLevel(2);

            //robot.turret.rotateAutonomous(Turret.SensorColor.RED);//Rotate to the Medium Junction
            robot.turret.rotateTime(1350,1);
            //robot.turret.setPowerByTime(0.7,1590);
            sleep(100);
            //robot.turret.gliderAuto();
            //sleep(800);
            robot.claw.openClaw();
            sleep(100);

            //robot.turret.rotateAutonomous(Turret.SensorColor.BLUE);//Rotate to the starting angle
            robot.turret.rotateTime(1350,2);

            //robot.turret.setPowerByTime(0.7,1150);
            robot.arm.goLevel(1);

            robot.motors.setMovement2D(41,0,0.6);//LOCATION 1
            robot.motors.setRotationFromStartHeading(88,0.7);//ROTATION TO TAKE CONE
            robot.motors.setMovement2D(110,0,0.8);//GO CLOSE TO THE CONE
            //robot.motors.setRotationFromStartHeading(93,0.7);//ROTATION TO TAKE CONE
            sleep(100);
            robot.claw.closeClaw();
            sleep(400);
            //ROTATION TO TAKE CONE
            robot.arm.goLevel(3);
            robot.motors.setRotationFromStartHeading(90,0.7);
            sleep(100);
            robot.motors.setMovement2D(87,180,0.8);//GO BACK TO PUT CONE IN HIGH JUNCTION

            //robot.turret.rotateAutonomous(Turret.SensorColor.BROWN);//ROTATE TO PUT CONE
            robot.turret.rotateTime(2015,3);

            //robot.turret.setPowerByTime(0.7,2300);
            robot.claw.openClaw();
            sleep(200);
            switch (circles) {
                case CIRCLES1:
                    telemetry.addData("detection : ", 1);
                    telemetry.update();
                    robot.motors.setMovement2D(82, 0, 0.8);//Positioned in Location 1
                    break;

                case CIRCLES2:
                    telemetry.addData("detection : ", 2);
                    telemetry.update();

                    robot.motors.setMovement2D(30, 0, 0.7);
                    robot.arm.goLevel(0);
                    break;
                case CIRCLES3:
                    telemetry.addData("detection : ", 3);
                    telemetry.update();
                    robot.motors.setMovement2D(27, 180, 0.7);
                    break;
                case UNKNOWN:
                    telemetry.addLine("detection : null");
                    telemetry.update();
                    break;
            }
        }
    }
}*/
    } }