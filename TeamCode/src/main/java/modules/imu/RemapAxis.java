package modules.imu;

import androidx.annotation.NonNull;

public interface RemapAxis {

    // Axis for Remap
    enum Axis{
        X(0x00),
        Y(0x01),
        Z(0x10),
        INVALID(0x11);

        public final int value;

        Axis(int value){
            this.value = value;
        }
    }

    // Sign for Maping Axis
    enum Sign {
        POSITIVE(0),
        NEGATIVE(1);

        public final int sign;

        Sign(int sign){
            this.sign = sign;
        }
    }

    void remapAxis(@NonNull Axis xAxis, @NonNull Axis yAxis, @NonNull Axis zAxis, @NonNull Sign xSign, @NonNull Sign ySign, @NonNull Sign zSign);

    void remapAxis(int AXIS_MAP_CONFIG_BYTE, int AXIS_MAP_SIGN_BYTE);
}
