package modules.configuration.drive.alteration;


    public class EaseFunctions {

        public static double easeInSine(double x) {

            return 1 - Math.cos((x * Math.PI) / 2);
        }

        public static double easeOutSine(double x) {

            return Math.sin((x * Math.PI) / 2);
        }

        public static double easeInOutSine(double x) {

            return -(Math.cos(Math.PI * x) - 1) / 2;
        }

        public static double easeInQuad(double x) {

            return x * x;
        }

        public static double easeOutQuad(double x) {

            return 1 - (1 - x) * (1 - x);
        }

        public static double easeInOutQuad(double x){

            return x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2;
        }

        public static double easeInCubic(double x) {

            return x * x * x;
        }

        public static double easeOutCubic(double x) {

            return 1 - Math.pow(1 - x, 3);
        }

        public static double easeInOutCubic(double x) {

            return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
        }

        public static double easeInQuart(double x) {

            return x * x * x * x;
        }

        public static double easeOutQuart(double x) {

            return 1 - Math.pow(1 - x, 4);
        }

        public static double easeInOutQuart(double x) {

            return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
        }

        public static double easeInQuint(double x) {

            return x * x * x * x * x;
        }

        public static double easeOutQuint(double x) {

            return 1 - Math.pow(1 - x, 5);
        }

        public static double easeInOutQuint(double x) {

            return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
        }

        public static double easeInExpo(double x) {

            return x == 0 ? 0 : Math.pow(2, 10 * x - 10);
        }

        public static double easeOutExpo(double x) {

            return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
        }

        public static double easeInOutExpo(double x) {

            return x == 0
                    ? 0
                    : x == 1
                    ? 1
                    : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2
                    : (2 - Math.pow(2, -20 * x + 10)) / 2;
        }

        public static double easeInCirc(double x) {

            return 1 - Math.sqrt(1 - Math.pow(x, 2));
        }

        public static double easeOutCirc(double x) {

            return Math.sqrt(1 - Math.pow(x - 1, 2));
        }

        public static double easeInOutCirc(double x) {

            return x < 0.5
                    ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                    : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
        }
}
