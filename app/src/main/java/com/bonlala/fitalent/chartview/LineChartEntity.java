package com.bonlala.fitalent.chartview;

public class LineChartEntity {
    public String xLabel;
    public Float yValue;
    public int corlors;
    public int index;
    public int invert;


    public LineChartEntity(String xLabel, Float yValue) {
        this.xLabel = xLabel;
        this.yValue = yValue;
    }

    public LineChartEntity(int position, int invert, Float yValue) {
        this.index = position;
        this.yValue = yValue;
        this.invert= invert;
    }

    public LineChartEntity(String xLabel, Float yValue, int corlors) {
        this.xLabel = xLabel;
        this.yValue = yValue;
        this.corlors = corlors;
    }

    @Override
    public String toString() {
        return "LineChartEntity{" +
                "xLabel='" + xLabel + '\'' +
                ", yValue=" + yValue +
                ", corlors=" + corlors +
                ", index=" + index +
                ", invert=" + invert +
                '}';
    }
}


