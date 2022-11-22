package modules.imu;

import androidx.annotation.NonNull;

public interface RemapAxis {

    // Axis for Remap
    enum Axis{
        X(0x0),
        Y(0x1),
        Z(0x2),
        INVALID(0x3);

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

    /**
     *  Remaparea axelor folosing înlcouirea axelor și a sensurilor în mod direct
     *
     * @param xAxis - Axa X de pe ControlHub devinde axa xAxis
     * @param yAxis - Axa Y de pe ControlHub devinde axa yAxis
     * @param zAxis - Axa Z de pe ControlHub devinde axa zAxis
     *
     * @param xSign - Schimbarea semnului axei X
     * @param ySign - Schimbarea semnului axei Y
     * @param zSign - Schimbarea semnului axei X
     * */
    void remapAxis(@NonNull Axis xAxis, @NonNull Axis yAxis, @NonNull Axis zAxis, @NonNull Sign xSign, @NonNull Sign ySign, @NonNull Sign zSign);

    /**
     *  Configurarea axelor folosing modificarea biților în mod direct
     *
     * @param AXIS_MAP_CONFIG_BYTE - byte-ul pentru remaparea axelor XYZ
     * @param AXIS_MAP_SIGN_BYTE   - byte-ul pentru schimbarea sensurilor axelor XYZ
     * */
    void remapAxis(int AXIS_MAP_CONFIG_BYTE, int AXIS_MAP_SIGN_BYTE);
}
