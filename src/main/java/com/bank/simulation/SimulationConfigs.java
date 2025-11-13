package com.bank.simulation;

import com.bank.models.EmployeeData;
import com.bank.models.Range;

import java.util.*;
import java.util.stream.Collectors;

import static com.bank.models.ServiceType.CASH;
import static com.bank.models.ServiceType.SERVICE;

public class SimulationConfigs {
    public static SimulationConfigs instance = new SimulationConfigs();

    private int outdoorQueueCapacity;
    private int indoorQueueCapacity;
    private double cashCustomerProbability;
    private ArrayList<Range> timeBetweenArrivalRanges;
    private Map<Integer, Double> timeBetweenArrivalProbability;
    private List<EmployeeData> employeeData;

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

        employeeData = new ArrayList<>();
        EmployeeData outdoorTeller = new EmployeeData(
                EmployeeData.Area.OUTDOOR,
                CASH,
                "outdoor_teller_1",
                getDefaultTellerProbability()
        );
        EmployeeData indoorTeller = new EmployeeData(
                EmployeeData.Area.INDOOR,
                CASH,
                "indoor_teller_1",
                getDefaultTellerProbability()
        );

        EmployeeData indoorServiceEmployeeData = new EmployeeData(
                EmployeeData.Area.INDOOR,
                SERVICE,
                "indoor_service_1",
                getDefaultServiceEmployeeProbability()
        );

        employeeData.add(outdoorTeller);
        employeeData.add(indoorTeller);
        employeeData.add(indoorServiceEmployeeData);
    }

    public Map<Integer, Double> getDefaultTellerProbability() {
        return new LinkedHashMap<>() {{
            put(2, 0.2);
            put(3, 0.3);
            put(4, 0.5);
        }};
    }

    public Map<Integer, Double> getDefaultServiceEmployeeProbability() {
        return new LinkedHashMap<>() {{
            put(4, 0.2);
            put(6, 0.5);
            put(8, 0.3);
        }};
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

    public void setEmployees(List<EmployeeData> employeeData) {
        this.employeeData = employeeData;
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

    public List<EmployeeData> getIndoorServiceEmployees() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(SERVICE) && e.getArea().equals(EmployeeData.Area.INDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<EmployeeData> getOutdoorCashEmployees() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(CASH) && e.getArea().equals(EmployeeData.Area.OUTDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<EmployeeData> getIndoorCashEmployees() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(CASH) && e.getArea().equals(EmployeeData.Area.INDOOR))
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

    public int getTimeBetweenArrival(double probability) {
        return timeBetweenArrivalRanges.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }
}
