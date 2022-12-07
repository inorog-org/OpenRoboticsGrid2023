package modules.odometry.encoders;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.internal.android.dex.util.ExceptionWithContext;

import modules.odometry.Position;
import modules.odometry.encoders.Encoder;

public class OdometryEncoders {

    // --- External Encoders --- //
    public final Encoder leftEncoder;
    public final Encoder rightEncoder;
    public final Encoder centralEncoder;

    // --- Distance from Robot Center --- //
    public static final double leftLength    = 17.5;  // 17.5 cm
    public static final double rightLength   = 17.5;  // 17.5 cm
    public static final double headingLength = 17.25; // 17.25 cm

    // --- Constructors --- //
    public OdometryEncoders(DcMotorEx leftEncoder, DcMotorEx rightEncoder, @NonNull DcMotorEx centralEncoder) throws EncodersExceptions {

        this.leftEncoder    = (leftEncoder  != null) ? new Encoder(leftEncoder)  : null;
        this.rightEncoder   = (rightEncoder != null) ? new Encoder(rightEncoder) : null;

        if(leftEncoder == null && rightEncoder == null) throw new EncodersExceptions();

        this.centralEncoder = new Encoder(centralEncoder);
    }

      // Update Encoder Values
    public void updateEncodersValues() {

        if(leftEncoder  != null) leftEncoder.updateValues();
        if(rightEncoder != null) rightEncoder.updateValues();
        centralEncoder.updateValues();
    }
}
