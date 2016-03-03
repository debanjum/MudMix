package edu.dartmouth.cs.armudwear.UI;

import com.androidplot.xy.XYSeries;

public class StatsSeries implements XYSeries {
    private double value;
    private double radius;
    private int SAMPLE_LIMIT = 30;
    double baseAngle = Math.PI * 4 / 3;
    double maxAngleDelta = Math.PI * 5 / 3;
    double maxValue;

    public StatsSeries(double initialValue, double maxValue, double radius) {
        this.value = initialValue;
        this.radius = radius;
        this.maxValue = maxValue;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int size() {
        return SAMPLE_LIMIT;
    }

    @Override
    public Number getX(int index) {
        double theta = baseAngle - ((double) index * value * maxAngleDelta) / (maxValue * (double) SAMPLE_LIMIT);
        return radius * Math.cos(theta);
    }

    @Override
    public Number getY(int index) {
        double theta = baseAngle  - ((double) index * value * maxAngleDelta) / (maxValue * (double) SAMPLE_LIMIT);
        return radius * Math.sin(theta);
    }

    public void updateValue(int updatedValue){
        this.value = updatedValue;
    }
    public void updateMaxValue(int updatedMax) {
        this.maxValue = updatedMax;
    }
}