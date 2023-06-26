package me.EvVlF;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;

public class Tolerance {
    private double size;
    private String quality;
    private int deviation;
    private double upperTolerance;
    private double averageTolerance;
    private double lowerTolerance;
    private double minRangeTableSize;
    private double maxRangeTableSize;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###");

    static final class ToleranceSingletonHolder {
        public static final Tolerance TOLERANCE_INSTANCE = new Tolerance();
    }

    private Tolerance() {
        initializeDecimalFormat();
    }

    static Tolerance getInstance() {
        return ToleranceSingletonHolder.TOLERANCE_INSTANCE;
    }

    void splitAndSetUserInputOnSizeQualityDeviation(Matcher matcher) {
        matcher.matches();
        setSize(Double.parseDouble(matcher.group("size")));
        setQuality(matcher.group("quality"));
        setDeviation(Integer.parseInt(matcher.group("deviation")));
    }

    private void printValue(String displayText, double value) {
        System.out.println(String.format(displayText + "%s", decimalFormat.format(value)));
    }

    void printAllValues() {
        calculateAverageTolerance();
        printValue("Верхнее отклонение, мкм: ", getUpperTolerance());
        printValue("Среднее отклонение, мкм: ", getAverageTolerance());
        printValue("Нижнее отклонение, мкм: ", getLowerTolerance());
        printValue("Верхний размер, мм: ", calculateOneOfSize(getSize(), getUpperTolerance()));
        printValue("Средний размер, мм: ", calculateOneOfSize(getSize(), getAverageTolerance()));
        printValue("Нижний размер, мм: ", calculateOneOfSize(getSize(), getLowerTolerance()));
    }

    private void initializeDecimalFormat() {
        DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols();
        decimalFormatSymbol.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbol);
    }

    private void setQuality(String quality) {
        this.quality = quality;
    }

    String getQuality() {
        return quality;
    }

    private void setDeviation(int deviation) {
        this.deviation = deviation;
    }

    int getDeviation() {
        return deviation;
    }

    private void setSize(double size) {
        this.size = size;
    }

    double getSize() {
        return size;
    }

    void setUpperTolerance(double upperTolerance) {
        this.upperTolerance = upperTolerance;
    }

    private double getUpperTolerance() {
        return upperTolerance;
    }

    private void calculateAverageTolerance() {
        if (upperTolerance > lowerTolerance) {
            averageTolerance = (upperTolerance - lowerTolerance) * 0.5 + lowerTolerance;
        } else {
            throw new IllegalArgumentException("Верхнее отклонение меньше нижнего");
        }
    }

    private double getAverageTolerance() {
        return averageTolerance;
    }

    void setLowerTolerance(double lowerTolerance) {
        this.lowerTolerance = lowerTolerance;
    }

    private double getLowerTolerance() {
        return lowerTolerance;
    }

    void setMinRangeTableSize(double minRangeTableSize) {
        this.minRangeTableSize = minRangeTableSize;
    }

    void setMaxRangeTableSize(double maxRangeTableSize) {
        this.maxRangeTableSize = maxRangeTableSize;
    }

    private double calculateOneOfSize(double oneOfSize, double tolerance) {
        return oneOfSize + tolerance * 0.001;
    }
}