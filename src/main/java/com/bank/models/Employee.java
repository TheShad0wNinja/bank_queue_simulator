package com.bank.models;

import java.util.ArrayList;

public class Employee {
    public enum Area {
        INDOOR, OUTDOOR
    }

    public enum Type {
        CASH, SERVICE
    }

    private final Area area;
    private final Type type;

    private ArrayList<Range> serviceTimeProbabilities;

    public Employee(Area area, Type type) {
        this.area = area;
        this.type = type;
    }

    public Employee(Area area, Type type, ArrayList<Range> serviceTimeProbabilities) {
        this(area, type);
        this.serviceTimeProbabilities = serviceTimeProbabilities;
    }

    public Area getArea() {
        return area;
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Range> getServiceTimeProbabilities() {
        return serviceTimeProbabilities;
    }

    public void setServiceTimeProbabilities(ArrayList<Range> serviceTimeProbabilities) {
        this.serviceTimeProbabilities = serviceTimeProbabilities;
    }

    public double getServiceTime(double probability) {
        return serviceTimeProbabilities.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }
}
