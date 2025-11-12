package com.bank.models;

import java.util.ArrayList;
import java.util.Map;

public class Employee {
    public enum Area {
        INDOOR, OUTDOOR
    }

    public enum Type {
        CASH, SERVICE
    }

    private final Area area;
    private final Type type;

    private ArrayList<Range> serviceTimeRanges;
    private Map<Integer, Double> serviceTimeProbabilities;

    public Employee(Area area, Type type) {
        this.area = area;
        this.type = type;
    }

    public Employee(Area area, Type type, Map<Integer, Double> serviceTimeProbabilities) {
        this(area, type);
        this.serviceTimeProbabilities = serviceTimeProbabilities;
        updateServiceTimeRanges();
    }

    public Area getArea() {
        return area;
    }

    public Type getType() {
        return type;
    }

    public Map<Integer, Double> getServiceTimeProbabilities() {
        return serviceTimeProbabilities;
    }

    public void setServiceTimeProbabilities(Map<Integer, Double> serviceTimeProbabilities) {
        this.serviceTimeProbabilities = serviceTimeProbabilities;
        updateServiceTimeRanges();
    }

    private void updateServiceTimeRanges() {
        serviceTimeRanges = new ArrayList<>();
        double lastProbability = 0.0;
        for (var entry : serviceTimeProbabilities.entrySet()) {
            // No need to add onto the last probability because it'll be avoided due to findFirst being used
            serviceTimeRanges.add(new Range(lastProbability , lastProbability + entry.getValue(), entry.getKey()));
            lastProbability += entry.getValue();
        }

        if (lastProbability > 1)
            throw new ArithmeticException("Service time probabilities are out of range");
    }

    public double getServiceTime(double probability) {
        return serviceTimeRanges.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }
}
