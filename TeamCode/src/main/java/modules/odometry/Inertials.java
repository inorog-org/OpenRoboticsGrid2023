package modules.odometry;


// Velocity - cm/s
// Acceleration - cm/s^2
public class Inertials {

   private double value = 0;
   private double velocity = 0;
   private double acceleration = 0;
   private long timeFrame;

   public Inertials() {

      timeFrame = System.currentTimeMillis();
   }

   public void updateInertials(double newValue) {

      long readingTime = System.currentTimeMillis();
      long deltaTime   = (readingTime - timeFrame) * 1000;

      timeFrame = readingTime;

      double currentVelocity = (newValue - value) / deltaTime;

      acceleration = (currentVelocity - velocity) / deltaTime;
      velocity = currentVelocity;
   }

   public double getVelocity() {
       return velocity;
   }

   public double getAcceleration() {
      return acceleration;
   }

}
