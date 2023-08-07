package modules.drive.subsystems.utils;

import com.qualcomm.robotcore.util.Range;
import com.sun.tools.javac.util.StringUtils;

import java.util.Arrays;
import java.util.StringJoiner;

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

        StringBuilder loadingBar = new StringBuilder();

        percent = Range.clip(value.doubleValue() / maxValue.doubleValue(), 0.0, 1.0);

        if(percent != 0) {

            double blockPercent = percent * barLength;

            int bigBlackBlocks = (int) blockPercent;

            for(int i = 1; i <= bigBlackBlocks; i++)
                loadingBar.append(blockSymbol);

            int bigWhiteSpaces = barLength - bigBlackBlocks - 1;

            if (bigWhiteSpaces != -1) {

                double fractionBlockPercent = blockPercent - bigBlackBlocks;

                int fractionBlockIndex = (int) (fractionBlockPercent * 8);

                loadingBar.append(fractionSymbols.charAt(fractionBlockIndex));

                for(int i = 1; i <= bigWhiteSpaces; i++)
                    loadingBar.append("\u2800");
            }
        }

        return loadingBar.toString();
    }}
