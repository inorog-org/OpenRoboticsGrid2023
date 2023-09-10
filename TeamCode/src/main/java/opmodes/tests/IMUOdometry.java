package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import modules.imu.IMU;

@TeleOp(name = "Concept: Odoemtry IMU", group = "Concept")
@Disabled
public class IMUOdometry extends LinearOpMode {

    private IMU imu;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);

        Position position = new Position();

        Velocity velocity = new Velocity();

        //imu.startAccelerationIntegration(position, velocity, 250)

        double x = 0 ,y = 0, vx = 0, vy = 0;

        waitForStart();

        double previousTime = getRuntime();

        double n = 0;

        while(opModeIsActive()) {

        //    position = imu.getPosition();

            double ax = imu.getOverallAcceleration().xAccel - 0.010043196321805017;
            double ay = imu.getOverallAcceleration().yAccel + 0.7968898552025369;

            /*
X: 0.010043196321805017
Y: -0.7968898552025369
            * */
/*
            x += ax;
            y += ay;
            n += 1;
*/

            // Read time interval (in seconds) between readings
            double deltaTime = getRuntime() - previousTime;

            // Integrate acceleration to obtain velocity
            vx += ax * deltaTime;
            vy += ay * deltaTime;

            // Integrate velocity to obtain position
            x += vx * deltaTime;
            y += vy * deltaTime;

            // Store current time for next iteration
            previousTime = getRuntime();

            // Display position in telemetry (optional)
            telemetry.addData("X Position", x * 100);
            telemetry.addData("Y Position", y * 100);

           // telemetry.addData("X Acc", ax);
           // telemetry.addData("Y Acc", ay);
            telemetry.update();

       //  telemetry.addData("Position X", position );
   //      telemetry.addData("Overall Acceleration X", imu.getOverallAcceleration().xAccel - 0.010043196321805017);
     //    telemetry.addData("Overall Acceleration Y", imu.getOverallAcceleration().yAccel + 0.7968898552025369);
      //   telemetry.update();
        }

      //  telemetry.addData("X", x / n);
      //  telemetry.addData("Y", y / n);
      //  telemetry.update();

        sleep(4000);
    }
}
