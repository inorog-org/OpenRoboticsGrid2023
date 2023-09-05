package modules.control.pid;

public class PIDFController {

    private double kProportional, kDerivative, kIntegral, kFeedforward, kFilter;

    private double target, currentError, lastError = 0, lastTimeStamp = 0, lastDerivative = 0, integralError = 0, period = 0;

    private double minIntegral = -1.0;
    private double maxIntegral =  1.0;

    private double positionTolerance   = 0.05;
    private double derivativeTolerance = Double.POSITIVE_INFINITY;
    public PIDFController(double kP, double kI, double kD, double kF, double lowPassFilterCoefficient) {

      this.kProportional = kP;
      this.kIntegral     = kI;
      this.kDerivative   = kD;
      this.kFeedforward  = kF;

      this.kFilter = lowPassFilterCoefficient;
    }

    public void setProportionalCoefficient(double kP) {
        this.kProportional = kP;
    }

    public void setIntegralCoefficient(double kI) {
        this.kIntegral = kI;
    }

    public void setDerivativeCoefficient(double kD) {
        this.kDerivative = kD;
    }

    public void setFeedforwardCoefficient(double kF) {
        this.kFeedforward = kF;
    }

    public void setLowPassFilterCoefficient(double lowPassFilterCoefficient) {
        this.kFilter = lowPassFilterCoefficient;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public void setDerivativeTolerance(double derivativeTolerance) {
        this.derivativeTolerance = derivativeTolerance;
    }

    public void setPositionTolerance(double positionTolerance) {
        this.positionTolerance = positionTolerance;
    }

    public void setTolerance(double positionTolerance, double derivativeTolerance) {
        this.positionTolerance   = positionTolerance;
        this.derivativeTolerance = derivativeTolerance;
    }

    public void setIntegralBounds(double minIntegral, double maxIntegral) {
        this.minIntegral = minIntegral;
        this.maxIntegral  = maxIntegral;
    }

    public double compute(double currentValue) {

        // Error
        currentError  = target - currentValue;

        // Derivative
        double currentTimeStamp = System.nanoTime() / 1E9;

        if(lastTimeStamp == 0) lastTimeStamp = currentTimeStamp; // Ca să nu o ia derivative-ul la vale

        period = currentTimeStamp - lastTimeStamp;
        lastTimeStamp = currentTimeStamp;
        double derivative;

        if(Math.abs(period) > 1E-6)   // ignorăm perioadele foarte scurte de timp
             derivative = (currentError - lastError) / period;
        else derivative = 0.0;  // poate fi folosită și pentru cazul timestamp egal cu zero #FIX

        lastError = currentError;

        // Integral

        // return Value
        return kProportional * currentError + kIntegral * integralError + kDerivative * derivative + kFeedforward * target;
    }
}
