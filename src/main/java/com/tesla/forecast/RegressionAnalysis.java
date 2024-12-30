package com.tesla.forecast;

import com.tesla.ds.MyArrayList;
import com.tesla.models.Dataset;

public class RegressionAnalysis {
    private static double MSE;
    private static int maxForecastedSales;
    private static int minForecastedSales;
    private static MyArrayList<Integer> forecastedList;
    private static int numOfRecs;
    private static double[] seasonalIndices; // Array to store seasonal indices

    public static void initializeForecast(int numOfRecordsForEachMonth, Dataset dataset) {
        numOfRecs = numOfRecordsForEachMonth;

        MyArrayList<Integer> arr = dataset.getListOfRecords();
        MyArrayList<Integer> frr = new MyArrayList<>();
        double regressionAnalysis;
        double month = arr.size();
        double rega;
        double regb;

        double calcOfTime = 0; // x
        double calcOfSales = 0; // y
        double calcOfTimePower = 0; // x^2
        double calcOfMxS = 0; // x.y

        double averageOfSales;
        double averageTime;

        // Compute the sum of x, y, x^2, and x*y
        for (int i = 1; i < month + 1; i++) {
            calcOfTime += i;
            calcOfSales += arr.get(i - 1);
            calcOfTimePower += i * i;
            calcOfMxS += (i * (arr.get(i - 1)));
        }

        averageOfSales = calcOfSales / month; // ~y
        averageTime = calcOfTime / month; // ~x
        regb = ((month * calcOfMxS) - (calcOfTime * calcOfSales)) /
                ((month * calcOfTimePower) - (calcOfTime * calcOfTime));
        rega = averageOfSales - regb * averageTime;

        // Calculate seasonal indices
        seasonalIndices = new double[12]; // Assuming monthly data

        // Calculate the average sales for each month and seasonal index
        double[] monthlySums = new double[12];
        int[] monthCounts = new int[12];

        for (int i = 0; i < month; i++) {
            int monthIndex = (i % 12); // Determine the month index (0 to 11)
            monthlySums[monthIndex] += arr.get(i);
            monthCounts[monthIndex]++;
        }

        for (int i = 0; i < 12; i++) {
            if (monthCounts[i] > 0) {
                seasonalIndices[i] = (monthlySums[i] / monthCounts[i]) / averageOfSales;
            }
        }

        System.out.println("Regression Coefficients: rega = " + rega + ", regb = " + regb);
        System.out.println("Seasonal Indices: ");
        for (int i = 0; i < seasonalIndices.length; i++) {
            System.out.println("Month " + (i + 1) + ": " + seasonalIndices[i]);
        }

        // Regression process with seasonal adjustments
        for (int i = 25; i < month + 25; i++) {
            int monthIndex = (i - 1) % 12; // Determine the month index for forecasting
            regressionAnalysis = rega + (regb * i);
            regressionAnalysis *= seasonalIndices[monthIndex]; // Adjust for seasonality
            frr.add((int) regressionAnalysis);
        }

        MSE = ForecastUtil.calcMSE(dataset, frr);
        minForecastedSales = ForecastUtil.getMinForecastedSales(frr);
        maxForecastedSales = ForecastUtil.getMaxForecastedSales(frr);
        forecastedList = frr;

        System.out.println("Forecasted Sales: " + frr);
    }

    public static double getMSE() {
        return MSE;
    }

    public static int getMaxForecastedSales() {
        return maxForecastedSales;
    }

    public static int getMinForecastedSales() {
        return minForecastedSales;
    }

    public static MyArrayList<Integer> getForecastedList() {
        return forecastedList;
    }

    public static int getNumOfRecs() {
        return numOfRecs;
    }
}
