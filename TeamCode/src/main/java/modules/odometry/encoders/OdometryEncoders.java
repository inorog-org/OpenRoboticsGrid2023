package modules.odometry.encoders;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.odometry.Heading;

public class OdometryEncoders {

    // --- External Encoders --- //
    public final Encoder leftEncoder;
    public final Encoder rightEncoder;
    public final Encoder centralEncoder;

    // --- Distance from Robot Center --- //
    public static final double leftLength    = 17.5;  // 17.5 cm
    public static final double rightLength   = 17.5;  // 17.5 cm
    public static final double centralLength = 17.25; // 17.25 cm

    // --- Constructors --- //
    public OdometryEncoders(DcMotorEx leftEncoder, DcMotorEx rightEncoder, @NonNull DcMotorEx centralEncoder, Heading heading) throws EncodersExceptions {

     this.centralEncoder = new Encoder(centralEncoder); // Encoderul Central va fi obligatoriu dacă dorim Odometrie cu Encodere

     if (heading == null) {  // Cazul in care nu avem deja IMU sau HeadingEncoders-urile inițiate

         if(leftEncoder == null || rightEncoder == null) // Verificăm dacă avem ambele encodere laterale
             throw new EncodersExceptions();

         this.leftEncoder    = new Encoder(leftEncoder);   // Init Left Encoder
         this.rightEncoder   = new Encoder(rightEncoder);  // Init Right Encoder

         heading = new HeadingEncoders(this.leftEncoder, this.rightEncoder); // Init Heading by Encoders

     } else {  // Cazul in care avem deja IMU sau HeadingEncoders-urile inițiate

         if(leftEncoder == null && rightEncoder == null)   // Cazul în care nu avem nici un encoder lateral și nu putem inițializa nici o Odometrie
             throw new EncodersExceptions();

         // Cazul in care avem cel puțin un Encoder + Heading
         if(leftEncoder   != null)  this.leftEncoder    = new Encoder(leftEncoder);  // Init Left Encoder
         else this.leftEncoder = null;
         if(rightEncoder  != null)  this.rightEncoder   = new Encoder(rightEncoder); // Init Right Encoder
         else this.rightEncoder = null;
     }

    }

    public OdometryEncoders(Encoder leftEncoder, Encoder rightEncoder, @NonNull Encoder centralEncoder, Heading heading) throws EncodersExceptions {

        this.centralEncoder = centralEncoder; // Encoderul Central va fi obligatoriu dacă dorim Odometrie cu Encodere

        if (heading == null) {  // Cazul in care nu avem deja IMU sau HeadingEncoders-urile inițiate

            if(leftEncoder == null || rightEncoder == null) // Verificăm dacă avem ambele encodere laterale
                throw new EncodersExceptions();

            this.leftEncoder    = leftEncoder;   // Init Left Encoder
            this.rightEncoder   = rightEncoder;  // Init Right Encoder

            heading = new HeadingEncoders(this.leftEncoder, this.rightEncoder); // Init Heading by Encoders

        } else {  // Cazul in care avem deja IMU sau HeadingEncoders-urile inițiate

            if(leftEncoder == null && rightEncoder == null)   // Cazul în care nu avem nici un encoder lateral și nu putem inițializa nici o Odometrie
                throw new EncodersExceptions();

            // Cazul in care avem cel puțin un Encoder + Heading
            this.leftEncoder    = leftEncoder;  // Init Left Encoder
            this.rightEncoder   = rightEncoder; // Init Right Encoder
        }
    }

    // getDeltaLateral
    public double getDeltaLateral() {

        if(rightEncoder != null)
            return rightEncoder.getDeltaDistance();

        if (leftEncoder != null)
            return leftEncoder.getDeltaDistance();

        return 0;
    }

    public double getLateralRadius(double deltaTheta) {
        if(rightEncoder != null)
            return getDeltaLateral() / deltaTheta + OdometryEncoders.rightLength;

        if (leftEncoder != null)
            return getDeltaLateral() / deltaTheta - OdometryEncoders.leftLength;

        return 0;
    }

    public double getDeltaDistance(double deltaTheta) {
        if(rightEncoder != null && leftEncoder != null)
            return (leftEncoder.getDeltaDistance() * rightLength + rightEncoder.getDeltaDistance() * leftLength) / (leftLength + rightLength);

        if(rightEncoder != null)
            return rightEncoder.getDeltaDistance() - rightLength * deltaTheta;

        if(leftEncoder  != null)
            return leftEncoder.getDeltaDistance() + leftLength * deltaTheta;

        return 0;
    }

    // Update Encoder Values
    public void updateEncodersValues() {

        centralEncoder.updateValues();

        if(leftEncoder  != null) leftEncoder.updateValues();
        if(rightEncoder != null) rightEncoder.updateValues();
    }
}
