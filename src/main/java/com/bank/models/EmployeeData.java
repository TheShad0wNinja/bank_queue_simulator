package com.bank.models;

import java.util.ArrayList;
import java.util.Map;

public class EmployeeData {
    public enum Area {
        INDOOR, OUTDOOR
    }

    private final Area area;
    private final ServiceType type;
    private final String id;

    private ArrayList<Range> serviceTimeRanges;
    private Map<Integer, Double> serviceTimeProbabilities;

    public EmployeeData(Area area, ServiceType type, String id) {
        this.area = area;
        this.type = type;
        this.id = id;
    }

    public EmployeeData(Area area, ServiceType type, String id, Map<Integer, Double> serviceTimeProbabilities) {
        this(area, type, id);
        this.serviceTimeProbabilities = serviceTimeProbabilities;
        updateServiceTimeRanges();
    }

    public Area getArea() {
        return area;
    }

    public ServiceType getType() {
        return type;
    }

    public String getId() {
        return id;
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

    public int getServiceTime(double probability) {
        return serviceTimeRanges.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }

    @Override
    public String toString() {
        return this.id;
    }
}
