package modules.odometry.encoders;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import modules.configuration.odometry.OdometryConstants;

/**
 *    Clasa pentru Encodere inspirată din RoadRunner și GM0 .
 *    Clasa ia în considerare și Integer Overflow-ul Encoderelor #Velocity.
 *
 * */

public class Encoder extends DcMotorImplEx {

    private final static int CPS_STEP = 0x10000; // 16 biți + 1 bit
    private double MULTIPLIER;  // Calibrează valorile
    private int DIRECTION;      // Coeficientul de Direcție

    // Poziție
    private int lastPosition;
    private int deltaPosition;

    // Viteza
    private double[] velocityEstimates;
    private int velocityEstimateIdx;
    private double lastUpdateTime;


    public Encoder(DcMotorEx encoder, double multiplier, DcMotorSimple.Direction direction){

        super(encoder.getController(), encoder.getPortNumber());

        this.MULTIPLIER = multiplier;
        this.DIRECTION  = (direction == Direction.FORWARD) ? 1 : -1;

        setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        this.lastPosition    = 0;
        this.deltaPosition   = 0;

        // Integer Overflow
        this.velocityEstimates = new double[3];
        this.lastUpdateTime = System.currentTimeMillis() / 1000.0;
    }

    public double getDeltaDistance() {

        return deltaPosition * OdometryConstants.TICKS_TO_CM * MULTIPLIER;
    }

    public double getGlobalDistance() {

        return lastPosition * OdometryConstants.TICKS_TO_CM * MULTIPLIER;
    }

    /**
     * Gets the position from the underlying motor and adjusts for the set direction.
     * Additionally, this method updates the velocity estimates used for compensated velocity
     *
     * @return encoder position
     */
    public void updateValues() {

        int currentPosition = getCurrentPosition() *  DIRECTION;

        if (currentPosition != lastPosition) {

            double currentTime = System.currentTimeMillis() / 1000.0;

            double dt = currentTime - lastUpdateTime;

            deltaPosition = currentPosition - lastPosition;

            velocityEstimates[velocityEstimateIdx] = deltaPosition / dt;

            velocityEstimateIdx = (velocityEstimateIdx + 1) % 3;

            lastPosition   = currentPosition;

            lastUpdateTime = currentTime;

        } else deltaPosition = 0;

    }

    /**
     * Gets the velocity directly from the underlying motor and compensates for the direction
     * See {@link #getCorrectedVelocity} for high (>2^15) counts per second velocities (such as on REV Through Bore)
     *
     * @return raw velocity
     */
    public double getRawVelocity() {
        return getVelocity() * DIRECTION;
    }

    /**
     * Uses velocity estimates gathered in {@link #getCurrentPosition} to estimate the upper bits of velocity
     * that are lost in overflow due to velocity being transmitted as 16 bits.
     * CAVEAT: must regularly call {@link #getCurrentPosition} for the compensation to work correctly.
     *
     * @return corrected velocity
     */
    public double getCorrectedVelocity() {
        double median = velocityEstimates[0] > velocityEstimates[1]
                ? Math.max(velocityEstimates[1], Math.min(velocityEstimates[0], velocityEstimates[2]))
                : Math.max(velocityEstimates[0], Math.min(velocityEstimates[1], velocityEstimates[2]));
        return inverseOverflow(getRawVelocity(), median);
    }

    /**
     *  Finding the Correct Value for Velocity - Integer Overflow => Inverse Overflow
     *
     * @return  corrected inversed overflow velocity
     * */
    private static double inverseOverflow(double input, double estimate) {

        // Convert to UInt16
        int real = (int) input & 0xffff;

        // Initial, modulo-based correction: it can recover the remainder of 5 of the upper 16 bits
        // because the velocity is always a multiple of 20 cps due to Expansion Hub's 50ms measurement window
        real += ((real % 20) / 4) * CPS_STEP;

        // estimate-based correction: it finds the nearest multiple of 5 to correct the upper bits by
        real += Math.round((estimate - real) / (5 * CPS_STEP)) * 5 * CPS_STEP;

        return real;
    }

}
