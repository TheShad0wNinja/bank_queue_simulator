package com.bank.controllers;

import com.bank.models.Employee;
import com.bank.models.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parameters {
    public static Parameters instance = new Parameters();

    private int outdoorQueueCapacity;
    private int indoorQueueCapacity;
    private double cashCustomerProbability;
    private ArrayList<Range> timeBetweenArrivalProbability;
    private List<Employee> employees;

    private Parameters() {
        resetParamsToDefault();
    }

    public void resetParamsToDefault() {
        outdoorQueueCapacity = 2;
        indoorQueueCapacity = Integer.MAX_VALUE;
        cashCustomerProbability = 0.7;

        timeBetweenArrivalProbability = new ArrayList<>(List.of(
                new Range(0.1, 0.15, 0),
                new Range(0.16, 0.40, 1),
                new Range(0.41, 0.65, 2),
                new Range(0.66, 1, 3)
        ));

        employees = new ArrayList<>();
        Employee outdoorTeller = new Employee(
                Employee.Area.OUTDOOR,
                Employee.Type.CASH,
                new ArrayList<>(List.of(
                        new Range(0.1, 0.20, 2),
                        new Range(0.21, 0.50, 3),
                        new Range(0.51, 1, 4)
                ))
        );
        Employee indoorTeller = new Employee(
                Employee.Area.INDOOR,
                Employee.Type.CASH,
                new ArrayList<>(List.of(
                        new Range(0.1, 0.20, 2),
                        new Range(0.21, 0.50, 3),
                        new Range(0.51, 1, 4)
                ))
        );

        Employee indoorServiceEmployee = new Employee(
                Employee.Area.INDOOR,
                Employee.Type.SERVICE,
                new ArrayList<>(List.of(
                        new Range(0.1, 0.20, 4),
                        new Range(0.21, 0.70, 6),
                        new Range(0.71, 1, 8)
                ))
        );

        employees.add(outdoorTeller);
        employees.add(indoorTeller);
        employees.add(indoorServiceEmployee);
    }

    public int getIndoorQueueCapacity() {
        return indoorQueueCapacity;
    }

    public void setIndoorQueueCapacity(int indoorQueueCapacity) {
        this.indoorQueueCapacity = indoorQueueCapacity;
    }

    public int getOutdoorQueueCapacity() {
        return outdoorQueueCapacity;
    }

    public void setOutdoorQueueCapacity(int outdoorQueueCapacity) {
        this.outdoorQueueCapacity = outdoorQueueCapacity;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public double getCashCustomerProbability() {
        return cashCustomerProbability;
    }

    public void setCashCustomerProbability(double cashCustomerProbability) {
        this.cashCustomerProbability = cashCustomerProbability;
    }

    public double getServiceCustomerProbability() {
        return (1 - cashCustomerProbability);
    }

    public ArrayList<Range> getTimeBetweenArrivalProbability() {
        return timeBetweenArrivalProbability;
    }

    public void setTimeBetweenArrivalProbability(ArrayList<Range> timeBetweenArrivalProbability) {
        this.timeBetweenArrivalProbability = timeBetweenArrivalProbability;
    }

    public List<Employee> getIndoorServiceEmployees() {
        return employees
                .stream()
                .filter(e -> e.getType().equals(Employee.Type.SERVICE) && e.getArea().equals(Employee.Area.INDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Employee> getOutdoorCashEmployees() {
        return employees
                .stream()
                .filter(e -> e.getType().equals(Employee.Type.CASH) && e.getArea().equals(Employee.Area.OUTDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Employee> getIndoorCashEmployees() {
        return employees
                .stream()
                .filter(e -> e.getType().equals(Employee.Type.CASH) && e.getArea().equals(Employee.Area.INDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public double getTimeBetweenArrivalTime(double probability) {
        return timeBetweenArrivalProbability.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }
}
