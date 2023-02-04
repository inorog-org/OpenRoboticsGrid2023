package modules.drive.subsystems.utils;

import java.util.LinkedList;
import java.util.Queue;

public class MedianValue {

    private final double THRESHOLD = 0.01;

    private double sum = 0;

    private int length;

    private Queue<Double> queue;

    private final Queue<Double> zeroQueue;

    public MedianValue(int number) {

        this.length = number;

        queue     = new LinkedList<>();

        zeroQueue = new LinkedList<>();

        initZeroQueue();
    }

    private void initZeroQueue(){

        for(int i = 1 ; i <= length ; i++)
            zeroQueue.add(0.0);

        queue = zeroQueue;
    }

    public double getMedian(double number) {

        number = (number > THRESHOLD) ? number : 0;

        sum += number - queue.element();

        queue.remove();
        queue.add(number);

        return (sum / length > THRESHOLD) ? sum / length : 0;
    }

    public double getLastMedian() {

        return sum / length;
    }

    public void reset(){
        sum = 0;
        queue = zeroQueue;
    }

}