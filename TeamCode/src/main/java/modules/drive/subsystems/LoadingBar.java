package modules.drive.subsystems;

import com.qualcomm.robotcore.util.Range;

public class LoadingBar<V extends Number> {

    private final V maxValue;

    private final int barLength;

    private final String fractionSymbols = "\u2800▏▎▍▌▋▊▉";

    private final String blockSymbol = "█";

    public double percent;

    public LoadingBar(V maxValue, int barCharLength) {

        this.maxValue = maxValue;
        this.barLength = barCharLength;
    }

    public String getASCIIBar(V value) {

        String loadingBar = "";

        percent = Range.clip(value.doubleValue() / maxValue.doubleValue(), 0.0, 1.0);

        if(percent != 0) {

            double blockPercent = percent * barLength;

            int bigBlackBlocks = (int) blockPercent;

            loadingBar += new String(new char[bigBlackBlocks]).replace("\0", blockSymbol);


            int bigWhiteSpaces = barLength - bigBlackBlocks - 1;

            if (bigWhiteSpaces != -1) {

                double fractionBlockPercent = blockPercent - bigBlackBlocks;

                int fractionBlockIndex = (int) (fractionBlockPercent * 8);

                loadingBar += fractionSymbols.charAt(fractionBlockIndex) + new String(new char[bigWhiteSpaces]).replace("\0", "\u2800");
            }
        }

        return loadingBar;
    }}
