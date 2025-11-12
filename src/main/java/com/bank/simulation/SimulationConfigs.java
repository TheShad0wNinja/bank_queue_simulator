package com.bank.simulation;

import com.bank.models.Employee;
import com.bank.models.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationConfigs {
    public static SimulationConfigs instance = new SimulationConfigs();

    private int outdoorQueueCapacity;
    private int indoorQueueCapacity;
    private double cashCustomerProbability;
    private ArrayList<Range> timeBetweenArrivalRanges;
    private Map<Integer, Double> timeBetweenArrivalProbability;
    private List<Employee> employees;

    private SimulationConfigs() {
        resetParamsToDefault();
    }

    public void resetParamsToDefault() {
        outdoorQueueCapacity = 2;
        indoorQueueCapacity = Integer.MAX_VALUE;
        cashCustomerProbability = 0.7;

        timeBetweenArrivalProbability = new HashMap<>(Map.of(
                0, 0.15,
                1, 0.25,
                2, 0.25,
                3, 0.35
        ));
        updateTimeBetweenArrivalRanges();

        employees = new ArrayList<>();
        Employee outdoorTeller = new Employee(
                Employee.Area.OUTDOOR,
                Employee.Type.CASH,
                new HashMap<>(Map.of(
                        2, 0.2,
                        3, 0.3,
                        4, 0.5
                ))
        );
        Employee indoorTeller = new Employee(
                Employee.Area.INDOOR,
                Employee.Type.CASH,
                new HashMap<>(Map.of(
                        2, 0.2,
                        3, 0.3,
                        4, 0.5
                ))
        );

        Employee indoorServiceEmployee = new Employee(
                Employee.Area.INDOOR,
                Employee.Type.SERVICE,
                new HashMap<>(Map.of(
                        4, 0.2,
                        6, 0.5,
                        8, 0.3
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

    public Map<Integer, Double> getTimeBetweenArrivalProbability() {
        return timeBetweenArrivalProbability;
    }

    public void setTimeBetweenArrivalProbability(Map<Integer, Double> timeBetweenArrivalProbability) {
        this.timeBetweenArrivalProbability = timeBetweenArrivalProbability;
        updateTimeBetweenArrivalRanges();
    }

    private void updateTimeBetweenArrivalRanges() {
        timeBetweenArrivalRanges = new ArrayList<>();
        double lastProbability = 0.0;
        for (var entry : timeBetweenArrivalProbability.entrySet()) {
            // No need to add onto the last probability because it'll be avoided due to findFirst being used
            timeBetweenArrivalRanges.add(new Range(lastProbability , lastProbability + entry.getValue(), entry.getKey()));
            lastProbability += entry.getValue();
        }

        if (lastProbability > 1)
            throw new ArithmeticException("Time between arrivals probabilities are out of range");
    }

    public double getTimeBetweenArrival(double probability) {
        return timeBetweenArrivalRanges.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }
}
